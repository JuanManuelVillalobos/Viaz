package com.viaz.dashboard.entity.charge;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/charge")
public class ChargeController {

    private final ChargeService chargeService;

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_WRITE')")
    @GetMapping(value = "/get-charge-types")
    public List<ChargeType> getIncidentTypes() {
        return chargeService.getChargeTypes();
    }
}
