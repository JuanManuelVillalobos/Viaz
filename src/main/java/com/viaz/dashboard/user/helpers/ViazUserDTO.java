package com.viaz.dashboard.user.helpers;

import com.viaz.dashboard.user.ViazUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Set;

public record ViazUserDTO(
        Long userId,
        @NotBlank String username,
        @NotBlank @Email String email,
        LocalDateTime lastLoginDate,
        ViazUser.UserStatus status,
        @NotBlank Set<String> roles) {
}
