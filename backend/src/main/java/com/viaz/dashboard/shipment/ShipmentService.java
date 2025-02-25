package com.viaz.dashboard.shipment;

import com.viaz.dashboard.shipment.idGenerator.ShipmentIdGeneratorService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.Year;
import java.util.List;
import java.util.Map;


@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentIdGeneratorService shipmentIdGeneratorService;

    public ShipmentService(ShipmentRepository shipmentRepository, ShipmentIdGeneratorService shipmentIdGeneratorService) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentIdGeneratorService = shipmentIdGeneratorService;
    }


    public Shipment getShipmentById(String viazId) {
        return shipmentRepository.findByViazId(viazId);
    }


    public List<Shipment> getShipments() {
        return shipmentRepository.findAll(Sort.by("viazId").descending());
    }

    public List<Shipment> getPendingShipments() {
        return shipmentRepository.getPendingShipments();
    }

    public Page<Shipment> getShipments(int page, int size) {
        return shipmentRepository.findAll(PageRequest.of(page, size));
    }

    public Shipment createShipment(Shipment shipment) {
        shipment.setViazId(shipmentIdGeneratorService.generateShipmentId());
        shipment.setYear(Year.now().getValue());
        shipment.setSequence(extractSequenceFromId(shipment.getViazId()));

        // Save the entity
        return shipmentRepository.save(shipment);
    }

    @Transactional
    public void deleteShipment(String viazId) {
        shipmentRepository.deleteByViazID(viazId);
    }


    public Shipment updateShipment(String viazId, Map<String, Object> updates) {
        Shipment existingShipment = getShipmentById(viazId);

        updates.forEach((key, value) -> {
            try {
                Field field = Shipment.class.getDeclaredField(key);
                field.setAccessible(true);
                field.set(existingShipment, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // TODO: Handle Error (e.g., log the error or throw a custom exception)
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
}
