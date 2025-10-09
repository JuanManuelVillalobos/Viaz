package com.viaz.dashboard.auth.dto;

public record LoginRequestDTO(
        String email,
        String password
) {
}
