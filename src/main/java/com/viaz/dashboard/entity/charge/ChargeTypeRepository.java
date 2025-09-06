package com.viaz.dashboard.entity.charge;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ChargeTypeRepository extends JpaRepository<ChargeType, Long> {

    Set<ChargeType> findByName(String name);
}
