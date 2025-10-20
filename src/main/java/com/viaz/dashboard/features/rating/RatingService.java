package com.viaz.dashboard.features.rating;

import com.viaz.dashboard.features.shipment.Shipment;
import com.viaz.dashboard.features.shipment.ShipmentRepository;
import com.viaz.dashboard.features.shipment.dto.ShipmentCloseRequestDTO;
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
    public Rating createRating(Shipment shipment, Integer customerServiceCarrierRating, String carrierObservations, double carrierRating, Integer customerServiceClientRating, String clientObservations, double clientRating) {

        Rating newRating = new Rating();
//        Set shipment
        newRating.setShipment(shipment);
        newRating.setCarrier(shipment.getTransport());
        newRating.setClient(shipment.getClient());


//            Shipment Related
        newRating.setCarrierRating(carrierRating);
        newRating.setClientRating(clientRating);

//            Customer Service Related
        newRating.setCarrierCustomerServiceRating((double) customerServiceCarrierRating);
        newRating.setClientCustomerServiceRating((double) customerServiceClientRating);

        newRating.setCarrierRatingObservation(carrierObservations);
        newRating.setClientRatingObservation(clientObservations);

        return ratingRepository.save(newRating);
    }

}
