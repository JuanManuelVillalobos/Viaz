package com.viaz.dashboard.entity.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
//        List<Notification> findByRecipientIdAndReadFalse(Long userId);

}
