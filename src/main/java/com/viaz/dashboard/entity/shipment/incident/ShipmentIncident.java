package com.viaz.dashboard.entity.shipment.incident;

import com.viaz.dashboard.entity.incident.IncidentType;
import com.viaz.dashboard.entity.shipment.Shipment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "shipment_incidents")
@EntityListeners(AuditingEntityListener.class)
public class ShipmentIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shipmentIncidentId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "incident_type_id")
    private IncidentType incidentType;

    @Column(name = "comment", length = 1000)
    private String comment;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime occurredAt;

    public ShipmentIncident(Shipment shipment, IncidentType incidentType, String comment) {
        this.shipment = shipment;
        this.incidentType = incidentType;
        this.comment = comment;
    }
}
