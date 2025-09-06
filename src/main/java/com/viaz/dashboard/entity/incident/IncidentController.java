package com.viaz.dashboard.entity.incident;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/incident")
public class IncidentController {
    private final IncidentService incidentService;

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_WRITE')")
    @GetMapping(value = "/get-incident-types")
    public List<IncidentType> getIncidentTypes(){
        return incidentService.getIncidentTypes();
    }
}
