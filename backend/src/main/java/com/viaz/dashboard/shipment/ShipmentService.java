package com.viaz.dashboard.shipment;

import com.viaz.dashboard.shipment.idGenerator.ShipmentIdGeneratorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;


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

    public Page<Shipment> getShipments(int page, int size) {
        return shipmentRepository.findAll(PageRequest.of(page, size));
    }

    public List<Shipment> getLastFiveShipments() {
        return shipmentRepository.findLastFiveShipments().stream()
                .limit(5)
                .toList();
    }

    public Shipment createShipment(Shipment shipment) {
        shipment.setViazId(shipmentIdGeneratorService.generateShipmentId());
        shipment.setYear(Year.now().getValue());
        shipment.setSequence(extractSequenceFromId(shipment.getViazId()));

        // Save the entity
        return shipmentRepository.save(shipment);
    }

    public void deleteShipment(Long viazId) {
        shipmentRepository.deleteById(viazId);
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
