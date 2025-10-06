package com.viaz.dashboard.features.incident;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface IncidentTypeRepository extends JpaRepository<IncidentType, Long> {

    Set<IncidentType> findByName(String name);

    Optional<IncidentType> findByNameIgnoreCase(String name);
}
