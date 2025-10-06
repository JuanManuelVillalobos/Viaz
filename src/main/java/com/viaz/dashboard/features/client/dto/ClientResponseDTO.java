package com.viaz.dashboard.features.client.dto;

import com.viaz.dashboard.features.client.Client;

public record ClientResponseDTO(
        Long clientId,
        String name
) {
    public ClientResponseDTO(Client client) {
        this(
                client.getClientId(),
                client.getName()
        );
    }
}
