package com.viaz.dashboard.entity.carrier;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrierRepository extends JpaRepository<Carrier, Long> {
    Optional<Carrier> getCarrierByName(String name);
}
