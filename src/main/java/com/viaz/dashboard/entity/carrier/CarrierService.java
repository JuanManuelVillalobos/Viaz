package com.viaz.dashboard.entity.carrier;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class CarrierService {

    private final CarrierRepository carrierRepository;

    public ResponseEntity<String> createCarrier(Carrier carrier) {
        if (carrier == null) {
            return ResponseEntity.badRequest().body("Not able to extract carrier");
        }
        carrierRepository.save(carrier);
        return ResponseEntity.ok(String.format("%s saved successfully.", carrier.getName()));
    }

    public List<Carrier> getCarriers() {
        return carrierRepository.findAll();
    }

    public Carrier getCarrierByName(String name) {
        Carrier carrier = carrierRepository.getCarrierByName(name);
        if (carrier == null) throw new NoSuchElementException("Carrier not found with name: " + name);

        return carrier;
    }

    public ResponseEntity<String> changeCarrierName(Long carrierId, String newname) {
        String oldname = carrierRepository.findById(carrierId).getClass().getName();
        carrierRepository.findById(carrierId).ifPresent(carrier -> {
            carrier.setName(newname);
            carrierRepository.save(carrier);
        });
        return ResponseEntity.ok(String.format("Transporte %s renombrado como %s exitosamente.", oldname ,newname));
    }
}
