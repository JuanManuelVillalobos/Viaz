package com.example.viaz.shipment;


import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "shipment")
@DynamicUpdate
public class Shipment {
    @Id
    @SequenceGenerator(
            name = "_sequence",
            sequenceName = "_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "_sequence"
    )
    private Long viazId;
    private String serviceType;
    private String customerService;
    private String client;
    private String origin;
    private String customsAgent;
    private String destination;
    private String status;
    private String container;
    private String cNTRType;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime cNTRDate;

    private String transport;
    private String operator;
    private String terminal;
    private Boolean cI;
    private Boolean cCP;
    private Boolean taxFolio;
    private Boolean loadManoeuvre;
    private Boolean pISVinculation;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate portDepartureDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime portArrivalDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime portUnloadDate;

    private String vacumManoeuvre;
    private String POD;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate PODDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate vacumArrival;
    private String EIR;
    private String observations;


    public Long getViazId() {
        return viazId;
    }

    public void setViazId(Long viazId) {
        this.viazId = viazId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getCustomerService() {
        return customerService;
    }

    public void setCustomerService(String customerService) {
        this.customerService = customerService;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getCustomsAgent() {
        return customsAgent;
    }

    public void setCustomsAgent(String customsAgent) {
        this.customsAgent = customsAgent;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public String getcNTRType() {
        return cNTRType;
    }

    public void setcNTRType(String cNTRType) {
        this.cNTRType = cNTRType;
    }

    public LocalDateTime getcNTRDate() {
        return cNTRDate;
    }

    public void setcNTRDate(LocalDateTime cNTRDate) {
        this.cNTRDate = cNTRDate;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public Boolean getcI() {
        return cI;
    }

    public void setcI(Boolean cI) {
        this.cI = cI;
    }

    public Boolean getcCP() {
        return cCP;
    }

    public void setcCP(Boolean cCP) {
        this.cCP = cCP;
    }

    public Boolean getTaxFolio() {
        return taxFolio;
    }

    public void setTaxFolio(Boolean taxFolio) {
        this.taxFolio = taxFolio;
    }

    public Boolean getLoadManoeuvre() {
        return loadManoeuvre;
    }

    public void setLoadManoeuvre(Boolean loadManoeuvre) {
        this.loadManoeuvre = loadManoeuvre;
    }

    public Boolean getpISVinculation() {
        return pISVinculation;
    }

    public void setpISVinculation(Boolean pISVinculation) {
        this.pISVinculation = pISVinculation;
    }

    public LocalDate getPortDepartureDate() {
        return portDepartureDate;
    }

    public void setPortDepartureDate(LocalDate portDepartureDate) {
        this.portDepartureDate = portDepartureDate;
    }

    public LocalDateTime getPortArrivalDate() {
        return portArrivalDate;
    }

    public void setPortArrivalDate(LocalDateTime portArrivalDate) {
        this.portArrivalDate = portArrivalDate;
    }

    public LocalDateTime getPortUnloadDate() {
        return portUnloadDate;
    }

    public void setPortUnloadDate(LocalDateTime portUnloadDate) {
        this.portUnloadDate = portUnloadDate;
    }

    public String getVacumManoeuvre() {
        return vacumManoeuvre;
    }

    public void setVacumManoeuvre(String vacumManoeuvre) {
        this.vacumManoeuvre = vacumManoeuvre;
    }

    public String getPOD() {
        return POD;
    }

    public void setPOD(String POD) {
        this.POD = POD;
    }

    public LocalDate getPODDate() {
        return PODDate;
    }

    public void setPODDate(LocalDate PODDate) {
        this.PODDate = PODDate;
    }

    public LocalDate getVacumArrival() {
        return vacumArrival;
    }

    public void setVacumArrival(LocalDate vacumArrival) {
        this.vacumArrival = vacumArrival;
    }

    public String getEIR() {
        return EIR;
    }

    public void setEIR(String EIR) {
        this.EIR = EIR;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}
