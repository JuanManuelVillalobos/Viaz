package com.viaz.dashboard.features.shipment.dto;

import com.viaz.dashboard.features.carrier.dto.CarrierResponseDTO;
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
        String client,
        String origin,
        String customsAgent,
        String destination,
        String status,
        String container,
        String containerType,
        LocalDateTime shipmentDate,
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
        String cleaningOrder,
        String pod,
        String cleaningPatio,
        Boolean hasCleaningAppointment,
        LocalDateTime cleaningAppointmentDate,
        LocalDate cleanArrivalDate,
        String eir,
        String observations,
        Integer files,
        Boolean open,
        Boolean hasIncidents,
        Boolean hasCharges,
        Double rating,
        Double shipmentRating,
        Double customerServiceRating,
        String ratingObservation,
        Boolean hasVariants,
        Boolean hasReschedule,
        Boolean deleted
) {


    public ShipmentResponseDTO(Shipment shipment, double customerServiceRatingWeight, double shipmentRatingWeight) {
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
                shipment.getClient(),
                shipment.getOrigin(),
                shipment.getCustomsAgent(),
                shipment.getDestination(),
                shipment.getStatus(),
                shipment.getContainer(),
                shipment.getContainerType(),
                shipment.getShipmentDate(),
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
                shipment.getCleaningOrder(),
                shipment.getPod(),
                shipment.getCleaningPatio(),
                shipment.getHasCleaningAppointment(),
                shipment.getCleaningAppointmentDate(),
                shipment.getCleanArrivalDate(),
                shipment.getEir(),
                shipment.getObservations(),
                shipment.getFiles(),
                shipment.getOpen(),
                shipment.getIncidents() != null && !shipment.getIncidents().isEmpty(),
                shipment.getCharges() != null && !shipment.getCharges().isEmpty(),
                calculateCombinedRating(shipment, customerServiceRatingWeight, shipmentRatingWeight),
                calculateShipmentRating(shipment),
                calculateCustomerServiceRating(shipment),
                getRatingObservations(shipment),
                shipment.getVariants() != null && !shipment.getVariants().isEmpty(),
                shipment.getIncidents() != null &&
                        shipment.getIncidents().stream()
                                .anyMatch(incident -> IncidentList.LIBERACION_DE_DESPACHO_FALLIDA.getName().equals(incident.getIncidentType().getName())
                                        && incident.getRescheduleDetail() != null),
                shipment.isDeleted()
        );
    }

    private static Double calculateCombinedRating(Shipment shipment, double customerServiceRatingWeight, double shipmentRatingWeight) {
        Rating rating = shipment.getRating();
        if (rating == null) return null;
        double customerServiceRating = rating.getCustomerServiceRating() != null ? rating.getCustomerServiceRating() : 0.0;
        double shipmentRating = rating.getShipmentRating() != null ? rating.getShipmentRating() : 0.0;
        return customerServiceRating * customerServiceRatingWeight + shipmentRating * shipmentRatingWeight;
    }

    private static Double calculateShipmentRating(Shipment shipment) {
        Rating rating = shipment.getRating();
        return rating != null ? rating.getShipmentRating() : null;
    }

    private static Double calculateCustomerServiceRating(Shipment shipment) {
        Rating rating = shipment.getRating();
        return rating != null ? rating.getCustomerServiceRating() : null;
    }

    private static String getRatingObservations(Shipment shipment) {
        Rating rating = shipment.getRating();
        return rating != null ? rating.getRatingObservation() : null;
    }
}
