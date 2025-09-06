package com.viaz.dashboard.initializers;

import com.viaz.dashboard.entity.incident.IncidentService;
import com.viaz.dashboard.initializers.lists.IncidentList;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(3)
@Component
@RequiredArgsConstructor
public class IncidentsInitializer {
    private final IncidentService incidentService;

    @PostConstruct
    public void initializeDefaultIncidents() {
        createDefaultIncidents();
    }

    private void createDefaultIncidents() {
        for (IncidentList incident : IncidentList.values()) {
            createPermissionIfNotExists(incident.getName());
        }
    }

    private void createPermissionIfNotExists(String name) {
        if (incidentService.getIncidentByName(name).isEmpty()) {
            incidentService.createIncidentOrThrow(name);
        }
    }
}
