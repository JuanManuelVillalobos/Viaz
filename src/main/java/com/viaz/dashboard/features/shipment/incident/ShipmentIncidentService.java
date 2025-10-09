package com.viaz.dashboard.features.shipment.incident;

import com.viaz.dashboard.features.shipment.Shipment;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ShipmentIncidentService {

    private final ShipmentIncidentRepository shipmentIncidentRepository;

    public ShipmentIncident getIncidentById(Long id) {
        return shipmentIncidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Incident not found"));
    }

    public void deleteIncident(ShipmentIncident shipmentIncident) {
        shipmentIncidentRepository.delete(shipmentIncident);
    }

    public long getNumberOfIncidentsByShipment(Shipment shipment) {
        return shipmentIncidentRepository.findByShipment(shipment).size();
    }

    public ShipmentIncident saveCarrierIncident(ShipmentIncident shipmentIncident) {
        return shipmentIncidentRepository.save(shipmentIncident);
    }

    public List<ShipmentIncident> getCarrierIncidentsByShipment(Shipment shipment) {
        return shipmentIncidentRepository.findByShipment(shipment);
    }
}
