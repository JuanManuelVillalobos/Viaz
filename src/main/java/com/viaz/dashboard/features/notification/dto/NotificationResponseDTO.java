package com.viaz.dashboard.features.notification.dto;

import com.viaz.dashboard.features.notification.Notification;

import java.time.Instant;

public record NotificationResponseDTO(
        Long notificationId,
        String message,
        boolean read,
        Instant timestamp
) {
    public NotificationResponseDTO(Notification notification) {
        this(notification.getNotificationId(),
                notification.getMessage(),
                notification.getRead(),
                notification.getTimeStamp()
        );
    }
}
