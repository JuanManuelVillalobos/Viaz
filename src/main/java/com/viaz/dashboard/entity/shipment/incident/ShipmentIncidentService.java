package com.viaz.dashboard.entity.shipment.incident;

import com.viaz.dashboard.entity.shipment.Shipment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ShipmentIncidentService {

    private final ShipmentIncidentRepository shipmentIncidentRepository;


    public long getNumberOfIncidentsByShipment(Shipment shipment) {
        return shipmentIncidentRepository.findByShipment(shipment).size();
    }

    public ShipmentIncident saveIncident(ShipmentIncident shipmentIncident) {
        return shipmentIncidentRepository.save(shipmentIncident);
    }

    public List<ShipmentIncidentResponseDTO> getIncidentsByShipment(Shipment shipment) {
        return shipmentIncidentRepository.findByShipment(shipment).stream().map(ShipmentIncidentResponseDTO::new).collect(Collectors.toList());
    }
}
