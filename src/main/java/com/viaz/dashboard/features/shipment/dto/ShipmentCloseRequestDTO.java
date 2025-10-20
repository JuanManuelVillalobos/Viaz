package com.viaz.dashboard.features.shipment.dto;

public record ShipmentCloseRequestDTO(
        String fullViazId,
        Integer carrierRating,
        String carrierObservations,
        Integer clientRating,
        String clientObservations
) {
    public ShipmentCloseRequestDTO(String fullViazId, Integer carrierRating, String carrierObservations, Integer clientRating, String clientObservations) {
        this.fullViazId = fullViazId;
        this.carrierRating = carrierRating;
        this.carrierObservations = carrierObservations;
        this.clientRating = clientRating;
        this.clientObservations = clientObservations;
    }
}
