package com.viaz.dashboard.entity.carrier;

import com.viaz.dashboard.entity.carrier.helpers.CarrierDetailsDTO;
import com.viaz.dashboard.entity.carrier.helpers.CarrierRequestDTO;
import com.viaz.dashboard.entity.carrier.helpers.CarrierResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

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
    @PostMapping("/update-carrier/{carrierId}/")
    public ResponseEntity<Map<Object,String>> UpdateCarrier(@PathVariable Long carrierId, CarrierRequestDTO carrier) {

        carrierService.updateCarrier(carrierId, carrier);


        Map<Object, String> response = new HashMap<>();
        response.put("message", "Carrier updated successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Map<Object, String>> getCarrierRatingStatistics() {


        return null;
    }

}
