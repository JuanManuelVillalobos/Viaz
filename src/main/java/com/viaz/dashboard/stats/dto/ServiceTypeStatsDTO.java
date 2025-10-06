package com.viaz.dashboard.stats.dto;

public record ServiceTypeStatsDTO(
        Long count,
        Double percentage
) {
    public ServiceTypeStatsDTO(Long count, Double percentage) {
        this.count = count;
        this.percentage = percentage;
    }
}
