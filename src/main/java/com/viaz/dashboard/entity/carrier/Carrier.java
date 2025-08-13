package com.viaz.dashboard.entity.carrier;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carriers")
@Entity
public class Carrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carrier_id")
    private Long carrierId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;
}
