package com.viaz.dashboard.features.shipment.incident;

import com.viaz.dashboard.features.shipment.Shipment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShipmentIncidentRepository extends JpaRepository<ShipmentIncident, Long> {
    List<ShipmentIncident> findByShipment(Shipment shipment);

    @Transactional
    int deleteShipmentIncidentByShipmentIncidentId(Long shipmentIncidentId);

    boolean existsShipmentIncidentByIncidentTypeIncidentId(Long incidentTypeIncidentId);

    @Query("SELECT COUNT(si) FROM ShipmentIncident si " +
            "WHERE si.shipment.open = true AND si.incidentType.entityTied = com.viaz.dashboard.initializers.lists.IncidentList.TypeOfEntityRelation.CARRIER")
    Integer countCarrierIncidentsInOpenShipments();

    boolean existsByShipmentAndIncidentType_NameIgnoreCase(Shipment shipment, String incidentTypeName);
}
