package com.viaz.dashboard.user.helpers;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ViazUserDTO(Long userId, @NotBlank String username,@NotBlank @Email String email){}
