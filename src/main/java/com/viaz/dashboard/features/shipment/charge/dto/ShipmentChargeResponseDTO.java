package com.viaz.dashboard.features.shipment.charge.dto;

import com.viaz.dashboard.features.shipment.charge.ShipmentCharge;

import java.time.Instant;
import java.time.LocalDateTime;

public record ShipmentChargeResponseDTO(
        Long shipmentChargeId,
        String shipment,
        String chargeType,
        String comment,
        Instant occurredAt
) {
    public ShipmentChargeResponseDTO(ShipmentCharge shipmentCharge) {
        this(
                shipmentCharge.getShipmentChargeId(),
                shipmentCharge.getShipment() != null ?
                        shipmentCharge.getShipment().getFullViazId() :
                        null,
                shipmentCharge.getChargeType() != null ?
                        shipmentCharge.getChargeType().getName() :
                        null,
                shipmentCharge.getComment(),
                shipmentCharge.getOccurredAt()
        );
    }
}
