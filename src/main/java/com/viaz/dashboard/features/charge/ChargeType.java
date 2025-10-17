package com.viaz.dashboard.features.charge;

import com.viaz.dashboard.initializers.lists.ChargeList;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "charge_types")
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

    @Column(name = "entityTied", nullable = false)
    private ChargeList.TypeOfEntityRelation entityTied; // It may be 'carrier' or 'client' or something else


    @Column(name = "deleted")
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public ChargeType(String name, ChargeList.TypeOfEntityRelation entityTied) {
        this.name = name;
        this.entityTied = entityTied;
    }
}
