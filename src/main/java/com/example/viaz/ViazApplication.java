package com.example.viaz;

import com.example.viaz.shipment.Shipment;
import com.example.viaz.shipment.ShipmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class ViazApplication {

    public static void main(String[] args) {
        SpringApplication.run(ViazApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(ShipmentRepository shipmentRepository) {
        return args -> {
            Shipment shipment = new Shipment(
                    "T",
                    "Cris",
                    "Bader Alexis",
                    "Manzanillo",
                    "Dicex",
                    "Apodaca",
                    "Ingresado",
                    "NYKU3912270",
                    "20",
                    //CNTR DATE
                    LocalDateTime.parse("2024-07-03'T'10:00"),
                    "Coronado",
                    "45000156340",
                    "SSA",
                    true,
                    true,
                    true,
                    true,
                    true,
                     //Port Departure DATE
                    LocalDateTime.parse("2024-07-03"),
                    //Arrival DATE
                    LocalDateTime.parse("2024-07-07'T'13:30"),
                    //Unload DATE
                    LocalDateTime.parse("2024-07-06'T'15:00"),
                    "Solicitada",
                    "",
                    //POD DATE
                    LocalDateTime.parse("2024-07-11"),
                    //Vacumm DATE
                    LocalDateTime.parse("2024-07-11"),
                    "",
                    ""
            )
                    shipmentRepository.saveAndFlush(shipment);
        }

    }
}
