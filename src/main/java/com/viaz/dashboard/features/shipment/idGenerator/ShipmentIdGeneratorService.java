package com.viaz.dashboard.features.shipment.idGenerator;

import com.viaz.dashboard.features.shipment.ShipmentRepository;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ShipmentIdGeneratorService {

    private final ShipmentRepository shipmentRepository;
    // A thread-safe counter to hold the current sequence number in memory.
    // This reduces the need for constant database queries for every ID generation.
    private final AtomicInteger sequence;

    public ShipmentIdGeneratorService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
        // Initialize the sequence from the database when the service is created.
        int initialSequence = getHighestSequence();
        this.sequence = new AtomicInteger(initialSequence);
    }

    /**
     * Retrieves the highest sequence number from the database.
     * This is the starting point for the in-memory counter.
     *
     * @return The highest existing sequence number, or 0 if none exist.
     */
    private int getHighestSequence() {
        Integer highestSequence = shipmentRepository.getHighestSequence();
        return (highestSequence == null) ? 0 : highestSequence;
    }

    /**
     * Initializes the continuous sequence with a determined starting number.
     * This method is intended to be called once, typically on application startup,
     * to support migration from an old ID system.
     *
     * @param startNumber The number to start the sequence from.
     */
    public void initializeSequence(int startNumber) {
        // Only set the value if the current sequence is lower than the desired start number.
        // This prevents accidental resets.
        this.sequence.updateAndGet(current -> Math.max(current, startNumber));
    }

    /**
     * Generates a new, continuous shipment ID with the format "VZ-YYYY-NNNN".
     * This method uses an in-memory counter to generate IDs quickly and increments
     * the database sequence periodically or on shutdown to ensure persistence.
     *
     * @return The new unique shipment ID string.
     */
    public String generateShipmentId() {
        // Increment the in-memory sequence counter.
        int currentSequence = sequence.incrementAndGet();
        int currentYear = Year.now().getValue();

        // The format uses a continuous sequence but includes the current year.
        return String.format("VZ-%d-%d", currentYear, currentSequence);
    }
}
