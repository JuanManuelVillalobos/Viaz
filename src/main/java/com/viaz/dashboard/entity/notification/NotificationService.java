package com.viaz.dashboard.entity.notification;

import com.viaz.dashboard.entity.user.UserRepository;
import com.viaz.dashboard.entity.user.ViazUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public void notifyUser(Long userId, String message) {
        ViazUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setRecipient(user);
        notification.setMessage(message);

        notificationRepository.save(notification);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow();
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}

