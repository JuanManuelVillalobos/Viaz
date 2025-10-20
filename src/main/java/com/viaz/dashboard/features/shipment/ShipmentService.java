package com.viaz.dashboard.features.shipment;

import com.viaz.dashboard.features.carrier.Carrier;
import com.viaz.dashboard.features.carrier.CarrierService;
import com.viaz.dashboard.features.charge.ChargeService;
import com.viaz.dashboard.features.charge.ChargeType;
import com.viaz.dashboard.features.client.Client;
import com.viaz.dashboard.features.client.ClientService;
import com.viaz.dashboard.features.incident.IncidentService;
import com.viaz.dashboard.features.incident.IncidentType;
import com.viaz.dashboard.features.notification.NotificationService;
import com.viaz.dashboard.features.rating.Rating;
import com.viaz.dashboard.features.rating.RatingService;
import com.viaz.dashboard.features.shipment.charge.ShipmentCharge;
import com.viaz.dashboard.features.shipment.charge.dto.ShipmentChargeResponseDTO;
import com.viaz.dashboard.features.shipment.charge.ShipmentChargeService;
import com.viaz.dashboard.features.shipment.dto.*;
import com.viaz.dashboard.features.shipment.incident.ShipmentIncident;
import com.viaz.dashboard.features.shipment.incident.dto.ShipmentIncidentResponseDTO;
import com.viaz.dashboard.features.shipment.incident.ShipmentIncidentService;
import com.viaz.dashboard.features.shipment.specification.ShipmentSpecifications;
import com.viaz.dashboard.features.shipment.subscription.ShipmentSubscriptionService;
import com.viaz.dashboard.features.user.UserService;
import com.viaz.dashboard.features.user.ViazUser;
import com.viaz.dashboard.exception.exceptions.ResourceNotFoundException;
import com.viaz.dashboard.features.user.userDetails.ViazUserDetails;
import com.viaz.dashboard.initializers.lists.ChargeList;
import com.viaz.dashboard.initializers.lists.IncidentList;
import com.viaz.dashboard.s3.S3Service;
import com.viaz.dashboard.features.shipment.idGenerator.ShipmentIdGeneratorService;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.viaz.dashboard.features.shipment.specification.ShipmentSpecifications.notDeleted;

@RequiredArgsConstructor
@Service
public class ShipmentService {

    private static final Logger log = LoggerFactory.getLogger(ShipmentService.class);
    private final ShipmentRepository shipmentRepository;
    private final ShipmentIdGeneratorService shipmentIdGeneratorService;
    private final S3Service s3Service;
    private final CarrierService carrierService;
    private final UserService userService;
    private final RatingService ratingService;
    private final ShipmentIncidentService shipmentIncidentService;
    private final IncidentService incidentService;
    private final ShipmentChargeService shipmentChargeService;
    private final ChargeService chargeService;
    private final NotificationService notificationService;
    private final ShipmentSubscriptionService shipmentSubscriptionService;
    private final CacheManager cacheManager;
    private final ClientService clientService;


    @Value("${rating.weight.clientCustomerService}")
    private double clientCustomerServiceRatingWeight;
    
    @Value("${rating.weight.carrierCustomerService}")
    private double carrierCustomerServiceRatingWeight;
    
    @Value("${rating.weight.carrier}")
    private double carrierRatingWeight;
    
    @Value("${rating.weight.client}")
    private double clientRatingWeight;


