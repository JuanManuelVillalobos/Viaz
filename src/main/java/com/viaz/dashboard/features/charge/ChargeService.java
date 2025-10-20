package com.viaz.dashboard.features.charge;

import com.viaz.dashboard.features.shipment.charge.ShipmentChargeRepository;
import com.viaz.dashboard.exception.exceptions.ResourceNotFoundException;
import com.viaz.dashboard.initializers.lists.ChargeList;
import com.viaz.dashboard.initializers.lists.IncidentList;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ChargeService {
    private final ChargeTypeRepository chargeTypeRepository;
    private final ShipmentChargeRepository shipmentChargeRepository;

    public List<ChargeType> getChargeTypes() {
        return chargeTypeRepository.findAll();
    }

    public Set<ChargeType> getChargeByName(String name) {
        return chargeTypeRepository.findByName(name);
    }

    public void createChargeOrThrow(String name, double penalty, String entityTied) {
        if (name == null){
            throw new IllegalArgumentException("Incident name cannot be empty");
        }
        if (entityTied == null){
            throw new IllegalArgumentException("Incident entity tied cannot be empty");
        }
        if (!getChargeByName(name).isEmpty()) {
            throw new RuntimeException("Incident already exists");
        }
        ChargeList.TypeOfEntityRelation typeOfEntityRelation = null;
        if(entityTied.equals("carrier")){
            typeOfEntityRelation = ChargeList.TypeOfEntityRelation.CARRIER;
        } else if (entityTied.equals("client")) {
            typeOfEntityRelation = ChargeList.TypeOfEntityRelation.CLIENT;
        }

        if(typeOfEntityRelation == null){
            throw new IllegalArgumentException("Incorrect entity tied");
        }

        ChargeType charge = ChargeType.builder()
                .name(name)
                .penalty(penalty)
                .entityTied(typeOfEntityRelation)
                .build();

        chargeTypeRepository.save(charge);
    }

    public ChargeType getChargeByIdOrThrow(@NonNull Long chargeId) {
        return chargeTypeRepository.findById(chargeId).orElseThrow(() -> new ResourceNotFoundException("No charge with id " + chargeId));
    }

    public void deleteChargeType(Long chargeId) {
        if (chargeId == null) {
            throw new IllegalArgumentException("Incident ID cannot be null");
        }
        if (!chargeTypeRepository.existsById(chargeId)) {
            throw new IllegalArgumentException("Incident with ID " + chargeId + " does not exist");
        }

        if (shipmentChargeRepository.existsShipmentChargeByChargeTypeChargeId(chargeId)) {
            throw new IllegalStateException("Cannot delete incident with shipments");
        }

        chargeTypeRepository.deleteById(chargeId);
    }
}
