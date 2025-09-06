package com.viaz.dashboard.entity.role.helpers;

import java.util.Set;

public record RoleResponseDTO(
        Long roleId,
        String name,
        String description,
        Set<String> permissions
        ) {
}
