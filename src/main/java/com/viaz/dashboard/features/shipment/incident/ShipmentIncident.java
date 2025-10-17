package com.viaz.dashboard.features.shipment.incident;

import com.viaz.dashboard.features.incident.IncidentType;
import com.viaz.dashboard.features.shipment.Shipment;
import com.viaz.dashboard.features.shipment.incident.reschedule.ShipmentRescheduleDetail;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "shipment_incidents")
@EqualsAndHashCode(of = {"shipmentIncidentId"})
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

    @OneToOne(mappedBy = "rescheduleIncident", cascade = CascadeType.ALL, orphanRemoval = true)
    private ShipmentRescheduleDetail rescheduleDetail;

    public void setRescheduleDetail(ShipmentRescheduleDetail detail) {
        this.rescheduleDetail = detail;
        if (detail != null) {
            detail.setRescheduleIncident(this);
        }
    }


//    ShipmentIncident Statistics
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant occurredAt;

//    Constructors
    public ShipmentIncident(Shipment shipment, IncidentType incidentType, String comment) {
        this.shipment = shipment;
        this.incidentType = incidentType;
        this.comment = comment;
    }
}
