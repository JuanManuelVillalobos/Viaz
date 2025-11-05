package com.viaz.dashboard.features.client;

import com.viaz.dashboard.features.user.ViazUser;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;


@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clients")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Client {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;


    public Client(String name) {
        this.name = name;
    }

    //    Client Stats
    @Column(name = "deleted")
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @ManyToOne
    @JoinColumn(name = "deleted_by_user_id")
    private ViazUser deletedByUserId;


    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private ViazUser createdBy;

    @LastModifiedBy
    @ManyToOne
    @JoinColumn(name = "last_modified_by_user_id")
    private ViazUser lastModifiedBy;

    @Override
    public String toString() {
        return String.format("Client %s", name);
    }

    public void softDelete(ViazUser deletedByUserId) {
        this.deleted = true;
        this.deletedAt = Instant.now();
        this.deletedByUserId = deletedByUserId;
    }
}
