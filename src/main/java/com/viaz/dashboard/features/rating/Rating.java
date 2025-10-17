package com.viaz.dashboard.features.rating;

import com.viaz.dashboard.features.carrier.Carrier;
import com.viaz.dashboard.features.client.Client;
import com.viaz.dashboard.features.shipment.Shipment;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
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

//    Shipment Related
    @Column(name = "carrier_rating")
    private Double carrierRating;
    @Column(name = "client_rating")
    private Double clientRating;


//    Customer Service Related
    @Column(name = "carrier_customer_service_rating")
    private Double carrierCustomerServiceRating;
    @Column(name = "client_customer_service_rating")
    private Double clientCustomerServiceRating;

    @Column(name = "carrier_rating_observation")
    private String carrierRatingObservation;

    @Column(name = "client_rating_observation")
    private String clientRatingObservation;

//  Rating Statistics
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @OneToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier", referencedColumnName = "carrier_id")
    private Carrier carrier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client", referencedColumnName = "client_id")
    private Client client;
}
