package com.viaz.dashboard.entity.shipment;

import com.viaz.dashboard.entity.carrier.Carrier;
import com.viaz.dashboard.entity.incident.IncidentType;
import com.viaz.dashboard.entity.rating.Rating;
import com.viaz.dashboard.entity.shipment.charge.ShipmentCharge;
import com.viaz.dashboard.entity.shipment.incident.ShipmentIncident;
import com.viaz.dashboard.entity.user.ViazUser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
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
    @Column(name = "viaz_id", nullable = false, unique = true)
    private String viazId;

    // These are used to get the ViazID
    @Column(name = "year")
    private int year;
    @Column(name = "sequence")
    private int sequence;
    //

    @Column(name = "transport_method")
    private String transportMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_service_agent", referencedColumnName = "user_id")
    private ViazUser customerServiceAgent;

    @Column(name = "type_of_service")
    private String typeOfService;

    @Column(name = "client")
    private String client;

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
    @JoinColumn(name = "transport", referencedColumnName = "carrier_id")
    private Carrier transport;

    @Column(name = "operator")
    private String operator;

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

    //    Fecha de Vacío
    @Column(name = "cleaning_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime cleaningDate;

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
    private Set<ShipmentIncident> incidents = new HashSet<>();

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ShipmentCharge> charges = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "rating_id")
    private Rating rating;

    @Column(name = "open")
    private Boolean open = true;


    //    Shipment Statistics
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private ViazUser createdBy;

    @LastModifiedBy
    @ManyToOne
    @JoinColumn(name = "last_modified_by_user_id")
    private ViazUser lastModifiedBy;


    @PrePersist
    public void generateId() {
        if (viazId == null) {
            this.viazId = "VZ-" + this.year + "-" + String.format("%03d", this.sequence);
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

}
