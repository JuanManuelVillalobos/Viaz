package com.viaz.dashboard.stats;

import com.viaz.dashboard.features.carrier.CarrierService;
import com.viaz.dashboard.features.client.ClientService;
import com.viaz.dashboard.features.rating.RatingRepository;
import com.viaz.dashboard.features.shipment.ShipmentRepository;
import com.viaz.dashboard.features.shipment.charge.ShipmentChargeRepository;
import com.viaz.dashboard.features.shipment.incident.ShipmentIncidentRepository;
import com.viaz.dashboard.stats.dto.MonthlyRatingDTO;
import com.viaz.dashboard.stats.dto.ServiceTypeStatsDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StatsService {

    private final ShipmentRepository shipmentRepository;
    private final RatingRepository ratingRepository;
    private final CarrierService carrierService;
    private final ShipmentChargeRepository shipmentChargeRepository;
    private final ShipmentIncidentRepository shipmentIncidentRepository;
    private final ClientService clientService;

    @Value("${rating.weight.clientCustomerService}")
    private double clientCustomerServiceRatingWeight;

    @Value("${rating.weight.carrierCustomerService}")
    private double carrierCustomerServiceRatingWeight;

    @Value("${rating.weight.carrier}")
    private double carrierRatingWeight;

    @Value("${rating.weight.client}")
    private double clientRatingWeight;


    @PostConstruct
    public void validateWeights() {
        double sum = carrierRatingWeight + clientRatingWeight + carrierCustomerServiceRatingWeight + clientCustomerServiceRatingWeight;

        // Allow small floating-point tolerance (e.g. 0.0001)
        if (Math.abs(sum - 1.0) > 0.0001) {
            throw new IllegalStateException(
                    String.format("Invalid rating weight configuration. Sum must be 1.0 but was %.4f", sum)
            );
        }
    }

    public Integer getLastMonthTotalShipments() {
        ZoneId zone = ZoneId.of("America/Mexico_City");

        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime oneMonthAgo = now.minusMonths(1);

        Instant start = oneMonthAgo.toInstant();
        Instant end = now.toInstant();

        return shipmentRepository.countShipmentsInPeriod(start, end);
    }

    public Integer getShipmentsInTransit() {
        return shipmentRepository.countShipmentsByStatus("En Transito");
    }

    public Integer getLastMonthTotalClosedShipments() {
        ZoneId zone = ZoneId.of("America/Mexico_City");

        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime oneMonthAgo = now.minusMonths(1);

        Instant start = oneMonthAgo.toInstant();
        Instant end = now.toInstant();

        return shipmentRepository.countShipmentsClosedInPeriod(start, end);
    }

    public Integer getOpenShipments() {
        return shipmentRepository.countShipmentsByOpenTrue();
    }

    public Integer getNumberOfIssuesInOpenShipments() {
        return shipmentIncidentRepository.countCarrierIncidentsInOpenShipments();
    }

    public Map<String, ServiceTypeStatsDTO> getNumberOfShipmentsByTypeOfService() {
        ZoneId zone = ZoneId.of("America/Mexico_City");

        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime oneMonthAgo = now.minusMonths(3);

        Instant start = oneMonthAgo.toInstant();
        Instant end = now.toInstant();


        List<Object[]> results = shipmentRepository.countByTypeOfServiceInPeriod(start, end);

        long total = results.stream()
                .mapToLong(row -> (Long) row[1])
                .sum();

        return getStringServiceTypeStatsDTOMap(results, total);
    }

    private static Map<String, ServiceTypeStatsDTO> getStringServiceTypeStatsDTOMap(List<Object[]> results, long total) {
        Map<String, ServiceTypeStatsDTO> statsMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            String type = (String) row[0];

            // Only include "Full" and "Sencillo"
            if (!"Full".equalsIgnoreCase(type) && !"Sencillo".equalsIgnoreCase(type)) {
                continue;
            }

            Long count = (Long) row[1];
            double percentage = total == 0 ? 0.0 : (double) count / total * 100;

            statsMap.put(type, new ServiceTypeStatsDTO(count, percentage));
        }
        return statsMap;
    }


    @Cacheable("monthlyRatings")
    public List<MonthlyRatingDTO> getMonthlyAverageRatingsForAllCarriers() {
        List<Object[]> rawResults = ratingRepository.findMonthlyAverageRatingsForAllCarriers();

        return rawResults.stream()
                .map(row -> {
                    // Extract carrier ID and resolve name
                    Long carrierId = (row[0] != null) ? ((Number) row[0]).longValue() : null;
                    String carrierName = (carrierId != null) ? carrierService.getCarrierNameById(carrierId) : "Unknown Carrier";

                    String month = (String) row[1];
                    int monthNumber = (row[2] != null) ? ((Number) row[2]).intValue() : 0;

                    // Ratings (null-safe)
                    Double carrierRating = (row[3] != null) ? ((Number) row[3]).doubleValue() : null;
                    Double carrierCustomerServiceRating = (row[4] != null) ? ((Number) row[4]).doubleValue() : null;

                    // Define weights
                    double carrierRatingWeight = 0.5;
                    double customerServiceRatingWeight = 0.5;

                    // Weighted average calculation (only include non-null ratings)
                    double weightedSum = 0.0;
                    double totalWeight = 0.0;

                    if (carrierRating != null) {
                        weightedSum += carrierRating * carrierRatingWeight;
                        totalWeight += carrierRatingWeight;
                    }

                    if (carrierCustomerServiceRating != null) {
                        weightedSum += carrierCustomerServiceRating * customerServiceRatingWeight;
                        totalWeight += customerServiceRatingWeight;
                    }

                    double weightedAverage = totalWeight > 0 ? weightedSum / totalWeight : 0.0;

                    return new MonthlyRatingDTO(carrierName, month, monthNumber, weightedAverage);
                })
                .sorted(Comparator.comparingInt(MonthlyRatingDTO::monthNumber))
                .collect(Collectors.toList());
    }


    @Cacheable(value = "monthlyRatingsForCarrier", key = "#carrierId")
    public List<MonthlyRatingDTO> getMonthlyAverageRatingsForCarrier(Long carrierId) {
        List<Object[]> rawResults = ratingRepository.findRatingsForCarrier(carrierId);
        String carrierName = carrierService.getCarrierNameById(carrierId);

        // Map by month and calculate weighted averages
        return getMonthlyRatingDTOS(rawResults, carrierName, carrierCustomerServiceRatingWeight, carrierRatingWeight);
    }


    public Integer getOpenEmptyShipments() {
        return shipmentRepository.countShipmentsByOpenTrueAndCleaningOrderIs("Completada");
    }

    public Integer getNumberOfChargesLastMonth() {
        ZoneId zone = ZoneId.of("America/Mexico_City");

        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime oneMonthAgo = now.minusMonths(1);

        Instant start = oneMonthAgo.toInstant();
        Instant end = now.toInstant();

        return shipmentChargeRepository.countChargesInDateRange(start, end);
    }


    @Cacheable(value = "monthlyRatingsForClient", key = "#clientId")
    public List<MonthlyRatingDTO> getMonthlyAverageRatingsForClient(Long clientId) {
        List<Object[]> rawResults = ratingRepository.findRatingsForClient(clientId);
        String clientName = clientService.getClientNameById(clientId);



        // Map by month and calculate weighted averages
        return getMonthlyRatingDTOS(rawResults, clientName, clientCustomerServiceRatingWeight, clientRatingWeight);
    }

    private List<MonthlyRatingDTO> getMonthlyRatingDTOS(List<Object[]> rawResults, String name, double customerServiceRatingWeight, double ratingWeight) {
        Map<String, List<Object[]>> ratingsByMonth = rawResults.stream()
                .collect(Collectors.groupingBy(row -> (String) row[1])); // group by month string

        List<MonthlyRatingDTO> results = new ArrayList<>();

        for (var entry : ratingsByMonth.entrySet()) {
            String month = entry.getKey();
            List<Object[]> ratings = entry.getValue();

            int monthNumber = ratings.isEmpty() ? 0 : ((Number) ratings.getFirst()[2]).intValue();

            double weightedSum = 0.0;
            double totalWeight = 0.0;

            for (Object[] row : ratings) {
                Double customerServiceRating = row[3] != null ? ((Number) row[3]).doubleValue() : null;
                Double rating = row[4] != null ? ((Number) row[4]).doubleValue() : null;

                if (customerServiceRating != null) {
                    weightedSum += customerServiceRating * customerServiceRatingWeight;
                    totalWeight += customerServiceRatingWeight;
                }

                if (rating != null) {
                    weightedSum += rating * ratingWeight;
                    totalWeight += ratingWeight;
                }
            }

            double averageRating = totalWeight > 0 ? weightedSum / totalWeight : 0;

            results.add(new MonthlyRatingDTO(name, month, monthNumber, averageRating));
        }

        results.sort(Comparator.comparingInt(MonthlyRatingDTO::monthNumber));

        return results;
    }
}
