package com.viaz.dashboard.shipment;

import com.viaz.dashboard.exception.exceptions.ResourceNotFoundException;
import com.viaz.dashboard.s3.S3Service;
import com.viaz.dashboard.shipment.idGenerator.ShipmentIdGeneratorService;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Service
public class ShipmentService {

    private static final Logger log = LoggerFactory.getLogger(ShipmentService.class);
    private final ShipmentRepository shipmentRepository;
    private final ShipmentIdGeneratorService shipmentIdGeneratorService;
    private final S3Service s3Service;

    public ShipmentService(ShipmentRepository shipmentRepository, ShipmentIdGeneratorService shipmentIdGeneratorService, S3Service s3Service) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentIdGeneratorService = shipmentIdGeneratorService;
        this.s3Service = s3Service;
    }


    public Shipment getShipmentById(String viazId) {
        return shipmentRepository.findByViazId(viazId);
    }


    @Cacheable(value = "shipments", key = "'allShipments'")
    public List<Shipment> getShipments() {
        return shipmentRepository.findAll(Sort.by("viazId").descending());
    }

    @Cacheable(value = "shipments", key = "'pendingShipments'")
    public List<Shipment> getPendingShipments() {
        return shipmentRepository.getPendingShipments();
    }

    @CacheEvict(value = "shipments", allEntries = true)
    public Shipment createShipment(Shipment shipment) {
        shipment.setViazId(shipmentIdGeneratorService.generateShipmentId());
        shipment.setYear(Year.now().getValue());
        shipment.setSequence(extractSequenceFromId(shipment.getViazId()));

        // Save the entity
        return shipmentRepository.save(shipment);
    }

    @CacheEvict(value = "shipments", allEntries = true)
    @Transactional
    public void deleteShipment(String viazId) {
        shipmentRepository.deleteByViazID(viazId);
    }

    @CacheEvict(value = "shipments", allEntries = true)
    public Shipment updateShipment(String viazId, Map<String, Object> updates) {
        Shipment existingShipment = getShipmentById(viazId);

        updates.forEach((key, value) -> {
            try {
                Field field = Shipment.class.getDeclaredField(key);
                field.setAccessible(true);
                field.set(existingShipment, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error(e.getMessage());
            }
        });

        // Save the updated shipment back to the database
        shipmentRepository.save(existingShipment);


        return getShipmentById(viazId); // Return the updated shipment
    }


    /**
     * Extracts the sequence number from the shipment ID of the format "vz-[currentYear]-[sequence]"
     *
     * @param viazID the generated shipment ID (e.g., "vz-2025-001")
     * @return the sequence number extracted from the ID
     */
    private int extractSequenceFromId(String viazID) {
        // Split the shipment ID by the "-" character and extract the third part (sequence)
        String[] parts = viazID.split("-");

        // The sequence should be the third part of the split array
        if (parts.length == 3) {
            return Integer.parseInt(parts[2]); // Parse and return the sequence part
        } else {
            throw new IllegalArgumentException("Invalid shipment ID format: " + viazID);
        }
    }

    @CacheEvict(value = "shipments", allEntries = true)
    public ResponseEntity<String> uploadExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip headers
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Shipment shipment = new Shipment();
                Field[] fields = Shipment.class.getDeclaredFields();
                int excelColumnIndex = 0; // Track column index in Excel

                for (Field field : fields) {
                    field.setAccessible(true);

                    // Skip "year" and "sequence" fields
                    if (field.getName().equals("year") || field.getName().equals("sequence")) {
                        continue;
                    }

                    if (excelColumnIndex >= row.getLastCellNum()) break;
                    Cell cell = row.getCell(excelColumnIndex++);

                    try {
                        if (cell != null) {
                            if (field.getType() == LocalDate.class) {
                                // Handle LocalDate separately
                                if (cell.getCellType() == CellType.NUMERIC) {
                                    field.set(shipment, cell.getLocalDateTimeCellValue().toLocalDate());
                                } else if (cell.getCellType() == CellType.STRING) {
                                    field.set(shipment, LocalDate.parse(cell.getStringCellValue(), dateFormatter));
                                }
                            } else if (field.getType() == LocalDateTime.class) {
                                // Handle LocalDateTime separately
                                if (cell.getCellType() == CellType.NUMERIC) {
                                    field.set(shipment, cell.getLocalDateTimeCellValue());
                                } else if (cell.getCellType() == CellType.STRING) {
                                    field.set(shipment, LocalDateTime.parse(cell.getStringCellValue(), dateTimeFormatter));
                                }
                            } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                                // Handle Booleans: If non-empty string, set to true; otherwise, false
                                if (cell.getCellType() == CellType.BOOLEAN) {
                                    field.set(shipment, cell.getBooleanCellValue());
                                } else if (cell.getCellType() == CellType.STRING) {
                                    field.set(shipment, !cell.getStringCellValue().trim().isEmpty());
                                } else {
                                    field.set(shipment, false);
                                }
                            } else {
                                // Convert all numbers to Strings, EXCEPT dates
                                switch (cell.getCellType()) {
                                    case STRING:
                                        if ("viazId".equals(field.getName()) || "container".equals(field.getName())) {
                                            field.set(shipment, cell.getStringCellValue().toUpperCase()); // Set to uppercase if field is viazId
                                        } else {
                                            field.set(shipment, capitalizeFirstLetter(cell.getStringCellValue()));
                                        }
                                        break;
                                    case NUMERIC:
                                        field.set(shipment, String.valueOf((long) cell.getNumericCellValue()));
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    } catch (IllegalAccessException | DateTimeParseException e) {
                        log.error("Error setting field: " + field.getName() + " - " + e.getMessage());
                    }
                }

                shipmentRepository.save(shipment);
            }
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to process the file.");
        }

        return ResponseEntity.ok("File uploaded and shipments saved successfully.");
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public PutObjectResponse uploadFileToShipment(String viazId, MultipartFile file) throws IOException {
        checkIfShipmentExistsOrThrow(viazId);
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }
        String objectKey = viazId + "/" + filename;
        byte[] fileBytes = file.getBytes();
        if (fileBytes.length == 0) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        return s3Service.putObject(objectKey, fileBytes);
    }

    private void checkIfShipmentExistsOrThrow(String viazId) {
        if (!shipmentRepository.existsShipmentByViazId(viazId)) {
            throw new ResourceNotFoundException(
                    "shipment with id " + viazId + " does not exist");
        }
    }

    public MultipartFile getFileFromShipment(String viazId, String filename) {
        return null;
    }

    public String getUploadUrl() {
        return s3Service.getUploadUrl();
    }
}