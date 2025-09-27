package com.viaz.dashboard.features.carrier.dto;

import com.viaz.dashboard.features.carrier.Carrier;
import com.viaz.dashboard.features.user.dto.UserSummaryDTO;

public record CarrierDetailsDTO(
        Long carrierId,
        String name,
        String rfc,
        String razonSocial,
        String caat,
        UserSummaryDTO createdBy,
        UserSummaryDTO lastModifiedBy
) {
    public CarrierDetailsDTO(Carrier carrier) {
        this(
                carrier.getCarrierId(),
                carrier.getName(),
                carrier.getRfc(),
                carrier.getRazonSocial(),
                carrier.getCaat(),
                carrier.getCreatedBy() != null ?
                        new UserSummaryDTO(carrier.getCreatedBy().getUserId(), carrier.getCreatedBy().getUsername()) :
                        null,
                carrier.getLastModifiedBy() != null ?
                        new UserSummaryDTO(carrier.getLastModifiedBy().getUserId(), carrier.getLastModifiedBy().getUsername()) :
                        null
        );
    }

    // Constructor or builder for mapping from Carrier entity
}