    @Transactional
    public ShipmentResponseDTO getShipmentByIdDTO(String viazId) {
        Shipment shipment = shipmentRepository.findByFullViazId(viazId).orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));
        return new ShipmentResponseDTO(shipment, carrierRatingWeight, clientRatingWeight, carrierCustomerServiceRatingWeight, clientCustomerServiceRatingWeight);
    }

    //    ONLY USE ON GETTER FUNCTIONS, WHEN MODIFYING ALWAYS GET SHIPMENT FROM REPOSITORY
    public Shipment getShipmentById(String viazId) {
        return shipmentRepository.findByFullViazId(viazId).orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));
    }

    @Cacheable(
            value = "shipmentsPageCache",
            key = "T(java.util.Objects).hash(#page, #size, " +
                    "T(java.util.Optional).ofNullable(#statuses).orElse(T(java.util.Collections).emptyList()).stream().sorted().toList(), " +
                    "T(java.util.Optional).ofNullable(#clients).orElse(T(java.util.Collections).emptyList()).stream().sorted().toList(), " +
                    "T(java.util.Optional).ofNullable(#carriers).orElse(T(java.util.Collections).emptyList()).stream().sorted().toList(), " +
                    "#search != null ? #search : '')"
    )
    public Page<ShipmentResponseDTO> getShipments(
            int page,
            int size,
            List<String> statuses,
            List<String> clients,
            List<String> carriers,
            List<String> customerServiceAgent,
            String search
    ) {
        Specification<Shipment> spec = ShipmentSpecifications.buildSpec(statuses, clients, carriers, customerServiceAgent, search);

        spec = spec.and(notDeleted());

        PageRequest pageable = PageRequest.of(page, size, Sort.by("fullViazId").descending());

        return shipmentRepository.findAll(spec, pageable)
                .map(shipment -> new ShipmentResponseDTO(shipment, carrierRatingWeight, clientRatingWeight, carrierCustomerServiceRatingWeight, clientCustomerServiceRatingWeight));
    }

    @Caching(evict = {
            @CacheEvict(value = "shipmentsPageCache", allEntries = true),
            @CacheEvict(value = "openShipmentsPageCache", allEntries = true),
    })
    public ShipmentResponseDTO createShipment(ShipmentRequestDTO shipmentRequestDTO) {
        Shipment shipment = new Shipment();

        Carrier carrier = null;
        ViazUser viazUser = null;
        Client client = null;
        try {
            carrier = carrierService.getCarrierByNameOrThrow(shipmentRequestDTO.transport());
        } catch (Exception ignored) {
        }

        try {
            viazUser = userService.getUserByName(shipmentRequestDTO.customerServiceAgent());
        } catch (Exception ignored) {
        }

        try {
            client = clientService.getClientByNameOrThrow(shipmentRequestDTO.client());
        } catch (Exception ignored) {
        }

        shipment.setTransport(carrier);
        shipment.setCustomerServiceAgent(viazUser);
        shipment.setClient(client);

        BeanUtils.copyProperties(shipmentRequestDTO, shipment);

        String viazId = shipmentIdGeneratorService.generateShipmentId();

        shipment.setFullViazId(viazId);
        shipment.setBaseViazId(viazId);
        shipment.setYear(Year.now().getValue());
        shipment.setSequence(extractSequenceFromId(shipment.getFullViazId()));
        ShipmentResponseDTO newShipment = new ShipmentResponseDTO(shipmentRepository.save(shipment), carrierRatingWeight, clientRatingWeight, carrierCustomerServiceRatingWeight, clientCustomerServiceRatingWeight);
        log.info("New shipment created: {}", newShipment.fullViazId());
        if (viazUser != null) {
            notificationService.createNotification(viazUser, "Nuevo embarque asignado con id " + shipment.getFullViazId());
            shipmentSubscriptionService.subscribeUserToShipment(viazUser.getUserId(), newShipment.fullViazId());
        }

        shipmentSubscriptionService.notifySubscribers(shipment, "Nuevo embarque creado con id " + shipment.getFullViazId());


        return newShipment;
    }

    @Caching(evict = {
            @CacheEvict(value = "shipmentsPageCache", allEntries = true),
            @CacheEvict(value = "openShipmentsPageCache", allEntries = true),
            @CacheEvict(value = "shipment", key = "#viazId")
    })
    public void deleteShipment(String viazId) {
        Shipment shipment = shipmentRepository.findByFullViazId(viazId).orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));
        shipment.setDeleted(true);
        shipmentRepository.save(shipment);
        log.info("Shipment with ID {} deleted successfully.", viazId);
    }

    @Caching(evict = {
            @CacheEvict(value = "shipmentsPageCache", allEntries = true),
            @CacheEvict(value = "openShipmentsPageCache", allEntries = true),
            @CacheEvict(value = "shipment", key = "#viazId")
    })
    @Transactional
    public ShipmentResponseDTO updateShipment(@NonNull String viazId, Map<String, Object> updates) {
        if (viazId.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipment ID (viazId) cannot be null or empty.");
        }

        Shipment existingShipment = getShipmentById(viazId);

        if (existingShipment == null) {
            throw new ResourceNotFoundException("Shipment with ID " + viazId + " not found.");
        }


        Map<String, Object[]> changes = new HashMap<>(); // key -> [oldValue, newValue]

        Set<String> excludedFields = Set.of(
                "hasIncidents",
                "hasCharges",
                "carrierRating",
                "clientRating",
                "carrierCustomerServiceRating",
                "clientCustomerServiceRating",
                "carrierRatingObservation",
                "clientRatingObservation",
                "shipmentCompleteRating",
                "hasVariants",
                "rating"
        );


        updates.forEach((key, value) -> {
            if (excludedFields.contains(key)) {
                return;
            }
            try {
                Field field = Shipment.class.getDeclaredField(key);
                field.setAccessible(true);

                Object oldValue = field.get(existingShipment);
                Object newValue = null;

                if (value != null) {
                    Class<?> fieldType = field.getType();

                    // Handle specific field types
                    if ("files".equals(field.getName())) {
                        // Assuming files shouldn't be overridden, just skip or reassign
                        newValue = existingShipment.getFiles();
                    } else if (fieldType == ViazUser.class) {
                        if (value instanceof String) {
                            newValue = userService.getUserByName((String) value);
                        } else {
                            log.warn("Field '{}' expects ViazUser, but value is not a String", key);
                        }

                    } else if (fieldType == Carrier.class) {
                        if (value instanceof String) {
                            newValue = carrierService.getCarrierByNameOrThrow((String) value);
                        } else {
                            log.warn("Field '{}' expects Carrier, but value is not a String", key);
                        }

                    } else if (fieldType == Client.class) {
                        if (value instanceof String) {
                            newValue = clientService.getClientByNameOrThrow((String) value);
                        } else {
                            log.warn("Field '{}' expects Client, but value is not a String", key);
                        }

                    } else if (fieldType == LocalDateTime.class) {
                        if (value instanceof String) {
                            try {
                                newValue = LocalDateTime.parse((String) value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                            } catch (DateTimeParseException e) {
                                log.error("Invalid LocalDateTime format for field '{}': {}", key, e.getMessage());
                            }
                        }

                    } else if (fieldType == LocalDate.class) {
                        if (value instanceof String) {
                            try {
                                newValue = LocalDate.parse((String) value, DateTimeFormatter.ISO_LOCAL_DATE);
                            } catch (DateTimeParseException e) {
                                log.error("Invalid LocalDate format for field '{}': {}", key, e.getMessage());
                            }
                        }

                    } else {
                        // Default case for normal fields
                        newValue = value;
                    }

                    // Apply change if it's different
                    if (!Objects.equals(oldValue, newValue)) {
                        field.set(existingShipment, newValue);
                        changes.put(key, new Object[]{oldValue, newValue});
                    }

                } else {
                    // Handle nulls for non-primitives
                    if (!field.getType().isPrimitive()) {
                        if (oldValue != null) {
                            field.set(existingShipment, null);
                            changes.put(key, new Object[]{oldValue, null});
                        }
                    } else {
                        log.warn("Cannot set primitive field '{}' to null", key);
                    }
                }

            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error("Error updating field '{}': {}", key, e.getMessage());
            }
        });

        Object[] customerServiceUserChange = changes.get("customerServiceAgent");
        if (customerServiceUserChange != null) {
            ViazUser oldUser = (ViazUser) customerServiceUserChange[0];
            ViazUser newUser = (ViazUser) customerServiceUserChange[1];

            if (oldUser != null) {
                shipmentSubscriptionService.unsubscribeUserFromShipment(oldUser, existingShipment.getFullViazId());
            }

            if (newUser != null) {
                shipmentSubscriptionService.subscribeUserToShipment(newUser.getUserId(), existingShipment.getFullViazId());
            }
        }


        // Save the updated shipment back to the database
        shipmentRepository.save(existingShipment);


        for (var change : changes.entrySet()) {
            String message = existingShipment.getFullViazId() + " " +
                    change.getKey() + " cambiado de " +
                    (change.getValue()[0] == null ? "N/A" : change.getValue()[0]) + " a " +
                    change.getValue()[1];
            shipmentSubscriptionService.notifySubscribers(existingShipment, message);
        }

        return new ShipmentResponseDTO(getShipmentById(viazId), carrierRatingWeight, clientRatingWeight, carrierCustomerServiceRatingWeight, clientCustomerServiceRatingWeight); // Return the updated shipment
    }


    /**
     * Extracts the sequence number from the shipment ID of the format "vz-[currentYear]-[sequence]"
     *
     * @param viazID the generated shipment ID (e.g., "vz-2025-001")
     * @return the sequence number extracted from the ID
     */
    private int extractSequenceFromId(String viazID) {
        // Split the shipment ID by the "-" character and extract the second part (sequence)
        String[] parts = viazID.split("-");

        // The sequence should be the second part of the split array
        if (parts.length == 2) {
            return Integer.parseInt(parts[1]); // Parse and return the sequence part
        } else {
            throw new IllegalArgumentException("Invalid shipment ID format: " + viazID);
        }
    }

    @CacheEvict(value = "shipment", allEntries = true)
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
                                            field.set(shipment, cell.getStringCellValue().toUpperCase());
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
                        log.error("Error setting field: {} - {}", field.getName(), e.getMessage());
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

    @Caching(evict = {
            @CacheEvict(value = "shipmentsPageCache", allEntries = true),
            @CacheEvict(value = "openShipmentsPageCache", allEntries = true),
            @CacheEvict(value = "shipment", key = "#viazId")
    })
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
        incrementFiles(viazId);
        return s3Service.putObject(objectKey, fileBytes);
    }

    private void checkIfShipmentExistsOrThrow(String viazId) {
        if (!shipmentRepository.existsShipmentByFullViazId(viazId)) {
            throw new ResourceNotFoundException("shipment with id " + viazId + " does not exist");
        }
    }

    public List<String> getDocuments(String viazId) {
        return s3Service.listObjects(viazId);
    }

    public String getFileFromShipment(String viazId, String objectKey) {
        String key = viazId + "/" + objectKey;
        return s3Service.getDownloadUrl(key);
    }

    @Caching(evict = {
            @CacheEvict(value = "shipmentsPageCache", allEntries = true),
            @CacheEvict(value = "openShipmentsPageCache", allEntries = true),
            @CacheEvict(value = "shipment", key = "#viazId")
    })
    public ResponseEntity<String> deleteObject(String viazId, String objectKey) {
        String key = viazId + "/" + objectKey;
        s3Service.deleteObject(key);
        decrementFiles(viazId);
        return ResponseEntity.ok("Object deleted correctly: " + key);
    }

    public void incrementFiles(String viazId) {
        Optional<Shipment> optionalShipment = shipmentRepository.findByFullViazId(viazId);
        shipmentRepository.findByFullViazId(viazId).orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));
        if (optionalShipment.isPresent()) {
            Shipment shipment = optionalShipment.get();
            shipment.incrementNumberOfFiles();
            shipmentRepository.save(shipment);
        } else {
            throw new RuntimeException("Shipment not found");
        }
    }

    public void decrementFiles(String viazId) {
        Optional<Shipment> optionalShipment = shipmentRepository.findByFullViazId(viazId);
        if (optionalShipment.isPresent()) {
            Shipment shipment = optionalShipment.get();
            shipment.decrementNumberOfFiles();
            shipmentRepository.save(shipment);
        } else {
            throw new RuntimeException("Shipment not found");
        }
    }

    @Caching(evict = {
            @CacheEvict(value = "shipmentsPageCache", allEntries = true),
            @CacheEvict(value = "openShipmentsPageCache", allEntries = true),
            @CacheEvict(value = "shipment", key = "#fullViazId")
    })
    @Transactional
    public String closeShipment(String fullViazId, ShipmentCloseRequestDTO request) {
        Integer customerServiceCarrierRating = request.carrierRating();
        String carrierObservations = request.carrierObservations();

        Integer customerServiceClientRating = request.clientRating();
        String clientObservations = request.clientObservations();


        Shipment shipment = getShipmentById(fullViazId);

        if (shipment == null) {
            throw new ResourceNotFoundException("Shipment not found with viazId: " + fullViazId);
        }

        double carrierRating = calculateRating(shipment, IncidentList.TypeOfEntityRelation.CARRIER, ChargeList.TypeOfEntityRelation.CARRIER);
        double clientRating = calculateRating(shipment, IncidentList.TypeOfEntityRelation.CLIENT, ChargeList.TypeOfEntityRelation.CLIENT);

        Rating existingRating = shipment.getRating();

        if (existingRating != null) {
            // Update existing rating
//            Shipment Related
            existingRating.setCarrierRating(carrierRating);
            existingRating.setClientRating(clientRating);

//            Customer Service Related
            existingRating.setCarrierCustomerServiceRating((double) customerServiceCarrierRating);
            existingRating.setClientCustomerServiceRating((double) customerServiceClientRating);

            existingRating.setCarrierRatingObservation(carrierObservations);
            existingRating.setClientRatingObservation(clientObservations);

            ratingService.saveRating(existingRating);
        } else {
            // Create a new rating
            Rating newRating = ratingService.createRating(shipment, customerServiceCarrierRating, carrierObservations, carrierRating , customerServiceClientRating, clientObservations, clientRating);
            shipment.setRating(newRating);
        }

        shipment.close();

        shipmentRepository.save(shipment);
        return shipment.getFullViazId();
    }

    public double calculateRating(Shipment shipment, IncidentList.TypeOfEntityRelation incidentRelation, ChargeList.TypeOfEntityRelation chargeRelation) {
        double maxRating = 10;
        final double MAX_TOTAL_PENALTY = 10.0;
        double totalIncidentPenalty = shipmentIncidentService.getIncidentsByShipmentTypeOfEntity(shipment, incidentRelation)
                .stream().mapToDouble(
                        incident -> incident.getIncidentType().getPenalty()
                ).sum();

        double totalChargePenalty = shipmentChargeService.getChargesByShipmentTypeOfEntity(shipment, chargeRelation)
                .stream().mapToDouble(
                        incident -> incident.getChargeType().getPenalty()
                ).sum();


        double totalPenalty = totalIncidentPenalty + totalChargePenalty;

        double cappedPenalty = Math.min(totalPenalty, MAX_TOTAL_PENALTY);

        return maxRating - cappedPenalty;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "shipmentsPageCache", allEntries = true),
            @CacheEvict(value = "openShipmentsPageCache", allEntries = true),
            @CacheEvict(value = "shipment", key = "#viazId")
    })
    public void addIncidentToShipment(@NonNull String viazId, @NonNull Long incidentId, @NonNull String comment) {
        Shipment shipment = getShipmentById(viazId);
        IncidentType incidentType = incidentService.getIncidentByIdOrThrow(incidentId);
        ShipmentIncident incident = shipmentIncidentService.saveCarrierIncident(
                new ShipmentIncident(shipment, incidentType, comment)
        );
        shipment.getIncidents().add(incident);
        shipmentRepository.save(shipment);
    }


    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "shipmentsPageCache", allEntries = true),
            @CacheEvict(value = "openShipmentsPageCache", allEntries = true),
            @CacheEvict(value = "shipment", key = "#viazId")
    })
    public void addChargeToShipment(@NonNull String viazId, @NonNull Long chargeId, @NonNull String comment) {
        Shipment shipment = getShipmentById(viazId);
        ChargeType chargeType = chargeService.getChargeByIdOrThrow(chargeId);
        ShipmentCharge charge = shipmentChargeService.saveCharge(
                new ShipmentCharge(shipment, chargeType, comment)
        );
        shipment.getCharges().add(charge);
        shipmentRepository.save(shipment);
    }

    @Caching(evict = {
            @CacheEvict(value = "shipmentsPageCache", allEntries = true),
            @CacheEvict(value = "openShipmentsPageCache", allEntries = true),
            @CacheEvict(value = "shipment", key = "#viazId")
    })
    @Transactional
    public void reopenShipment(@NonNull String viazId) {
        Shipment shipment = getShipmentById(viazId);
        shipment.open();
        shipmentRepository.save(shipment);
    }


    @Transactional
    public List<ShipmentIncidentResponseDTO> getIncidentsByViazId(String viazId) {
        Shipment shipment = getShipmentById(viazId);

        return shipmentIncidentService.getIncidentsByShipment(shipment).stream().map(ShipmentIncidentResponseDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public List<ShipmentChargeResponseDTO> getChargesByViazId(String viazId) {
        Shipment shipment = getShipmentById(viazId);
        return shipmentChargeService.getChargesByShipment(shipment).stream().map(ShipmentChargeResponseDTO::new).collect(Collectors.toList());
    }

    public ShipmentFilterOptionsDTO getFilterOptions() {
        List<String> statuses = shipmentRepository.findDistinctStatuses();
        List<String> clients = shipmentRepository.findDistinctClients();
        List<String> carriers = shipmentRepository.findDistinctCarriers();

        return new ShipmentFilterOptionsDTO(statuses, clients, carriers);
    }


    @Transactional
    public void deleteShipmentIncident(Long incidentId) {
        ShipmentIncident incident = shipmentIncidentService.getIncidentById(incidentId);
        if (incident == null) {
            throw new ResourceNotFoundException("ShipmentIncident not found with id: " + incidentId);
        }

        String viazId = incident.getShipment().getFullViazId();

        Shipment shipment = shipmentRepository.findByFullViazId(viazId).orElseThrow(() -> new ResourceNotFoundException("Shipment not found for incident id: " + incidentId));

        shipment.getIncidents().remove(incident);

        shipmentRepository.save(shipment);

//        Cache using local variable ViazID
        if (cacheManager.getCache("shipment") != null) {
            Objects.requireNonNull(cacheManager.getCache("shipment")).evict(viazId);
        }
        if (cacheManager.getCache("shipmentsPageCache") != null) {
            Objects.requireNonNull(cacheManager.getCache("shipmentsPageCache")).clear();
        }
    }


    @Transactional
    public void deleteShipmentCharge(Long chargeId) {
        ShipmentCharge charge = shipmentChargeService.getChargeById(chargeId);
        if (charge == null) {
            throw new ResourceNotFoundException("ShipmentCharge not found with id: " + chargeId);
        }

        String viazId = charge.getShipment().getFullViazId();

        Shipment shipment = shipmentRepository.findByFullViazId(viazId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found for incident id: " + chargeId));

        shipment.getCharges().remove(charge);

        shipmentRepository.save(shipment);

//        Cache using local variable ViazID
        if (cacheManager.getCache("shipment") != null) {
            Objects.requireNonNull(cacheManager.getCache("shipment")).evict(viazId);
        }
        if (cacheManager.getCache("shipmentsPageCache") != null) {
            Objects.requireNonNull(cacheManager.getCache("shipmentsPageCache")).clear();
        }
    }


    public void notifyAdminsOfDelayedUnload() {
        // Calculate threshold: 10 hours ago from now
        Instant tenHoursAgoInstant = Instant.now().minus(10, ChronoUnit.HOURS);

        LocalDateTime tenHoursAgo = LocalDateTime.ofInstant(tenHoursAgoInstant, ZoneId.systemDefault());


        // Get shipments where arrival date was 10+ hours ago and no unload yet
        List<Shipment> delayedShipments = shipmentRepository.findShipmentsWithArrivalOverdueAndNoUnload(tenHoursAgo);

        if (delayedShipments.isEmpty()) return; // no need to proceed if empty

        // Get all admins
        List<ViazUser> admins = userService.getAllUsersByRole("ADMIN");

        // Send notification for each shipment to each admin
        for (Shipment shipment : delayedShipments) {
            String message = "Embarque " + shipment.getFullViazId() + " ha llegado hace más de 10 horas y no se ha descargado.";

            for (ViazUser admin : admins) {
                notificationService.createNotification(admin, message);
            }
        }
    }

    @Caching(evict = {
            @CacheEvict(value = "shipmentsPageCache", allEntries = true),
            @CacheEvict(value = "openShipmentsPageCache", allEntries = true),
            @CacheEvict(value = "shipment", key = "#viazId")
    })
    public void changeStatusOfShipment(String viazId, String status) {
        Shipment shipment = shipmentRepository.findByFullViazId(viazId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not with viazId: " + viazId));


        shipment.setStatus(status);
        shipmentRepository.save(shipment);
    }

    @Caching(evict = {
            @CacheEvict(value = "shipmentsPageCache", allEntries = true),
            @CacheEvict(value = "openShipmentsPageCache", allEntries = true),
            @CacheEvict(value = "shipment", key = "#viazId")
    })
    public String createVariantOfShipment(String viazId) {
        Shipment baseShipment = shipmentRepository.findByFullViazId(viazId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with viazId: " + viazId));

        String suffix = calculateNextSuffix(baseShipment);

        Shipment variant = createVariantFromBase(baseShipment);
        variant.setFullViazId(baseShipment.getBaseViazId() + suffix);

        Shipment saved = shipmentRepository.save(variant);
        return saved.getFullViazId();
    }


    private Shipment createVariantFromBase(Shipment base) {
        Shipment variant = new Shipment();

        // Required linkage
        variant.setBaseShipment(base);
        variant.setBaseViazId(base.getBaseViazId());

        // ID-related
        variant.setYear(base.getYear());
        variant.setSequence(base.getSequence());

        // Likely to stay the same
        variant.setCustomerServiceAgent(base.getCustomerServiceAgent());
        variant.setTypeOfService(base.getTypeOfService());
        variant.setClient(base.getClient());
        variant.setTransportMethod(base.getTransportMethod());
        variant.setContainer(base.getContainer());
        variant.setContainerType(base.getContainerType());
        variant.setCi(base.getCi());
        variant.setCcp(base.getCcp());
        variant.setTaxFolio(base.getTaxFolio());
        variant.setLoadManoeuvre(base.getLoadManoeuvre());
        variant.setPisVinculation(base.getPisVinculation());

        // Status
        variant.setStatus("Información Pendiente");

        return variant;
    }


    private String calculateNextSuffix(Shipment baseShipment) {
        List<Shipment> variants = shipmentRepository.findByBaseShipment(baseShipment);
        if (variants.isEmpty()) return "A";
        // Determine next suffix (e.g., "B", "C", ...)
        char last = variants.stream()
                .map(s -> s.getFullViazId().substring(s.getFullViazId().length() - 1))
                .max(String::compareTo)
                .orElse("A").charAt(0);
        return String.valueOf((char) (last + 1));
    }

    public List<String> getAllVariants(String fullViazId) {
        Shipment base = shipmentRepository.findBaseShipmentByFullViazId(fullViazId);
        return shipmentRepository.findByBaseShipment(base).stream().map(Shipment::getFullViazId).collect(Collectors.toList());
    }

    @Cacheable(
            value = "openShipmentsPageCache",
            key = "T(java.util.Objects).hash(#page, #size, " +
                    "T(java.util.Optional).ofNullable(#clients).orElse(T(java.util.Collections).emptyList()).stream().sorted().toList(), " +
                    "T(java.util.Optional).ofNullable(#carriers).orElse(T(java.util.Collections).emptyList()).stream().sorted().toList(), " +
                    "#search != null ? #search : '')"
    )
    public Page<ShipmentResponseDTO> getOpenShipments(
            int page,
            int size,
            List<String> statuses,
            List<String> clients,
            List<String> carriers,
            List<String> customerServiceAgent,
            String search
    ) {
        Specification<Shipment> baseSpec = ShipmentSpecifications.buildSpec(statuses, clients, carriers, customerServiceAgent, search);

        Specification<Shipment> openSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("open"));  // filter where open == true

        Specification<Shipment> combinedSpec = baseSpec == null ? openSpec : baseSpec.and(openSpec);

        combinedSpec = combinedSpec.and(notDeleted());


        PageRequest pageable = PageRequest.of(page, size, Sort.by("fullViazId").descending());

        return shipmentRepository.findAll(combinedSpec, pageable)
                .map(shipment -> new ShipmentResponseDTO(shipment, carrierRatingWeight, clientRatingWeight, carrierCustomerServiceRatingWeight, clientCustomerServiceRatingWeight));
    }

    public void notifyCustomerServiceThatCleaningManuovreIsPending() {
        shipmentRepository.findAllByCleaningOrderIsIgnoreCase("pendiente").forEach(shipment -> {
            notificationService.createNotification(shipment.getCustomerServiceAgent(), "Embarque " + shipment.getFullViazId() + " pendiente de maniobra de vacío");
        });
    }

    public byte[] exportShipmentsToExcel(
            List<String> statuses,
            List<String> clients,
            List<String> carriers,
            String idFrom,
            String idTo
    ) throws IOException {

        // Step 1: Query
        Specification<Shipment> spec = ShipmentSpecifications.buildExcelSpec(statuses, clients, carriers, idFrom, idTo);
        List<Shipment> shipments = shipmentRepository.findAll(spec);

        // Step 2: Map to DTOs
        List<ShipmentExcelDTO> dtoList = shipments.stream()
                .map(ShipmentExcelDTO::new)
                .toList();


        // Step 3: Export to Excel
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Shipments");

            // Define header
            List<String> headers = Arrays.asList(
                    "Shipment ID", "Status", "Client", "Carrier", "Origin", "Destination",
                    "Departure Date", "Arrival Date", "Container", "Agent", "Type of Service",
                    "Transport Method", "Container Type", "CI", "CCP", "Tax Folio", "Load Manoeuvre",
                    "PIS Vinculation", "Open", "Created Date"
            );

            // Write header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                headerRow.createCell(i).setCellValue(headers.get(i));
            }

            // Write data
            int rowNum = 1;
            for (ShipmentExcelDTO dto : dtoList) {
                Row row = sheet.createRow(rowNum++);
                int cellIdx = 0;
                row.createCell(cellIdx++).setCellValue(dto.fullViazId());
                row.createCell(cellIdx++).setCellValue(dto.status());
                row.createCell(cellIdx++).setCellValue(dto.clientName());
                row.createCell(cellIdx++).setCellValue(dto.transportName());
                row.createCell(cellIdx++).setCellValue(dto.origin());
                row.createCell(cellIdx++).setCellValue(dto.destination());
                row.createCell(cellIdx++).setCellValue(dto.departureDate());
                row.createCell(cellIdx++).setCellValue(dto.arrivalDate());
                row.createCell(cellIdx++).setCellValue(dto.container());
                row.createCell(cellIdx++).setCellValue(dto.customerServiceAgent());
                row.createCell(cellIdx++).setCellValue(dto.typeOfService());
                row.createCell(cellIdx++).setCellValue(dto.transportMethod());
                row.createCell(cellIdx++).setCellValue(dto.containerType());
                row.createCell(cellIdx++).setCellValue(dto.ci());
                row.createCell(cellIdx++).setCellValue(dto.ccp());
                row.createCell(cellIdx++).setCellValue(dto.taxFolio());
                row.createCell(cellIdx++).setCellValue(dto.loadManoeuvre());
                row.createCell(cellIdx++).setCellValue(dto.pisVinculation());
                row.createCell(cellIdx++).setCellValue(dto.open());
                row.createCell(cellIdx++).setCellValue(dto.createdDate());
            }


            // Auto-size columns
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Output to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }


    @Caching(evict = {
            @CacheEvict(value = "shipmentsPageCache", allEntries = true),
            @CacheEvict(value = "openShipmentsPageCache", allEntries = true),
            @CacheEvict(value = "shipment", key = "#viazId")
    })
    public void restoreShipment(String viazId) {
        Shipment shipment = shipmentRepository.findByFullViazId(viazId).orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));
        shipment.setDeleted(false);
        shipmentRepository.save(shipment);
        log.info("Shipment with ID {} restored successfully.", viazId);
    }

    @Cacheable(
            value = "assignedOpenShipmentsCache",
            key = "T(java.util.Objects).hash(#page, #size, " +
                    "T(java.util.Optional).ofNullable(#clients).orElse(T(java.util.Collections).emptyList()).stream().sorted().toList(), " +
                    "T(java.util.Optional).ofNullable(#carriers).orElse(T(java.util.Collections).emptyList()).stream().sorted().toList(), " +
                    "#search != null ? #search : '')"
    )
    public Page<ShipmentResponseDTO> getAssignedShipments(
            int page,
            int size,
            List<String> statuses,
            List<String> clients,
            List<String> carriers,
            List<String> customerServiceAgent,
            String search
    ) {
        ViazUserDetails currentUserDetails = (ViazUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ViazUser currentUser = currentUserDetails.viazUser();

        // Optional filters like status, client, carrier, search
        Specification<Shipment> baseSpec = ShipmentSpecifications.buildSpec(statuses, clients, carriers, customerServiceAgent, search);

        // Only open shipments
        Specification<Shipment> openSpec = (root, query, cb) ->
                cb.isTrue(root.get("open"));

        // Only those assigned to the current user
        Specification<Shipment> assignedToCurrentUser = (root, query, cb) ->
                cb.equal(root.get("customerServiceAgent"), currentUser);

        // Not deleted shipments
        Specification<Shipment> notDeletedSpec = notDeleted();

        // Combine specifications using .and chaining (without Specification.where)
        Specification<Shipment> combinedSpec = openSpec
                .and(assignedToCurrentUser)
                .and(notDeletedSpec);

        if (baseSpec != null) {
            combinedSpec = combinedSpec.and(baseSpec);
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by("fullViazId").descending());

        return shipmentRepository.findAll(combinedSpec, pageable)
                .map(shipment -> new ShipmentResponseDTO(shipment, carrierRatingWeight, clientRatingWeight, carrierCustomerServiceRatingWeight, clientCustomerServiceRatingWeight));

    }
}