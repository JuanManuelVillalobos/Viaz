package com.viaz.dashboard.shipment;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "shipment")
@DynamicUpdate
public class Shipment {
    @Id
    private String viazId;
    private int year;
    private int sequence;

    private String typeOfService;
    private String customerServiceAgent;
    private String client;
    private String origin;
    private String customsAgent;
    private String destination;
    private String shipmentStatus;
    private String container;
    private String cntrType;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime cntrDate;

    private String transportProvider;
    private String shipmentOperator;
    private String shipmentTerminal;
    private Boolean ci;
    private Boolean ccp;
    private Boolean taxFolio;
    private Boolean loadManoeuvre;
    private Boolean pisVinculation;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate portDepartureDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime portArrivalDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime portUnloadDate;

    private String vacumManoeuvre;
    private String pod;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate podDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate vacumArrival;
    private String eir;
    private String observations;


    @PrePersist
    public void generateId() {
        if (viazId == null) {
            this.viazId = "VZ-" + this.year + "-" + String.format("%03d", this.sequence);
        }
    }

}
