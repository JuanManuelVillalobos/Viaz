package com.viaz.dashboard.shipment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    @Query(value = "SELECT s FROM shipment s ORDER BY s.viazId DESC")
    List<Shipment> findLastFiveShipments();

    Shipment findByViazId(String viazId);

    @Query("SELECT MAX(s.sequence) FROM shipment s WHERE s.year = :year")
    Integer getHighestSequenceForYear(@Param("year") int year);


}
