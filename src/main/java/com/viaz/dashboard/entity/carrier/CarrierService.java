package com.viaz.dashboard.entity.carrier;

import com.viaz.dashboard.entity.carrier.helpers.CarrierDetailsDTO;
import com.viaz.dashboard.entity.carrier.helpers.CarrierRequestDTO;
import com.viaz.dashboard.entity.carrier.helpers.CarrierResponseDTO;
import com.viaz.dashboard.exception.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CarrierService {

    private final CarrierRepository carrierRepository;

    public String createCarrier(CarrierRequestDTO carrier) {
        if (carrier == null) {
            throw new IllegalArgumentException("Carrier cannot be null");
        }

        if (carrierRepository.getCarrierByName(carrier.name()).isPresent()) {
            throw new IllegalArgumentException("Carrier with name " + carrier.name() + " already exists.");
        }

        Carrier newCarrier = new Carrier(carrier.name(), carrier.rfc(), carrier.razonSocial(), carrier.caat());

        carrierRepository.save(newCarrier);
        return String.format("%s saved successfully.", newCarrier.getName());
    }

    public List<CarrierResponseDTO> getCarriers() {
        return carrierRepository.findAll().stream().map(this::carrierToDTO).collect(Collectors.toList());
    }

    public List<CarrierDetailsDTO> getCarriersWithSensitiveFields() {
        return carrierRepository.findAll().stream().map(CarrierDetailsDTO::new).collect(Collectors.toList());
    }

    public Optional<Carrier> getOptionalByName(String name) {
        return carrierRepository.getCarrierByName(name);
    }

    public Carrier getCarrierByNameOrThrow(String name) {
        return carrierRepository.getCarrierByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found: " + name));
    }


    public ResponseEntity<String> changeCarrierName(Long carrierId, String newname) {
        String oldname = carrierRepository.findById(carrierId).getClass().getName();
        carrierRepository.findById(carrierId).ifPresent(carrier -> {
            carrier.setName(newname);
            carrierRepository.save(carrier);
        });
        return ResponseEntity.ok(String.format("Transporte %s renombrado como %s exitosamente.", oldname, newname));
    }

    public CarrierResponseDTO carrierToDTO(Carrier carrier) {
        return new CarrierResponseDTO(carrier.getCarrierId(), carrier.getName());
    }

    public void updateCarrier(Long carrierId, CarrierRequestDTO carrier) {

    }
}
