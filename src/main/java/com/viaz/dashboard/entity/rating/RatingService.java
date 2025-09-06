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

    public Rating saveRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    public Rating createRating(@NonNull Shipment shipment, @NonNull Integer customerServiceRating, @NonNull Integer shipmentRating, String observations) {

        Rating newRating = new Rating();
//        Set Rating based on incidences
        newRating.setShipmentRating(shipmentRating);

//        Set Rating based on customer service
        newRating.setCustomerServiceRating(customerServiceRating);

        newRating.setRatingObservation(observations);

        newRating.setShipment(shipment);


        return ratingRepository.save(newRating);
    }

}
