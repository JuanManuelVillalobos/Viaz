package com.viaz.dashboard.features.shipment.charge;

import com.viaz.dashboard.features.shipment.Shipment;
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
}
