package com.viaz.dashboard.initializers;

import com.viaz.dashboard.features.carrier.CarrierService;
import com.viaz.dashboard.features.carrier.dto.CarrierResponseDTO;
import com.viaz.dashboard.features.charge.ChargeService;
import com.viaz.dashboard.features.charge.ChargeType;
import com.viaz.dashboard.features.client.ClientService;
import com.viaz.dashboard.features.client.dto.ClientResponseDTO;
import com.viaz.dashboard.features.incident.IncidentService;
import com.viaz.dashboard.features.incident.IncidentType;
import com.viaz.dashboard.features.rating.RatingRepository;
import com.viaz.dashboard.features.shipment.ShipmentRepository;
import com.viaz.dashboard.features.shipment.ShipmentService;
import com.viaz.dashboard.features.shipment.dto.ShipmentCloseRequestDTO;
import com.viaz.dashboard.features.shipment.dto.ShipmentRequestDTO;
import com.viaz.dashboard.features.user.UserService;
import com.viaz.dashboard.features.user.dto.UserSummaryDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.List;
import java.util.Random;

@Slf4j
@Order(6)
@Component
@RequiredArgsConstructor
@Profile("dev")
public class ShipmentInitializer implements CommandLineRunner {

    private final ShipmentService shipmentService;
    private final CarrierService carrierService;

    private final Random random = new Random();

    // Dropdown options based on your selects
    private static final List<String> TRANSPORT_METHODS = List.of("terrestre", "maritimo");
    private static final List<String> TYPE_OF_SERVICES = List.of("Full", "Sencillo");
    private static final List<String> STATUSES = List.of(
            "Información Pendiente", "Folio Pendiente", "Maniobras Pendientes",
            "Todo Ok", "En Puerto", "En Transito", "En Destino",
            "POD Pendiente", "Vacío Pendiente", "Ingresado", "Entregado"
    );
    private static final List<String> CONTAINER_TYPES = List.of(
            "40_HC", "20_HC", "40_IMO", "20_IMO", "40_DC", "20_DC",
            "CAJA_53", "NISSAN", "PLATAFORMA", "CAMA_BAJA", "RABON"
    );
    private static final List<String> POD_STATUSES = List.of("Pendiente", "Enviado");
    private static final List<String> EIR_STATUSES = List.of("No dieron", "Enviado");
    private static final List<String> EMPTYING_ORDER_TYPES = List.of("Solicitada", "Enviada", "Pendiente", "Completada");
    private static final List<String> EMPTYING_PATIOS = List.of("SSA", "TIMSA", "CIMA", "OCUPA", "ISL", "CISE", "Otro");
    private final UserService userService;
    private final ShipmentRepository shipmentRepository;
    private final IncidentService incidentService;
    private final ChargeService chargeService;
    private final RatingRepository ratingRepository;
    private final ClientService clientService;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing default shipments...");

        List<String> customerAgents = userService.getUsersByRole("Client Service").stream().map(UserSummaryDTO::username).toList();
        List<String> carriers = carrierService.getCarriers().stream().map(CarrierResponseDTO::name).toList();
        List<String> clients = clientService.getClients().stream().map(ClientResponseDTO::name).toList();
        List<IncidentType> incidentTypes = incidentService.getIncidentTypes();
        List<ChargeType> chargeTypes = chargeService.getChargeTypes();


        if (customerAgents.isEmpty() || carriers.isEmpty()) {
            log.warn("No customer service agents or carriers found. Skipping shipment initialization.");
            return;
        }

        int totalShipments = 30;
        Instant startDate = ZonedDateTime.of(
                2025, 1, 1, 0, 0, 0, 0,
                ZoneOffset.UTC
        ).toInstant();
        Instant endDate = Instant.now();

