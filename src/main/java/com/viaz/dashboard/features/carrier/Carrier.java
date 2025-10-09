package com.viaz.dashboard.features.carrier;

import com.viaz.dashboard.features.user.ViazUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carriers")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Carrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carrier_id")
    private Long carrierId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "rfc")
    @Size(min = 10, max = 10, message = "RFC must be 10 characters long")
    private String rfc;

    @Column(name = "razon_social")
    private String razonSocial;

    @Column(name = "caat")
    @Size(min = 4, max = 4, message = "CAAT must be 4 character long")
    private String caat;

    //    Carrier Statistics
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private ViazUser createdBy;

    @LastModifiedBy
    @ManyToOne
    @JoinColumn(name = "last_modified_by_user_id")
    private ViazUser lastModifiedBy;

    public Carrier(String name, String rfc, String razonSocial, String caat) {
        this.name = name;
        this.rfc = rfc;
        this.razonSocial = razonSocial;
        this.caat = caat;
    }

    @Column(name = "deleted")
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @ManyToOne
    @JoinColumn(name = "deleted_by_user_id")
    private ViazUser deletedByUserId;

    @Override
    public String toString() {
        return String.format("Carrier %s", name);
    }
}
