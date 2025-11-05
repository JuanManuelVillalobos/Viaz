package com.viaz.dashboard.features.carrier;

import com.viaz.dashboard.features.carrier.dto.CarrierDetailsDTO;
import com.viaz.dashboard.features.carrier.dto.CarrierRequestDTO;
import com.viaz.dashboard.features.carrier.dto.CarrierResponseDTO;
import com.viaz.dashboard.exception.exceptions.ResourceNotFoundException;
import com.viaz.dashboard.features.user.UserRepository;
import com.viaz.dashboard.features.user.ViazUser;
import com.viaz.dashboard.features.user.userDetails.ViazUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CarrierService {

    private final CarrierRepository carrierRepository;
    private final UserRepository userRepository;

    public void createCarrier(CarrierRequestDTO carrier) {
        if (carrier == null) {
            throw new IllegalArgumentException("Carrier cannot be null");
        }

        if (carrierRepository.getCarrierByName(carrier.name()).isPresent()) {
            throw new IllegalArgumentException("Carrier with name " + carrier.name() + " already exists.");
        }

        Carrier newCarrier = new Carrier(carrier.name(), carrier.rfc(), carrier.razonSocial(), carrier.caat());

        carrierRepository.save(newCarrier);
    }

    public List<CarrierResponseDTO> getCarriers() {
        return carrierRepository.findAll().stream().filter(carrier -> !carrier.isDeleted()).map(this::carrierToDTO).collect(Collectors.toList());
    }

    public List<CarrierDetailsDTO> getCarriersWithSensitiveFields() {
        return carrierRepository.findAll().stream().filter(carrier -> !carrier.isDeleted()).map(CarrierDetailsDTO::new).collect(Collectors.toList());
    }

    public Optional<Carrier> getOptionalByName(String name) {
        return carrierRepository.getCarrierByName(name);
    }

    public Carrier getCarrierByNameOrThrow(String name) {
        return carrierRepository.getCarrierByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found: " + name));
    }


    public CarrierResponseDTO carrierToDTO(Carrier carrier) {
        return new CarrierResponseDTO(carrier.getCarrierId(), carrier.getName());
    }

    public CarrierDetailsDTO updateCarrier(Long carrierId, Map<String, Object> updates) {
        Carrier existingCarrier = carrierRepository.findById(carrierId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with ID: " + carrierId));

        // Allowed fields to update
        Set<String> allowedFields = Set.of("name", "rfc", "razonSocial", "caat");

        updates.forEach((key, value) -> {
            if (!allowedFields.contains(key)) {
                // Ignore fields not allowed to be changed
                return;
            }

            try {
                Field field = Carrier.class.getDeclaredField(key);
                field.setAccessible(true);

                if (value == null) {
                    // For these String fields, allow nulling except maybe name? (you can decide)
                    field.set(existingCarrier, null);
                    return;
                }

                String stringValue = value.toString();

                // Validate and set according to field
                switch (key) {
                    case "name":
                        if (stringValue.trim().isEmpty()) {
                            throw new IllegalArgumentException("Name cannot be empty");
                        }
                        field.set(existingCarrier, stringValue);
                        break;

                    case "rfc":
                        field.set(existingCarrier, stringValue);
                        break;

                    case "caat":
                        if (stringValue.length() != 4) {
                            throw new IllegalArgumentException("CAAT must be exactly 4 characters");
                        }
                        field.set(existingCarrier, stringValue);
                        break;

                    case "razonSocial":
                        // No specific validation, just set
                        field.set(existingCarrier, stringValue);
                        break;

                    default:
                        // Should never happen because of allowedFields check
                        break;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to update field '" + key + "': " + e.getMessage());
            }
        });

        return new CarrierDetailsDTO(carrierRepository.save(existingCarrier));
    }

    @Transactional
    public void deleteCarrier(Long carrierId) {
        if (carrierId == null) {
            throw new IllegalArgumentException("Carrier ID cannot be null");
        }
        if (!carrierRepository.existsById(carrierId)) {
            throw new IllegalArgumentException("Carrier with ID " + carrierId + " does not exist");
        }

        Carrier carrier = carrierRepository.getCarrierByCarrierId(carrierId);

//        Get auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ViazUserDetails viazUserDetails = (ViazUserDetails) auth.getPrincipal();
        ViazUser currentUser = userRepository.findByUsername(viazUserDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        carrier.softDelete(currentUser);
        carrierRepository.save(carrier);
    }

    public String getCarrierNameById(Long carrierId) {
        return carrierRepository.getCarrierNameByCarrierId(carrierId);
    }
}

