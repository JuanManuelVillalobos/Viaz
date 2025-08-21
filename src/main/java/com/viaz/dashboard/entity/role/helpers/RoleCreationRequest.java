package com.viaz.dashboard.entity.role.helpers;

public record RoleCreationRequest(
        String name,
        String description,
        String displayName,
        boolean cosmetic
) {
}
