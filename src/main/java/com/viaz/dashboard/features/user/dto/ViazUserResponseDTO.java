package com.viaz.dashboard.features.user.dto;

import com.viaz.dashboard.features.user.ViazUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

public record ViazUserResponseDTO(
        Long userId,
        @NotBlank String username,
        @NotBlank @Email String email,
        Instant lastLoginDate,
        ViazUser.UserStatus status,
        @NotBlank Set<String> roles) {
}
