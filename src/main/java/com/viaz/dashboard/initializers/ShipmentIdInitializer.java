package com.viaz.dashboard.initializers;

import com.viaz.dashboard.features.shipment.idGenerator.ShipmentIdGeneratorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Order(5)
@Component
public class ShipmentIdInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ShipmentIdInitializer.class);
    private final ShipmentIdGeneratorService idGeneratorService;

    @Value("${migration.viazIdStartNumber}")
    private int MIGRATION_START_NUMBER = 1914;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing shipment ID sequence for migration...");
        idGeneratorService.initializeSequence(MIGRATION_START_NUMBER);
        logger.info("Shipment ID sequence initialized to start at: {}", MIGRATION_START_NUMBER);
    }
}

