package com.viaz.dashboard.features.shipment.subscription;

import com.viaz.dashboard.features.user.ViazUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "shipment_subscriptions")
public class ShipmentSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private ViazUser user;

    @Column(name = "shipment_id", nullable = false)
    private String shipmentID;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant subscribedAt;
}
