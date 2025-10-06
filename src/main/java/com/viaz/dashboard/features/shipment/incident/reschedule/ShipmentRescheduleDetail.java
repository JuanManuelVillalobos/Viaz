package com.viaz.dashboard.features.shipment.incident.reschedule;

import com.viaz.dashboard.features.shipment.incident.ShipmentIncident;
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
@Table(name = "shipment_reschedule_details")
@EntityListeners(AuditingEntityListener.class)
public class ShipmentRescheduleDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reschedule_id")
    private Long rescheduleId;

    // The incident of type LIBERACION_DE_DESPACHO_FALLIDA (reschedule incident)
    @OneToOne(optional = false)
    @JoinColumn(name = "shipment_incident_id")
    private ShipmentIncident rescheduleIncident;

    // The new scheduled unload time
    @Column(name = "new_unload_time", nullable = false)
    private LocalDateTime newUnloadTime;

    @Column(name = "comment", length = 1000)
    private String comment;

    // created date for auditing
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate;


}
