package com.viaz.dashboard.features.incident;

import com.viaz.dashboard.initializers.lists.IncidentList;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "incident_types")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentType {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "incident_id")
    private Long incidentId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "penalty", nullable = false)
    private double penalty;

    @Column(name = "entity_tied", nullable = false)
    private IncidentList.TypeOfEntityRelation entityTied; // It may be 'carrier' or 'client'

    @Column(name = "deleted")
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private Instant deletedAt;

}
