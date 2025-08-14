package com.viaz.dashboard.entity.carrier;

import lombok.NonNull;

public record CarrierDTO(
        @NonNull Long carrierId,
        @NonNull String name
) {
}
