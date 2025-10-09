package com.viaz.dashboard.features.shipment.incident.reschedule;

import com.viaz.dashboard.features.incident.IncidentType;
import com.viaz.dashboard.features.incident.IncidentTypeRepository;
import com.viaz.dashboard.features.shipment.Shipment;
import com.viaz.dashboard.features.shipment.ShipmentRepository;
import com.viaz.dashboard.features.shipment.incident.ShipmentIncident;
import com.viaz.dashboard.features.shipment.incident.ShipmentIncidentRepository;
import com.viaz.dashboard.features.shipment.incident.reschedule.dto.ShipmentRescheduleResponseDTO;
import com.viaz.dashboard.initializers.lists.IncidentList;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ShipmentRescheduleService {

    private final ShipmentIncidentRepository shipmentIncidentRepository;
    private final ShipmentRescheduleDetailRepository rescheduleDetailRepository;
    private final IncidentTypeRepository incidentTypeRepository;
    private final ShipmentRepository shipmentRepository;


    @Caching(evict = {
            @CacheEvict(value = "shipmentsPageCache", allEntries = true),
            @CacheEvict(value = "openShipmentsPageCache", allEntries = true),
            @CacheEvict(value = "shipment", key = "#fullViazId")
    })
    public ShipmentIncident createRescheduleIncident(String fullViazId, LocalDateTime newUnloadTime, String comment) {
        Shipment shipment = shipmentRepository.findByFullViazId(fullViazId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found"));

        // Check if a reschedule already exists for this shipment
        boolean alreadyRescheduled = shipmentIncidentRepository.existsByShipmentAndIncidentType_NameIgnoreCase(
                shipment,
                IncidentList.LIBERACION_DE_DESPACHO_FALLIDA.getName()
        );

        if (alreadyRescheduled) {
            throw new IllegalStateException("A reschedule incident already exists for this shipment.");
        }

        IncidentType missedUnloadType = incidentTypeRepository.findByNameIgnoreCase(IncidentList.LIBERACION_DE_DESPACHO_FALLIDA.getName())
                .orElseThrow(() -> new EntityNotFoundException("IncidentType " + IncidentList.LIBERACION_DE_DESPACHO_FALLIDA.getName() + " not found"));

        // Create shipment incident for missed unload
        ShipmentIncident incident = new ShipmentIncident();
        incident.setShipment(shipment);
        incident.setIncidentType(missedUnloadType);
        incident.setComment(comment);
        // occurredAt is auto-set by auditing

        // Create reschedule detail and link to incident
        ShipmentRescheduleDetail rescheduleDetail = new ShipmentRescheduleDetail();
        rescheduleDetail.setNewUnloadTime(newUnloadTime);
        rescheduleDetail.setComment(comment);
        rescheduleDetail.setRescheduleIncident(incident);

        incident.setRescheduleDetail(rescheduleDetail);

        // Save incident (cascades save of rescheduleDetail)
        return shipmentIncidentRepository.save(incident);
    }

    public ShipmentRescheduleResponseDTO getRescheduleDetailByFullViazId(String fullViazId) {
        final String EXPECTED_INCIDENT_TYPE_NAME = IncidentList.LIBERACION_DE_DESPACHO_FALLIDA.getName();

        Shipment shipment = shipmentRepository.findByFullViazId(fullViazId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found with fullViazId: " + fullViazId));

        return shipment.getIncidents().stream()
                .filter(incident ->
                        incident.getIncidentType() != null &&
                                EXPECTED_INCIDENT_TYPE_NAME.equalsIgnoreCase(incident.getIncidentType().getName()) &&
                                incident.getRescheduleDetail() != null)
                .findFirst()
                .map(incident -> new ShipmentRescheduleResponseDTO(
                        incident.getShipmentIncidentId(),
                        incident.getOccurredAt(),
                        incident.getRescheduleDetail().getNewUnloadTime(),
                        incident.getRescheduleDetail().getComment()
                ))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Reschedule detail not found for incident of type '" +
                                EXPECTED_INCIDENT_TYPE_NAME + "' in shipment with fullViazId: " + fullViazId));
    }


}

