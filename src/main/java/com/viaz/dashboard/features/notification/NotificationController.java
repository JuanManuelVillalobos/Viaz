package com.viaz.dashboard.features.notification;

import com.viaz.dashboard.features.notification.dto.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    public List<NotificationResponseDTO> getNotificationsById(@PathVariable Long userId) {
        return notificationService.getUserNotifications(userId);
    }

    @PostMapping("/{userId}/mark-all-read")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@PathVariable Long userId) {
        boolean reasult  = notificationService.markAllAsRead(userId);
        if(!reasult) {
            throw new RuntimeException("Something went wrong while marking all notifications as read");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Notifications marked as read successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/mark-read/{notificationId}")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Notification marked as read successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}/unread-count")
    public Long getUnreadCount(@PathVariable Long userId) {
        return notificationService.getUnreadCount(userId);
    }

}
