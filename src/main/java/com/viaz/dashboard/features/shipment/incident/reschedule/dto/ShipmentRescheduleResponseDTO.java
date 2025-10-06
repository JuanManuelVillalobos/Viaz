package com.viaz.dashboard.features.shipment.incident.reschedule.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record ShipmentRescheduleResponseDTO (
    Long incidentId,
    Instant occurredAt,
    LocalDateTime newUnloadDate,
    String comment
) {}

