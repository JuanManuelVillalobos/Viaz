package com.viaz.dashboard.entity.incident;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IncidentTypeRepository extends JpaRepository<IncidentType, Long> {

    Set<IncidentType> findByName(String name);
}
