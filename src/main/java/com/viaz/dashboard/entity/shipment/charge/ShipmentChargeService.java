package com.viaz.dashboard.entity.shipment.charge;

import com.viaz.dashboard.entity.shipment.Shipment;
import com.viaz.dashboard.entity.shipment.incident.ShipmentIncident;
import com.viaz.dashboard.entity.shipment.incident.ShipmentIncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ShipmentChargeService {


    private final ShipmentChargeRepository shipmentChargeRepository;

    public ShipmentCharge saveCharge(ShipmentCharge shipmentCharge) {
        return shipmentChargeRepository.save(shipmentCharge);
    }

    public List<ShipmentCharge> getChargesByShipment(Shipment shipment) {
        return shipmentChargeRepository.findByShipment(shipment);
    }
}
