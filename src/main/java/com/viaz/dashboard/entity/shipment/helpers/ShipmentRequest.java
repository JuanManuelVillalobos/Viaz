package com.viaz.dashboard.entity.shipment.helpers;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ShipmentRequest(
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
        String operator,
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
        LocalDateTime cleaningDate,
        LocalDate cleanArrivalDate,
        String eir,
        String observations,
        Integer files
) {}
