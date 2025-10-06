package com.viaz.dashboard.features.carrier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CarrierRepository extends JpaRepository<Carrier, Long> {
    Optional<Carrier> getCarrierByName(String name);

    @Query("SELECT c.name FROM Carrier c WHERE c.carrierId = :carrierId")
    String getCarrierNameByCarrierId(@Param("carrierId") Long carrierId);


    void deleteByCarrierId(Long carrierId);
}
