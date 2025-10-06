package com.viaz.dashboard.features.carrier.dto;

import lombok.NonNull;

public record CarrierResponseDTO(
        @NonNull Long carrierId,
        @NonNull String name
) {
}
