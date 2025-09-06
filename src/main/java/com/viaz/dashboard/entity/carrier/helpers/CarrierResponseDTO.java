package com.viaz.dashboard.entity.carrier.helpers;

import lombok.NonNull;

public record CarrierResponseDTO(
        @NonNull Long carrierId,
        @NonNull String name
) {
}
