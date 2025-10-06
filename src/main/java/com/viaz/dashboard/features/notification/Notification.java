package com.viaz.dashboard.features.notification;

import com.viaz.dashboard.features.user.ViazUser;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;


@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notifications")
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    private String message;

    private boolean read;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant timeStamp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private ViazUser recipient;

    public Notification(String message, ViazUser recipient) {
        this.message = message;
        this.recipient = recipient;
    }

    public boolean getRead() {
        return read;
    }
}
