package com.viaz.dashboard.entity.carrier;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CarrierRepository extends JpaRepository<Carrier, Long> {
    Carrier getCarrierByName(String name);
}
