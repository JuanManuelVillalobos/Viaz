package com.viaz.dashboard.entity.charge;

import com.viaz.dashboard.exception.exceptions.ResourceNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ChargeService {
    private final ChargeTypeRepository chargeTypeRepository;

    public List<ChargeType> getChargeTypes() {
        return chargeTypeRepository.findAll();
    }

    public Set<ChargeType> getChargeByName(String name) {
        return chargeTypeRepository.findByName(name);
    }

    public void createChargeOrThrow(String name) {
        if (!getChargeByName(name).isEmpty()) {
            throw new RuntimeException("Charge already exists");
        }
        ChargeType charge = new ChargeType(name);
        chargeTypeRepository.save(charge);
    }

    public ChargeType getChargeByIdOrThrow(@NonNull Long chargeId) {
        return chargeTypeRepository.findById(chargeId).orElseThrow(() -> new ResourceNotFoundException("No charge with id " + chargeId));
    }

}
