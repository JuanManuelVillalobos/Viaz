package com.viaz.dashboard.entity.shipment.incident;

import java.time.LocalDateTime;


public record ShipmentIncidentResponseDTO(
        Long shipmentIncidentId,
        String shipment,
        String incidentType,
        String comment,
        LocalDateTime occurredAt
) {
    public ShipmentIncidentResponseDTO(ShipmentIncident shipmentIncident) {
        this(
                shipmentIncident.getShipmentIncidentId(),
                shipmentIncident.getShipment() != null ?
                        shipmentIncident.getShipment().getViazId() :
                        null,
                shipmentIncident.getIncidentType() != null ?
                        shipmentIncident.getIncidentType().getName() :
                        null,
                shipmentIncident.getComment(),
                shipmentIncident.getOccurredAt()
        );
    }
}
