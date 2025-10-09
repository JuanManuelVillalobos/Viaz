package com.viaz.dashboard.features.charge;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "charges")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "charge_id")
    private Long chargeId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "penalty", nullable = false)
    private double penalty;


    @Column(name = "deleted")
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public ChargeType(String name) {
        this.name = name;
    }
}
