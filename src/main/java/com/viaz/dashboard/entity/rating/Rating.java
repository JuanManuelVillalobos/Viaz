package com.viaz.dashboard.entity.rating;

import com.viaz.dashboard.entity.carrier.Carrier;
import com.viaz.dashboard.entity.shipment.Shipment;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ratings")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long ratingId;

    @Column(name = "shipment_rating")
    private Integer shipmentRating;

    @Column(name = "customer_service_rating")
    private Integer customerServiceRating;

    @Column(name = "rating_observation")
    private String ratingObservation;

//  Rating Statistics
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @OneToOne
    @JoinColumn(name = "viaz_id")
    private Shipment shipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier", referencedColumnName = "carrier_id")
    private Carrier carrier;
}
