package com.viaz.dashboard.entity.carrier;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CarrierService {

    private final CarrierRepository carrierRepository;

    public ResponseEntity<String> createCarrier(Carrier carrier) {
        if(carrier == null){
            return ResponseEntity.badRequest().body("Not able to extract carrier");
        }
        carrierRepository.save(carrier);
        return ResponseEntity.ok(String.format("%s saved successfully.", carrier.getName()));
    }

    public List<Carrier> getCarriers() {
        return carrierRepository.findAll();
    }

    public Carrier getCarrierById(Long id) {
        return carrierRepository.getReferenceById(id);
    }
}
