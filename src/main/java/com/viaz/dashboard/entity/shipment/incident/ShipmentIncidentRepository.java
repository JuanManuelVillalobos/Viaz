package com.viaz.dashboard.entity.shipment.incident;

import com.viaz.dashboard.entity.shipment.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentIncidentRepository extends JpaRepository<ShipmentIncident, Long> {
    List<ShipmentIncident> findByShipment(Shipment shipment);
}
