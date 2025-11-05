package com.viaz.dashboard.features.shipment;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long>, JpaSpecificationExecutor<Shipment> {

    @EntityGraph(attributePaths = {"client", "customerServiceAgent", "transport", "incidents.incidentType", "charges.chargeType", "rating", "variants"})
    @Cacheable(value = "shipment", key = "#viazId")
    Optional<Shipment> findByFullViazId(String viazId);


    @Query("SELECT MAX(s.sequence) FROM Shipment s")
    Integer getHighestSequence();
    
    boolean existsShipmentByFullViazId(String viazId);

    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.createdDate >= :startDate AND s.createdDate < :endDate")
    Integer countShipmentsInPeriod(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);


    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.createdDate >= :startDate AND s.createdDate < :endDate AND s.open IS NOT TRUE ")
    Integer countShipmentsClosedInPeriod(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT DISTINCT s.status FROM Shipment s WHERE s.status IS NOT NULL")
    List<String> findDistinctStatuses();

    @Query("SELECT DISTINCT t.name FROM Shipment s JOIN s.client t WHERE t.name IS NOT NULL")
    List<String> findDistinctClients();

    @Query("SELECT DISTINCT t.name FROM Shipment s JOIN s.transport t WHERE t.name IS NOT NULL")
    List<String> findDistinctCarriers();

    //    ONLY FOR TESTING
    @Modifying
    @Transactional
    @Query("UPDATE Shipment s SET s.createdDate = :createdDate WHERE s.fullViazId = :viazId")
    void updateCreatedDate(@Param("viazId") String viazId, @Param("createdDate") Instant createdDate);



    Integer countShipmentsByStatus(String status);

    @Query("""
                SELECT s.typeOfService, COUNT(s)
                FROM Shipment s
                WHERE s.createdDate >= :startDate AND s.createdDate < :endDate
                GROUP BY s.typeOfService
            """)
    List<Object[]> countByTypeOfServiceInPeriod(@Param("startDate") Instant startDate,
                                                @Param("endDate") Instant endDate);

    @Query("""
            SELECT s FROM Shipment s
            WHERE s.unloadDate IS NULL
              AND s.arrivalDate <= :thresholdTime
            """)
    List<Shipment> findShipmentsWithArrivalOverdueAndNoUnload(@Param("thresholdTime") LocalDateTime thresholdTime);


    Integer countShipmentsByOpenTrueAndEmptyingOrderIs(String emptyingOrder);

    List<Shipment> findByBaseShipment(Shipment baseShipment);

    Shipment findBaseShipmentByFullViazId(String fullViazId);

    List<Shipment> findAllByEmptyingOrderIsIgnoreCase(String emptyingOrder);

    Integer countShipmentsByOpenTrue();

}