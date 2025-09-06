package com.viaz.dashboard.initializers;

import com.viaz.dashboard.entity.charge.ChargeService;
import com.viaz.dashboard.initializers.lists.ChargeList;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(4)
@Component
@RequiredArgsConstructor
public class ChargesInitializer {

    private final ChargeService chargeService;

    @PostConstruct
    public void initializeDefaultCharges() {
        createDefaultCharges();
    }

    private void createDefaultCharges() {
        for (ChargeList charge : ChargeList.values()) {
            createPermissionIfNotExists(charge.getName());
        }
    }

    private void createPermissionIfNotExists(String name) {
        if (chargeService.getChargeByName(name).isEmpty()) {
            chargeService.createChargeOrThrow(name);
        }
    }
}
