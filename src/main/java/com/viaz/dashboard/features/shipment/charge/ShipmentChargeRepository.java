package com.viaz.dashboard.features.shipment.charge;

import com.viaz.dashboard.features.shipment.Shipment;
import com.viaz.dashboard.features.shipment.incident.ShipmentIncident;
import com.viaz.dashboard.initializers.lists.ChargeList;
import com.viaz.dashboard.initializers.lists.IncidentList;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ShipmentChargeRepository extends JpaRepository<ShipmentCharge, Long> {

    List<ShipmentCharge> findByShipment(Shipment shipment);

    @Transactional
    boolean deleteShipmentChargeByShipmentChargeIdIs(Long shipmentChargeId);

    boolean existsShipmentChargeByChargeTypeChargeId(Long chargeTypeChargeId);

    @Query("SELECT COUNT(c) FROM ShipmentCharge c WHERE c.occurredAt >= :start AND c.occurredAt < :end")
    int countChargesInDateRange(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT si FROM ShipmentCharge si WHERE si.shipment = :shipment AND si.chargeType.entityTied = :entityTied")
    List<ShipmentCharge> findChargesByShipmentTypeOfEntity(@Param("shipment") Shipment shipment, @Param("entityTied") ChargeList.TypeOfEntityRelation entityTied);
}
