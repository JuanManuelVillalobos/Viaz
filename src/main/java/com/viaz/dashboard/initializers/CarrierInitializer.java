package com.viaz.dashboard.initializers;

import com.viaz.dashboard.entity.carrier.CarrierService;
import com.viaz.dashboard.entity.carrier.helpers.CarrierRequestDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Order(4)
@Component
@RequiredArgsConstructor
@Profile("dev")
public class CarrierInitializer {

    private final CarrierService carrierService;

    private static final List<String> DEFAULT_CARRIERS = List.of(
            "Transportes del Norte",
            "Fletes y Mudanzas Express",
            "Logística Integral MX",
            "Carga Segura",
            "Autotransportes del Pacífico",
            "Rapid Freight",
            "Movil Cargo",
            "Transmex",
            "LogiTrack",
            "Global Haulers"
    );

    @PostConstruct
    public void initializeDefaultCarriers() {
        log.info("Initializing default carriers...");

        for (String carrierName : DEFAULT_CARRIERS) {
            if (carrierService.getOptionalByName(carrierName).isEmpty()) {
                CarrierRequestDTO carrier = new CarrierRequestDTO(
                        carrierName,
                        "RFC123456X",           // Placeholder RFC
                        "Razon Social Placeholder", // Placeholder razonSocial
                        "0001"               // Placeholder CAAT
                );
                carrierService.createCarrier(carrier);
                log.info("Created carrier: {}", carrierName);
            } else {
                log.info("Carrier already exists: {}", carrierName);
            }
        }

        log.info("Default carriers initialized.");
    }
}
