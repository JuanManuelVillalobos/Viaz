package com.viaz.dashboard.features.shipment.dto;

import com.viaz.dashboard.features.shipment.Shipment;

import java.time.format.DateTimeFormatter;

public record ShipmentExcelDTO(
        String fullViazId,
        String status,
        String clientName,
        String transportName,
        String origin,
        String destination,
        String departureDate,
        String arrivalDate,
        String container,
        String customerServiceAgent,
        String typeOfService,
        String transportMethod,
        String containerType,
        String ci,
        String ccp,
        String taxFolio,
        String loadManoeuvre,
        String pisVinculation,
        String open,
        String createdDate
) {
    public ShipmentExcelDTO(Shipment shipment) {
        this(
                shipment.getFullViazId(),
                shipment.getStatus(),
                shipment.getClient() != null ?
                        shipment.getClient().getName() :
                        null,
                shipment.getTransport() != null ? shipment.getTransport().getName() : "",
                shipment.getOrigin(),
                shipment.getDestination(),
                formatDate(shipment.getDepartureDate()),
                formatDateTime(shipment.getArrivalDate()),
                shipment.getContainer(),
                shipment.getCustomerServiceAgent() != null ? shipment.getCustomerServiceAgent().getUsername() : "",
                shipment.getTypeOfService(),
                shipment.getTransportMethod(),
                shipment.getContainerType(),
                toYesNo(shipment.getCi()),
                toYesNo(shipment.getCcp()),
                toYesNo(shipment.getTaxFolio()),
                toYesNo(shipment.getLoadManoeuvre()),
                toYesNo(shipment.getPisVinculation()),
                toYesNo(shipment.getOpen()),
                shipment.getCreatedDate() != null ? shipment.getCreatedDate().toString() : ""
        );
    }

    private static String formatDate(java.time.LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
    }

    private static String formatDateTime(java.time.LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
    }

    private static String toYesNo(Boolean value) {
        return value != null && value ? "Yes" : "No";
    }
}
