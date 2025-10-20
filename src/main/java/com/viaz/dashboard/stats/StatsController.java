package com.viaz.dashboard.stats;

import com.viaz.dashboard.stats.dto.MonthlyRatingDTO;
import com.viaz.dashboard.stats.dto.ServiceTypeStatsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/stats/")
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/get-last-month-total-shipments")
    public Integer getLastMonthTotalShipments() {
        return statsService.getLastMonthTotalShipments();
    }

    @GetMapping("/get-shipments-in-transit")
    public Integer getShipmentsInTransit() {
        return statsService.getShipmentsInTransit();
    }

    @GetMapping("/get-last-month-total-closed-shipments")
    public Integer getLastMonthTotalClosedShipments() {
        return statsService.getLastMonthTotalClosedShipments();
    }

    @GetMapping("/get-open-shipments")
    public Integer getOpenShipments() {
        return statsService.getOpenShipments();
    }

    @GetMapping("/get-number-of-issues-in-open-shipments")
    public Integer getNumberOfIssuesInOpenShipments(){
        return statsService.getNumberOfIssuesInOpenShipments();
    }

    @GetMapping("/get-number-of-shipments-by-type-of-service")
    public Map<String, ServiceTypeStatsDTO> getNumberOfShipmentsByTypeOfService(){
        return statsService.getNumberOfShipmentsByTypeOfService();
    }

    @GetMapping("/get-monthly-ratings-of-carriers")
    public List<MonthlyRatingDTO> getMonthlyRatings() {
        return statsService.getMonthlyAverageRatingsForAllCarriers();
    }

    @GetMapping("/get-monthly-ratings-of-carrier/{carrierId}")
    public List<MonthlyRatingDTO> getMonthlyRatingsOfCarrier(@PathVariable Long carrierId) {
        return statsService.getMonthlyAverageRatingsForCarrier(carrierId);
    }

    @GetMapping("/get-cleaned-shipments")
    public Integer getEmptyShipments() {
        return statsService.getOpenEmptyShipments();
    }

    @GetMapping("/get-number-of-charges-in-last-month")
    public Integer getNumberOfChargesInLastMonth(){
        return statsService.getNumberOfChargesLastMonth();
    }

    @GetMapping("/get-monthly-ratings-of-client/{clientId}")
    public List<MonthlyRatingDTO> getMonthlyRatingsOfClient(@PathVariable Long clientId) {
        return statsService.getMonthlyAverageRatingsForClient(clientId);
    }
}
