package com.viaz.dashboard.initializers;

import com.viaz.dashboard.features.incident.IncidentService;
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
            createIncidentIfNotExists(incident.getName(), incident.getPenalty(), incident.getTypeOfEntityRelation());
        }
    }

    private void createIncidentIfNotExists(String name, double penalty, String typeOfEntityRelation) {
        if (incidentService.getIncidentByName(name).isEmpty()) {
            incidentService.createIncidentOrThrow(name, penalty, typeOfEntityRelation);
        }
    }
}
