package com.viaz.dashboard.entity.incident;

import com.viaz.dashboard.exception.exceptions.ResourceNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class IncidentService {

    private final IncidentTypeRepository incidentTypeRepository;

    public long getNumberOfIncidents() {
        return 0;
    }

    public List<IncidentType> getIncidentTypes() {
        return incidentTypeRepository.findAll();
    }

    public Set<IncidentType> getIncidentByName(String name) {
        return incidentTypeRepository.findByName(name);
    }

    public void createIncidentOrThrow(String name) {
        if (!getIncidentByName(name).isEmpty()) {
            throw new RuntimeException("Incident already exists");
        }
        IncidentType incident = new IncidentType(name);
        incidentTypeRepository.save(incident);
    }

    public IncidentType getIncidentByIdOrThrow(@NonNull Long incidentId) {
        return incidentTypeRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("No incident with id " + incidentId));
    }
}
