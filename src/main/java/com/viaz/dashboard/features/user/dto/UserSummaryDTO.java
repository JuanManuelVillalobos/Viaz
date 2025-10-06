package com.viaz.dashboard.features.user.dto;

public record UserSummaryDTO(Long id, String username) {
    public UserSummaryDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}

