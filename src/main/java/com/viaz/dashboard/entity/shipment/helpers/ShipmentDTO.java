package com.viaz.dashboard.entity.shipment.helpers;

import com.viaz.dashboard.entity.carrier.CarrierDTO;
import com.viaz.dashboard.entity.shipment.Shipment;

import java.time.LocalDate;
import java.time.LocalDateTime;

// This class is created to receive the request body from the frontend and associate the carrier.
public record ShipmentDTO(
        String viazId,
        Integer year,
        Integer sequence,
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
        CarrierDTO transport,
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
) {
    public ShipmentDTO(Shipment shipment) {
        this(
                shipment.getViazId(),
                shipment.getYear(),
                shipment.getSequence(),
                shipment.getTransportMethod(),
                shipment.getCustomerServiceAgent(),
                shipment.getTypeOfService(),
                shipment.getClient(),
                shipment.getOrigin(),
                shipment.getCustomsAgent(),
                shipment.getDestination(),
                shipment.getStatus(),
                shipment.getContainer(),
                shipment.getContainerType(),
                shipment.getShipmentDate(),
                shipment.getTransport() != null ?
                        new CarrierDTO(shipment.getTransport().getCarrierId(), shipment.getTransport().getName()) :
                        null,
                shipment.getOperator(),
                shipment.getTerminal(),
                shipment.getCi(),
                shipment.getCcp(),
                shipment.getTaxFolio(),
                shipment.getLoadManoeuvre(),
                shipment.getPisVinculation(),
                shipment.getDepartureDate(),
                shipment.getArrivalDate(),
                shipment.getUnloadDate(),
                shipment.getCleaningOrder(),
                shipment.getPod(),
                shipment.getCleaningPatio(),
                shipment.getCleaningDate(),
                shipment.getCleanArrivalDate(),
                shipment.getEir(),
                shipment.getObservations(),
                shipment.getFiles()
        );
    }

}
