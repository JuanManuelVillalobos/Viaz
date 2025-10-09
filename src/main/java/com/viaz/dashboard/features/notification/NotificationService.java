package com.viaz.dashboard.features.notification;

import com.viaz.dashboard.features.notification.dto.NotificationResponseDTO;
import com.viaz.dashboard.features.user.UserRepository;
import com.viaz.dashboard.features.user.UserService;
import com.viaz.dashboard.features.user.ViazUser;
import com.viaz.dashboard.exception.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public List<NotificationResponseDTO> getUserNotifications(Long userId) {
        return notificationRepository.findByRecipientUserIdOrderByTimeStampDesc(
                userId).stream().map(NotificationResponseDTO::new).toList();
    }

    public void markAsRead(Long notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        notification.ifPresentOrElse(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        }, () -> {
            throw new ResourceNotFoundException("Notification not found with id: " + notificationId);
        });
    }

    public void createNotification(ViazUser user, String message) {
        Notification notification = new Notification();
        notification.setRecipient(user);
        notification.setMessage(message);
        notificationRepository.save(notification);

    }

    public Long getUnreadCount(Long userId) {
        ViazUser user = userService.getUserById(userId);
        return notificationRepository.countByRecipientAndReadIsFalse(user);
    }

    public boolean markAllAsRead(Long userId) {
        if (userId == null) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        notificationRepository.findByRecipientUserIdAndReadIsFalse(userId).forEach(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
        return true;
    }
}

