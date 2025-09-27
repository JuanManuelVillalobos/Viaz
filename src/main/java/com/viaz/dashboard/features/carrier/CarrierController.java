package com.viaz.dashboard.features.carrier;

import com.viaz.dashboard.features.carrier.dto.CarrierDetailsDTO;
import com.viaz.dashboard.features.carrier.dto.CarrierRequestDTO;
import com.viaz.dashboard.features.carrier.dto.CarrierResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/carrier")
public class CarrierController {

    private final CarrierService carrierService;

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication,'CARRIER_WRITE')")
    @PostMapping(value = "/create-carrier")
    public ResponseEntity<Map<String, Object>> createCarrier(@RequestBody CarrierRequestDTO carrier) {

        carrierService.createCarrier(carrier);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Carrier created successfully ");
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication,'SHIPMENT_WRITE')")
    @GetMapping("/get-carriers")
    public List<CarrierResponseDTO> getCarriers() {
        return carrierService.getCarriers();
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication,'CARRIER_WRITE')")
    @GetMapping("/get-carriers-with-sensitive-fields")
    public List<CarrierDetailsDTO> getCarriersWithSensitiveFields() {
        return carrierService.getCarriersWithSensitiveFields();
    }


    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication,'CARRIER_WRITE')")
    @PatchMapping("/update-carrier/{carrierId}")
    public CarrierDetailsDTO UpdateCarrier(@PathVariable Long carrierId, @RequestBody Map<String, Object> updates) {
        return carrierService.updateCarrier(carrierId, updates);

    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'CARRIER_DELETE')")
    @DeleteMapping("/delete-carrier/{carrierId}")
    public ResponseEntity<Map<String, Object>> deleteCarrier(@PathVariable Long carrierId) {
        carrierService.deleteCarrier(carrierId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Carrier deleted successfully ");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


//    public ResponseEntity<Map<Object, String>> getCarrierRatingStatistics() {
//
//        return null;
//    }

}
