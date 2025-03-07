package com.viaz.dashboard.shipment.idGenerator;

import com.viaz.dashboard.shipment.ShipmentRepository;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
public class ShipmentIdGeneratorService {

    private final ShipmentRepository shipmentRepository;

    public ShipmentIdGeneratorService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    private int getYear() {
        return Year.now().getValue();
    }

    public String generateShipmentId() {
        int currentYear = getYear();
        int currentSequence = getNextSequence(currentYear);

        return String.format("VZ-%d-%03d", currentYear, currentSequence);
    }

    private int getNextSequence(int currentYear) {
        // Get the highest sequence for the current year
        Integer highestSequence = shipmentRepository.getHighestSequenceForYear(currentYear);
        return (highestSequence == null) ? 1 : highestSequence + 1;
    }
}
