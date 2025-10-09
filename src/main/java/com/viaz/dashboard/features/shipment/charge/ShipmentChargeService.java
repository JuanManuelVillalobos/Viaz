package com.viaz.dashboard.features.shipment.charge;

import com.viaz.dashboard.features.shipment.Shipment;
import jakarta.persistence.EntityNotFoundException;
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

    public ShipmentCharge getChargeById(Long chargeId) {
        return shipmentChargeRepository.findById(chargeId)
                .orElseThrow(() -> new EntityNotFoundException("Charge not found"));
    }

    public void deleteCharge(ShipmentCharge charge) {
        shipmentChargeRepository.delete(charge);
    }
}
