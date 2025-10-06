package com.viaz.dashboard.features.charge;

import jakarta.persistence.*;
import lombok.*;

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


    public ChargeType(String name) {
        this.name = name;
    }
}
