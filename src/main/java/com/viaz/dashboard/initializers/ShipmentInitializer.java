package com.viaz.dashboard.initializers;

import com.viaz.dashboard.entity.carrier.CarrierService;
import com.viaz.dashboard.entity.carrier.helpers.CarrierResponseDTO;
import com.viaz.dashboard.entity.shipment.ShipmentService;
import com.viaz.dashboard.entity.shipment.helpers.ShipmentRequestDTO;
import com.viaz.dashboard.entity.user.UserService;
import com.viaz.dashboard.entity.user.helpers.UserSummaryDTO;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static java.lang.Math.max;

@Slf4j
@Order(5)
@Component
@RequiredArgsConstructor
public class ShipmentInitializer implements CommandLineRunner {

    private final ShipmentService shipmentService;
    private final CarrierService carrierService;

    private final Random random = new Random();

    // Dropdown options based on your selects
    private static final List<String> TRANSPORT_METHODS = List.of("Terrestre", "Marítimo");
    private static final List<String> TYPE_OF_SERVICES = List.of("Full", "Sencillo");
    private static final List<String> STATUSES = List.of(
            "Información Pendiente", "Folio Pendiente", "Maniobras Pendientes",
            "Todo Ok", "En Puerto", "En Transito", "En Destino",
            "POD Pendiente", "Vacío Pendiente", "Ingresado", "Egresado"
    );
    private static final List<String> CONTAINER_TYPES = List.of(
            "40_HC", "20_HC", "40_IMO", "20_IMO", "40_DC", "20_DC",
            "CAJA_53", "NISSAN", "PLATAFORMA", "CAMA_BAJA", "RABON"
    );
    private static final List<String> POD_STATUSES = List.of("Pendiente", "Enviado");
    private static final List<String> EIR_STATUSES = List.of("No dieron", "Enviado");
    private static final List<String> CLEANING_ORDERS = List.of("Solicitada", "Enviada", "Pendiente", "Completada");
    private static final List<String> CLEANING_PATIOS = List.of("SSA", "TIMSA", "CIMA", "OCUPA", "ISL", "CISE", "Otro");
    private final UserService userService;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing default shipments...");

        List<String> customerAgents = userService.getUsersByRole("Customer Service").stream().map(UserSummaryDTO::username).toList();
        List<String> carriers = carrierService.getCarriers().stream().map(CarrierResponseDTO::name).toList();


        if (customerAgents.isEmpty() || carriers.isEmpty()) {
            log.warn("No customer service agents or carriers found. Skipping shipment initialization.");
            return;
        }

        for (int i = 1; i <= 30; i++) {
            ShipmentRequestDTO shipment = new ShipmentRequestDTO(
                    randomFrom(TRANSPORT_METHODS),
                    randomFrom(customerAgents),
                    randomFrom(TYPE_OF_SERVICES),
                    "DACHSER CATHERINE",                       // fixed client example
                    "Colima, Manzanillo",
                    "Customs Agent " + i,                      // dummy customs agent name
                    "Guadalajara, Jalisco",
                    randomFrom(STATUSES),
                    "OOLU" + (100000 + i),                     // container like OOLU0516360 pattern
                    randomFrom(CONTAINER_TYPES),
                    LocalDateTime.of(2025, 2, 1, 0, 0),
                    randomFrom(carriers),
                    "Operator " + i,
                    "Terminal " + i,
                    i % 2 == 0,
                    i % 3 == 0,
                    i % 4 == 0,
                    i % 5 == 0,
                    i % 6 == 0,
                    LocalDate.of(2025, 2, 1).minusDays(i),
                    LocalDateTime.now().plusDays(i),
                    LocalDateTime.now().plusDays(i + 1),
                    randomFrom(CLEANING_ORDERS),
                    randomFrom(POD_STATUSES),
                    randomFrom(CLEANING_PATIOS),
                    LocalDateTime.now().plusDays(i + 2),
                    LocalDate.now().plusDays(i + 2),
                    randomFrom(EIR_STATUSES),
                    "Observation shipment " + i,
                    random.nextInt(10)
            );

            shipmentService.createShipment(shipment);
        }

        log.info("Default shipments initialized.");
    }

    private <T> T randomFrom(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}
