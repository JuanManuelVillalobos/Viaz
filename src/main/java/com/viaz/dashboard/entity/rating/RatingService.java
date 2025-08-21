package com.viaz.dashboard.entity.rating;

import com.viaz.dashboard.entity.shipment.Shipment;
import com.viaz.dashboard.entity.shipment.ShipmentRepository;
import com.viaz.dashboard.exception.exceptions.ResourceNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final ShipmentRepository shipmentRepository;

    public ResponseEntity<String> createRating(@NonNull String viazId, @NonNull Integer rating, String observations) {
        Shipment shipment = shipmentRepository.findByViazId(viazId);

        if (shipment == null) {
            throw new ResourceNotFoundException("Shipment not found with viazId: " + viazId);
        }

        Rating newRating = new Rating();
        newRating.setShipmentRating(rating);
        newRating.setRatingObservation(observations);
        newRating.setShipment(shipment);

        ratingRepository.save(newRating);

        return ResponseEntity.ok("Rating created successfully for shipment: " + viazId + " with rating ID: " + newRating.getRatingId());
    }

}
