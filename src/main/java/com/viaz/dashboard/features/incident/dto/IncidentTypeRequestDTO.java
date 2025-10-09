package com.viaz.dashboard.features.incident.dto;

import com.viaz.dashboard.initializers.lists.IncidentList;

public record IncidentTypeRequestDTO(
        String name,
        Integer penalty,
        String typeOfEntityRelation
        ) { }
