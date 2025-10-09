package com.viaz.dashboard.features.notification;

import com.viaz.dashboard.features.user.ViazUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientUserIdOrderByTimeStampDesc(Long userId);


    Long countByRecipientAndReadIsFalse(ViazUser recipient);

    List<Notification> findByRecipientUserIdAndReadIsFalse(Long recipientUserId);


}
