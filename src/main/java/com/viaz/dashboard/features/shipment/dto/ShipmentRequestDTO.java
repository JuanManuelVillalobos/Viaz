package com.viaz.dashboard.features.shipment.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ShipmentRequestDTO(
        String transportMethod,
        String customerServiceAgent,
        String typeOfService,
        String client,
        String origin,
        String customsAgent,
        String destination,
        String status,
        String container,
        String containerType,
        LocalDateTime shipmentDate,
        String transport,
        String foreignReference,
        String terminal,
        Boolean ci,
        Boolean ccp,
        Boolean taxFolio,
        Boolean loadManoeuvre,
        Boolean pisVinculation,
        LocalDate departureDate,
        LocalDateTime arrivalDate,
        LocalDateTime unloadDate,
        String cleaningOrder,
        String pod,
        String cleaningPatio,
        Boolean hasCleaningAppointment,
        LocalDateTime cleaningAppointmentDate,
        LocalDate cleanArrivalDate,
        String eir,
        String observations
) {}
