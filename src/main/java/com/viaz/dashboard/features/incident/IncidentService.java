package com.viaz.dashboard.features.incident;

import com.viaz.dashboard.features.shipment.incident.ShipmentIncidentRepository;
import com.viaz.dashboard.exception.exceptions.ResourceNotFoundException;
import com.viaz.dashboard.initializers.lists.IncidentList;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class IncidentService {

    private final IncidentTypeRepository incidentTypeRepository;
    private final ShipmentIncidentRepository shipmentIncidentRepository;

    public long getNumberOfIncidents() {
        return 0;
    }

    public List<IncidentType> getIncidentTypes() {
        return incidentTypeRepository.findAll();
    }

    public Set<IncidentType> getIncidentByName(String name) {
        return incidentTypeRepository.findByName(name);
    }

    public void createIncidentOrThrow(String name, double penalty, String entityTied) {
        if (name == null){
            throw new IllegalArgumentException("Incident name cannot be empty");
        }
        if (entityTied == null){
            throw new IllegalArgumentException("Incident entity tied cannot be empty");
        }
        if (!getIncidentByName(name).isEmpty()) {
            throw new RuntimeException("Incident already exists");
        }
        IncidentList.TypeOfEntityRelation typeOfEntityRelation = null;
        if(entityTied.equals("carrier")){
            typeOfEntityRelation = IncidentList.TypeOfEntityRelation.CARRIER;
        } else if (entityTied.equals("client")) {
            typeOfEntityRelation = IncidentList.TypeOfEntityRelation.CLIENT;
        }

        if(typeOfEntityRelation == null){
            throw new IllegalArgumentException("Incorrect entity tied");
        }

        IncidentType incident = IncidentType.builder()
                .name(name)
                .penalty(penalty)
                .entityTied(typeOfEntityRelation)
                .build();

        incidentTypeRepository.save(incident);
    }

    public IncidentType getIncidentByIdOrThrow(@NonNull Long incidentId) {
        return incidentTypeRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("No incident with id " + incidentId));
    }

    public void deleteIncidentType(Long incidentId) {
        if (incidentId == null) {
            throw new IllegalArgumentException("Incident ID cannot be null");
        }
        if (!incidentTypeRepository.existsById(incidentId)) {
            throw new IllegalArgumentException("Incident with ID " + incidentId + " does not exist");
        }

        if (shipmentIncidentRepository.existsShipmentIncidentByIncidentTypeIncidentId(incidentId)){
            throw new IllegalStateException("Cannot delete incident with shipments");
        }

        incidentTypeRepository.deleteById(incidentId);
    }
}
