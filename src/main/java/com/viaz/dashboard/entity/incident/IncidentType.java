package com.viaz.dashboard.entity.incident;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Table(name = "charges")
@Entity
public class IncidentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "charge_id")
    private Long incidentId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    public IncidentType(String name) {
        this.name = name;
    }
}
