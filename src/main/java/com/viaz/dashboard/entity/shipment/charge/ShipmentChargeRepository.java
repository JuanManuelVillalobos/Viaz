package com.viaz.dashboard.entity.shipment.charge;

import com.viaz.dashboard.entity.shipment.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentChargeRepository extends JpaRepository<ShipmentCharge, Long> {

    List<ShipmentCharge> findByShipment(Shipment shipment);
}
