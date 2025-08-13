package com.viaz.dashboard.entity.shipment;

import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(name = "customer_service_agent")
    private String customerServiceAgent;

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

    @Column(name = "shipment_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime shipmentDate;

    @Column(name = "transport")
    private String transport;

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

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;


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

}