        for (int sequence = 1; sequence <= totalShipments; sequence++) {
            Instant createdDate = getCreatedDateBySequence(sequence, totalShipments, startDate, endDate);

            ShipmentRequestDTO shipment = new ShipmentRequestDTO(
                    randomFrom(TRANSPORT_METHODS),
                    randomFrom(customerAgents),
                    randomFrom(TYPE_OF_SERVICES),
                   randomFrom(clients),
                    "Colima, Manzanillo",
                    "Customs Agent " + sequence,
                    "Guadalajara, Jalisco",
                    randomFrom(STATUSES),
                    "OOLU" + (100000 + sequence),
                    randomFrom(CONTAINER_TYPES),
                    LocalDateTime.of(2025, 2, 1, 0, 0),
                    randomFrom(carriers),
                    "Reference " + sequence,
                    "Terminal " + sequence,
                    sequence % 2 == 0,
                    sequence % 3 == 0,
                    sequence % 4 == 0,
                    sequence % 5 == 0,
                    sequence % 6 == 0,
                    LocalDate.of(2025, 2, 1).minusDays(sequence),
                    LocalDateTime.now().plusDays(sequence),
                    LocalDateTime.now().plusDays(sequence + 1),
                    randomFrom(EMPTYING_ORDER_TYPES),
                    randomFrom(POD_STATUSES),
                    randomFrom(EMPTYING_PATIOS),
                    true,
                    LocalDateTime.now().plusDays(sequence + 2),
                    LocalDate.now().plusDays(sequence + 2),
                    randomFrom(EIR_STATUSES),
                    "Observation shipment " + sequence
            );

            // Create and retrieve the viazId
            var createdShipment = shipmentService.createShipment(shipment);
            String viazId = createdShipment.fullViazId();

            shipmentRepository.updateCreatedDate(viazId, createdDate);

            // Add 0-n incidents
            if (!incidentTypes.isEmpty()) {
                int incidentCount;
                int roll = random.nextInt(10); // 0–9

                if (roll < 7) { // 70% chance
                    incidentCount = 0;
                } else if (roll < 9) { // 20% chance
                    incidentCount = 1;
                } else { // 10% chance
                    incidentCount = 2;
                }

                for (int i = 0; i < incidentCount; i++) {
                    IncidentType randomIncident = randomFrom(incidentTypes);
                    shipmentService.addIncidentToShipment(viazId, randomIncident.getIncidentId(), "Test incident");
                }
            }

            // Add 0-n charges
            if (!chargeTypes.isEmpty()) {
                int chargeCount;
                int roll = random.nextInt(10); // 0–9

                if (roll < 7) { // 70% chance
                    chargeCount = 0;
                } else if (roll < 9) { // 20% chance
                    chargeCount = 1;
                } else { // 10% chance
                    chargeCount = 2;
                }


                for (int i = 0; i < chargeCount; i++) {
                    ChargeType randomCharge = randomFrom(chargeTypes);
                    shipmentService.addChargeToShipment(viazId, randomCharge.getChargeId(), "Test charge");
                }
            }

            // Close some shipments
            if (sequence % 3 == 0) { // Close every 3rd shipment, has rating from 1-10
//            if (true) {
                String closedShipmentViazID = shipmentService.closeShipment(viazId, new ShipmentCloseRequestDTO(viazId ,random.nextInt(1, 11), "Closed for testing", random.nextInt(1, 11), "Closed for testing"));
//                String closedShipmentViazID = shipmentService.closeShipment(viazId, new ShipmentCloseRequestDTO(viazId ,10, "Closed for testing", 0, "Closed for testing"));
                ratingRepository.updateCreatedDate(shipmentService.getShipmentById(closedShipmentViazID).getRating().getRatingId(), createdDate);
            }

        }


        log.info("Default shipments initialized.");
    }

    private Instant getCreatedDateBySequence(int sequence, int totalCount, Instant start, Instant end) {
        long startEpoch = start.getEpochSecond();
        long endEpoch = end.getEpochSecond();
        long interpolatedEpoch = startEpoch + ((endEpoch - startEpoch) * (sequence - 1)) / Math.max(1, totalCount - 1);

        return Instant.ofEpochSecond(interpolatedEpoch);
    }


    private <T> T randomFrom(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}
