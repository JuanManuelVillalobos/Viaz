package com.viaz.dashboard.entity.shipment;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.font.ShapeGraphicAttribute;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Shipment findByViazId(String viazId);

    @Query("SELECT s FROM Shipment s WHERE LOWER(s.status) != LOWER('entregado') ORDER BY s.viazId DESC")
    List<Shipment> getPendingShipments();

    @Modifying
    @Query("DELETE FROM Shipment s WHERE s.viazId = :viazID")
    void deleteByViazID(String viazID);

    @Query("SELECT MAX(s.sequence) FROM Shipment s WHERE s.year = :year")
    Integer getHighestSequenceForYear(@Param("year") int year);

    boolean existsShipmentByViazId(String viazId);

    List<Shipment> getShipmentsByArrivalDateAfter(LocalDateTime arrivalDateAfter);
}