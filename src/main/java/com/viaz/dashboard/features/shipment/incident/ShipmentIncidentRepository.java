package com.viaz.dashboard.features.shipment.incident;

import com.viaz.dashboard.features.shipment.Shipment;
import com.viaz.dashboard.initializers.lists.IncidentList;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShipmentIncidentRepository extends JpaRepository<ShipmentIncident, Long> {
    List<ShipmentIncident> findByShipment(Shipment shipment);


    @Query("SELECT si FROM ShipmentIncident si WHERE si.shipment = :shipment AND si.incidentType.entityTied = :entityTied")
    List<ShipmentIncident> findIncidentsByShipmentTypeOfEntity(@Param("shipment") Shipment shipment, @Param("entityTied") IncidentList.TypeOfEntityRelation entityTied);


    @Transactional
    int deleteShipmentIncidentByShipmentIncidentId(Long shipmentIncidentId);

    boolean existsShipmentIncidentByIncidentTypeIncidentId(Long incidentTypeIncidentId);

    @Query("""
                SELECT COUNT(si)
                FROM ShipmentIncident si
                WHERE si.shipment.open = true
                  AND si.incidentType.entityTied = com.viaz.dashboard.initializers.lists.IncidentList$TypeOfEntityRelation.CARRIER
            """)
    Integer countCarrierIncidentsInOpenShipments();


    boolean existsByShipmentAndIncidentType_NameIgnoreCase(Shipment shipment, String incidentTypeName);
}
