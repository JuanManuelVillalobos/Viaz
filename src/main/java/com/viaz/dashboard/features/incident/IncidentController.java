package com.viaz.dashboard.features.incident;

import com.viaz.dashboard.features.incident.dto.IncidentTypeRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/incident")
public class IncidentController {
    private final IncidentService incidentService;

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_WRITE')")
    @GetMapping(value = "/get-incident-types")
    public List<IncidentType> getIncidentTypes() {
        return incidentService.getIncidentTypes();
    }


    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'INCIDENT_MANAGE')")
    @PostMapping(value = "/create-incident-type")
    public ResponseEntity<Map<String, Object>> createIncidentType(@RequestBody IncidentTypeRequestDTO incidentTypeRequest) {
        incidentService.createIncidentOrThrow(incidentTypeRequest.name(), incidentTypeRequest.penalty(), incidentTypeRequest.entityTied());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Incident type created successfully " + HtmlUtils.htmlEscape(incidentTypeRequest.name()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'INCIDENT_MANAGE')")
    @DeleteMapping(value = "/delete-incident-type/{incidentId}")
    public ResponseEntity<Map<String, Object>> deleteIncidentType(@PathVariable Long incidentId) {
        incidentService.deleteIncidentType(incidentId);

         Map<String, Object> response = new HashMap<>();
        response.put("message", "Incident type deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
