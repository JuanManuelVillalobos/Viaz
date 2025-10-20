package com.viaz.dashboard.initializers;


import com.viaz.dashboard.features.carrier.dto.CarrierRequestDTO;
import com.viaz.dashboard.features.client.ClientService;
import com.viaz.dashboard.features.client.dto.ClientRequestDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Order(5)
@Component
@RequiredArgsConstructor
@Profile("dev")
public class ClientInitializer {

    private final ClientService clientService;

    private static final List<String> DEFAULT_CLIENTS = List.of(
            "Feroly",
            "Dachser Catherine"
    );

    @PostConstruct
    public void initializeDefaultCarriers() {
        log.info("Initializing default carriers...");

        for (String carrierName : DEFAULT_CLIENTS) {
            if (clientService.getOptionalByName(carrierName).isEmpty()) {
                ClientRequestDTO client = new ClientRequestDTO(
                        carrierName
                );
                clientService.createClient(client);
                log.info("Created Client: {}", carrierName);
            } else {
                log.info("Client already exists: {}", carrierName);
            }
        }

        log.info("Default clients initialized.");
    }
}
