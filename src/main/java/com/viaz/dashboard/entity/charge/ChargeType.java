package com.viaz.dashboard.entity.charge;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "charges")
@Entity
public class ChargeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "charge_id")
    private Long chargeId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    public ChargeType(String name) {
        this.name = name;
    }
}
