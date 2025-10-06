package com.viaz.dashboard.features.shipment.incident.dto;

import com.viaz.dashboard.features.shipment.incident.ShipmentIncident;

import java.time.Instant;


public record ShipmentIncidentResponseDTO(
        Long shipmentIncidentId,
        String shipment,
        String incidentType,
        String comment,
        Instant occurredAt
) {
    public ShipmentIncidentResponseDTO(ShipmentIncident shipmentIncident) {
        this(
                shipmentIncident.getShipmentIncidentId(),
                shipmentIncident.getShipment() != null ?
                        shipmentIncident.getShipment().getFullViazId() :
                        null,
                shipmentIncident.getIncidentType() != null ?
                        shipmentIncident.getIncidentType().getName() :
                        null,
                shipmentIncident.getComment(),
                shipmentIncident.getOccurredAt()
        );
    }
}
