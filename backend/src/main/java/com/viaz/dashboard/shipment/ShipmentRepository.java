package com.viaz.dashboard.shipment;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Shipment findByViazId(String viazId);

    @Query("SELECT s FROM shipment s WHERE LOWER(s.shipmentStatus) != LOWER('egresado') ORDER BY s.viazId DESC")
    List<Shipment> getPendingShipments();

    @Transactional
    @Modifying
    @Query("DELETE FROM shipment s WHERE s.viazId = :viazID")
    void deleteByViazID(String viazID);

    @Query("SELECT MAX(s.sequence) FROM shipment s WHERE s.year = :year")
    Integer getHighestSequenceForYear(@Param("year") int year);


}
