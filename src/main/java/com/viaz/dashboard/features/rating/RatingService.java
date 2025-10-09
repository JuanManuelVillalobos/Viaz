package com.viaz.dashboard.features.rating;

import com.viaz.dashboard.features.shipment.Shipment;
import com.viaz.dashboard.features.shipment.ShipmentRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final ShipmentRepository shipmentRepository;

    public Rating saveRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    @CacheEvict(value = "monthlyRatings", allEntries = true)
    public Rating createRating(@NonNull Shipment shipment, @NonNull Double customerServiceRating, @NonNull Double shipmentRating, String observations) {

        Rating newRating = new Rating();
//        Set Rating based on incidences
        newRating.setShipmentRating(shipmentRating);

//        Set Rating based on customer service
        newRating.setCustomerServiceRating(customerServiceRating);

        newRating.setRatingObservation(observations);

        newRating.setShipment(shipment);

        newRating.setCarrier(shipment.getTransport());


        return ratingRepository.save(newRating);
    }

}
