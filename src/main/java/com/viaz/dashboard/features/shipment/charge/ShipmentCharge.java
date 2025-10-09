package com.viaz.dashboard.features.shipment.charge;

import com.viaz.dashboard.features.charge.ChargeType;
import com.viaz.dashboard.features.shipment.Shipment;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "shipment_charges")
@EqualsAndHashCode(of = {"shipmentChargeId"})
@EntityListeners(AuditingEntityListener.class)
public class ShipmentCharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shipmentChargeId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "charge_type_id")
    private ChargeType chargeType;

    @Column(name = "comment", length = 1000)
    private String comment;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant occurredAt;

    public ShipmentCharge(Shipment shipment, ChargeType chargeType, String comment) {
        this.shipment = shipment;
        this.chargeType = chargeType;
        this.comment = comment;
    }
}
