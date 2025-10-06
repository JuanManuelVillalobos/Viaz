package com.viaz.dashboard.features.incident;

import com.viaz.dashboard.initializers.lists.IncidentList;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "incidents")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "charge_id")
    private Long incidentId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "penalty", nullable = false)
    private double penalty;

    @Column(name = "entityTied", nullable = false)
    private IncidentList.TypeOfEntityRelation entityTied; // It may be 'carrier' or 'client'

    public IncidentType(String name, IncidentList.TypeOfEntityRelation entityTied) {
        this.name = name;
        this.entityTied = entityTied;
    }
}
