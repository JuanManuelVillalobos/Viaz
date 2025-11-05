package com.viaz.dashboard.features.shipment.dto;

import com.viaz.dashboard.features.carrier.dto.CarrierResponseDTO;
import com.viaz.dashboard.features.client.dto.ClientResponseDTO;
import com.viaz.dashboard.features.rating.Rating;
import com.viaz.dashboard.features.shipment.Shipment;
import com.viaz.dashboard.features.user.dto.UserSummaryDTO;
import com.viaz.dashboard.initializers.lists.IncidentList;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ShipmentResponseDTO(
        String baseViazId,
        String fullViazId,
        Integer year,
        Integer sequence,
        String transportMethod,
        UserSummaryDTO customerServiceAgent,
        String typeOfService,
        ClientResponseDTO client,
        String origin,
        String customsAgent,
        String destination,
        String status,
        String container,
        String containerType,
        LocalDateTime shipmentLoadingDate,
        CarrierResponseDTO transport,
        String foreignReference,
        String terminal,
        Boolean ci,
        Boolean ccp,
        Boolean taxFolio,
        Boolean loadManoeuvre,
        Boolean pisVinculation,
        LocalDate departureDate,
        LocalDateTime arrivalDate,
        LocalDateTime unloadDate,
        String emptyingOrder,
        String pod,
        String emptyingPatio,
        Boolean hasEmptyingAppointment,
        LocalDateTime emptyingAppointmentDate,
        LocalDate emptyArrivalDate,
        String eir,
        String observations,
        Integer files,
        Boolean open,
        Boolean hasIncidents,
        Boolean hasCharges,
        Double carrierRating,
        Double clientRating,
        Double carrierCustomerServiceRating,
        Double clientCustomerServiceRating,
        String carrierRatingObservation,
        String clientRatingObservation,
        Double shipmentCompleteRating,
        Boolean hasVariants,
        Boolean hasReschedule,
        Boolean deleted
) {


    public ShipmentResponseDTO(
            Shipment shipment,
            double carrierRatingWeight,
            double clientRatingWeight,
            double carrierCustomerServiceRatingWeight,
            double clientCustomerServiceRatingWeight
    ) {
        this(
                shipment.getBaseViazId(),
                shipment.getFullViazId(),
                shipment.getYear(),
                shipment.getSequence(),
                shipment.getTransportMethod(),
                shipment.getCustomerServiceAgent() != null ?
                        new UserSummaryDTO(shipment.getCustomerServiceAgent().getUserId(), shipment.getCustomerServiceAgent().getUsername()) :
                        null,
                shipment.getTypeOfService(),
                shipment.getClient() != null ?
                        new ClientResponseDTO(shipment.getClient().getClientId(), shipment.getClient().getName()) :
                        null,
                shipment.getOrigin(),
                shipment.getCustomsAgent(),
                shipment.getDestination(),
                shipment.getStatus(),
                shipment.getContainer(),
                shipment.getContainerType(),
                shipment.getShipmentLoadingDate(),
                shipment.getTransport() != null ?
                        new CarrierResponseDTO(shipment.getTransport().getCarrierId(), shipment.getTransport().getName()) :
                        null,
                shipment.getForeignReference(),
                shipment.getTerminal(),
                shipment.getCi(),
                shipment.getCcp(),
                shipment.getTaxFolio(),
                shipment.getLoadManoeuvre(),
                shipment.getPisVinculation(),
                shipment.getDepartureDate(),
                shipment.getArrivalDate(),
                shipment.getUnloadDate(),
                shipment.getEmptyingOrder(),
                shipment.getPod(),
                shipment.getEmptyingPatio(),
                shipment.getHasEmptyingAppointment(),
                shipment.getEmptyingAppointmentDate(),
                shipment.getEmptyArrivalDate(),
                shipment.getEir(),
                shipment.getObservations(),
                shipment.getFiles(),
                shipment.getOpen(),
                shipment.getIncidents() != null && !shipment.getIncidents().isEmpty(),
                shipment.getCharges() != null && !shipment.getCharges().isEmpty(),
                getCarrierRating(shipment),
                getClientRating(shipment),
                getCarrierCustomerServiceRating(shipment),
                getClientCustomerServiceRating(shipment),
                getCarrierRatingObservation(shipment),
                getClientRatingObservation(shipment),
                calculateCombinedRating(shipment, carrierRatingWeight, clientRatingWeight, carrierCustomerServiceRatingWeight, clientCustomerServiceRatingWeight),
                shipment.getVariants() != null && !shipment.getVariants().isEmpty(),
                shipment.getIncidents() != null &&
                        shipment.getIncidents().stream()
                                .anyMatch(incident -> IncidentList.LIBERACION_DE_DESPACHO_FALLIDA.getName().equals(incident.getIncidentType().getName())
                                        && incident.getRescheduleDetail() != null),
                shipment.isDeleted()
        );
    }

    private static Double calculateCombinedRating(
            Shipment shipment,
            double carrierRatingWeight,
            double clientRatingWeight,
            double carrierCustomerServiceRatingWeight,
            double clientCustomerServiceRatingWeight
    ) {
        Rating rating = shipment.getRating();
        if (rating == null) return null;

        double totalRating = 0.0;
        double totalWeight = 0.0;

        if (rating.getCarrierRating() != null) {
            totalRating += rating.getCarrierRating() * carrierRatingWeight;
            totalWeight += carrierRatingWeight;
        }
        if (rating.getClientRating() != null) {
            totalRating += rating.getClientRating() * clientRatingWeight;
            totalWeight += clientRatingWeight;
        }
        if (rating.getCarrierCustomerServiceRating() != null) {
            totalRating += rating.getCarrierCustomerServiceRating() * carrierCustomerServiceRatingWeight;
            totalWeight += carrierCustomerServiceRatingWeight;
        }
        if (rating.getClientCustomerServiceRating() != null) {
            totalRating += rating.getClientCustomerServiceRating() * clientCustomerServiceRatingWeight;
            totalWeight += clientCustomerServiceRatingWeight;
        }

        return totalWeight > 0 ? totalRating / totalWeight : null;
    }

    private static Double getCarrierRating(Shipment shipment) {
        Rating rating = shipment.getRating();
        return rating != null ? rating.getCarrierRating() : null;
    }

    private static Double getClientRating(Shipment shipment) {
        Rating rating = shipment.getRating();
        return rating != null ? rating.getClientRating() : null;
    }

    private static Double getCarrierCustomerServiceRating(Shipment shipment) {
        Rating rating = shipment.getRating();
        return rating != null ? rating.getCarrierCustomerServiceRating() : null;
    }

    private static Double getClientCustomerServiceRating(Shipment shipment) {
        Rating rating = shipment.getRating();
        return rating != null ? rating.getClientCustomerServiceRating() : null;
    }

    private static String getCarrierRatingObservation(Shipment shipment) {
        Rating rating = shipment.getRating();
        return rating != null ? rating.getCarrierRatingObservation() : null;
    }

    private static String getClientRatingObservation(Shipment shipment) {
        Rating rating = shipment.getRating();
        return rating != null ? rating.getClientRatingObservation() : null;
    }
}
