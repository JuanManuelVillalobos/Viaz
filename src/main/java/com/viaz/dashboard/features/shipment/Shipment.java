package com.viaz.dashboard.features.shipment;

import com.viaz.dashboard.features.carrier.Carrier;
import com.viaz.dashboard.features.client.Client;
import com.viaz.dashboard.features.rating.Rating;
import com.viaz.dashboard.features.shipment.charge.ShipmentCharge;
import com.viaz.dashboard.features.shipment.incident.ShipmentIncident;
import com.viaz.dashboard.features.user.ViazUser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shipments")
@Entity
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "base_viaz_id")
    private String baseViazId;

    @Column(name = "viaz_id", nullable = false, unique = true)
    private String fullViazId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_shipment_id")
    private Shipment baseShipment;

    @OneToMany(mappedBy = "baseShipment")
    private List<Shipment> variants = new ArrayList<>();

    // These are used to get the ViazID
    @Column(name = "year")
    private int year;
    @Column(name = "sequence")
    private int sequence;
    //

    @Column(name = "transport_method")
    private String transportMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_service_agent",
            referencedColumnName = "user_id"
    )
    @ToString.Exclude
    private ViazUser customerServiceAgent;

    @Column(name = "type_of_service")
    private String typeOfService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client",
            referencedColumnName = "client_id"
    )
    private Client client;

    @Column(name = "origin")
    private String origin;

    @Column(name = "customs_agent")
    private String customsAgent;

    @Column(name = "destination")
    private String destination;

    @Column(name = "status")
    private String status;

    @Column(name = "container")
    private String container;

    @Column(name = "container_type")
    private String containerType;

    //    Fecha de carga
    @Column(name = "shipment_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime shipmentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport",
            referencedColumnName = "carrier_id"
    )
    private Carrier transport;

    @Column(name = "foreign_reference")
    private String foreignReference;

    @Column(name = "terminal")
    private String terminal;

    @Column(name = "ci")
    private Boolean ci;

    @Column(name = "ccp")
    private Boolean ccp;

    @Column(name = "tax_folio")
    private Boolean taxFolio;

    @Column(name = "load_manouvre")
    private Boolean loadManoeuvre;

    @Column(name = "pis_vinculation")
    private Boolean pisVinculation;

    @Column(name = "departure_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate departureDate;

    @Column(name = "arrival_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime arrivalDate;

    @Column(name = "unload_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime unloadDate;

    //     Maniobra de vacio
    @Column(name = "cleaning_order")
    private String cleaningOrder;

    @Column(name = "pod")
    private String pod;

    @Column(name = "cleaning_patio")
    private String cleaningPatio;


    @Column(name = "has_cleaning_appointment")
    private Boolean hasCleaningAppointment;

    //    Fecha de Cita de Vacío
    @Column(name = "cleaning_appointment_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime cleaningAppointmentDate;

    @Column(name = "clean_arrival_date")
    private LocalDate cleanArrivalDate;

    @Column(name = "eir")
    private String eir;

    @Column(name = "observations")
    private String observations;

    @Column(name = "number_of_files")
    private Integer files;


    //    Shipment Data
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<ShipmentIncident> incidents = new HashSet<>();

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<ShipmentCharge> charges = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "rating_id")
    private Rating rating;

    @Column(name = "open")
    private Boolean open = true;


    //    Shipment Statistics
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

    @Column(name = "deleted")
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @ManyToOne
    @JoinColumn(name = "deleted_by_user_id")
    private ViazUser deletedByUserId;


    @Version
    @Column(name = "version")
    private Long version;


    @PrePersist
    public void generateId() {
        if (fullViazId == null) {
            this.fullViazId = "VZ-" + this.year + "-" + String.format("%03d", this.sequence);
        }
        this.files = 0;
    }

    public void incrementNumberOfFiles() {
        if (this.files == null) {
            this.files = 0;
        }
        this.files++;
    }

    public void decrementNumberOfFiles() {
        if (this.files == null) {
            this.files = 0;
        }
        this.files--;
    }

    public void open() {
        this.open = true;
    }

    public void close() {
        this.open = false;
    }

    public void markModified() {
        this.observations = (this.observations == null ? "" : this.observations) + " ";
    }


}
