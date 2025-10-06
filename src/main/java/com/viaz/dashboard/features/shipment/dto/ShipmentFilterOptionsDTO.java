package com.viaz.dashboard.features.shipment.dto;

import java.util.List;

public record ShipmentFilterOptionsDTO(
    List<String> statuses,
    List<String> clients,
    List<String> carriers
) {}

