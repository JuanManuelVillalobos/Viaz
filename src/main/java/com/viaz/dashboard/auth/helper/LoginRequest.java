package com.viaz.dashboard.auth.helper;

public record LoginRequest(
        String email,
        String password
) {
}
