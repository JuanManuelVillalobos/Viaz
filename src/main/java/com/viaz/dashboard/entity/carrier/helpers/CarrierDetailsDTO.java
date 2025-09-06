package com.viaz.dashboard.entity.carrier.helpers;

import com.viaz.dashboard.entity.carrier.Carrier;
import com.viaz.dashboard.entity.user.helpers.UserSummaryDTO;
import lombok.Data;

import java.time.LocalDateTime;

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

