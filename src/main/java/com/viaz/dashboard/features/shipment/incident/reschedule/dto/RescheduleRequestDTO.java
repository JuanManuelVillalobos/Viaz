package com.viaz.dashboard.features.shipment.incident.reschedule.dto;

import java.time.LocalDateTime;

public record RescheduleRequestDTO(
        LocalDateTime newUnloadTime,
        String comment
) {
}
