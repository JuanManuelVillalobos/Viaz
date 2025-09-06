package com.viaz.dashboard.entity.shipment.charge;

import java.time.LocalDateTime;

public record ShipmentChargeResponseDTO(
        Long shipmentChargeId,
        String shipment,
        String chargeType,
        String comment,
        LocalDateTime occurredAt
) {
    public ShipmentChargeResponseDTO(ShipmentCharge shipmentCharge) {
        this(
                shipmentCharge.getShipmentChargeId(),
                shipmentCharge.getShipment() != null ?
                        shipmentCharge.getShipment().getViazId() :
                        null,
                shipmentCharge.getChargeType() != null ?
                        shipmentCharge.getChargeType().getName() :
                        null,
                shipmentCharge.getComment(),
                shipmentCharge.getOccurredAt()
        );
    }
}
