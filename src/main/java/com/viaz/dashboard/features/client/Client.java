package com.viaz.dashboard.features.client;

import com.viaz.dashboard.features.user.ViazUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long clientId;

    private String name;

    @Column(name = "deleted")
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @ManyToOne
    @JoinColumn(name = "deleted_by_user_id")
    private ViazUser deletedByUserId;
}
