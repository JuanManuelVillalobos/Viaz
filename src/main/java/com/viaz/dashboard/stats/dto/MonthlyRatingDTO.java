package com.viaz.dashboard.stats.dto;

public record MonthlyRatingDTO(
        String carrierName,
        String month,
        int monthNumber,
        double averageRating
) {
    public MonthlyRatingDTO(String carrierName, String month, int monthNumber, double averageRating) {
        this.carrierName = carrierName;
        this.month = month;
        this.monthNumber = monthNumber;
        this.averageRating = averageRating;
    }
}
