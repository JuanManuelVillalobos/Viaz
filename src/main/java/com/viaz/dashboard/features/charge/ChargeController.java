package com.viaz.dashboard.features.charge;

import com.viaz.dashboard.features.charge.dto.ChargeTypeRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/charge")
public class ChargeController {

    private final ChargeService chargeService;

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_WRITE')")
    @GetMapping(value = "/get-charge-types")
    public List<ChargeType> getIncidentTypes() {
        return chargeService.getChargeTypes();
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'CHARGE_MANAGE')")
    @PostMapping(value = "/create-charge-type")
    public ResponseEntity<Map<String, Object>> createChargeType(@RequestBody ChargeTypeRequestDTO chargeTypeRequest){
        chargeService.createChargeOrThrow(chargeTypeRequest.name(), chargeTypeRequest.penalty());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Charge type created successfully " + HtmlUtils.htmlEscape(chargeTypeRequest.name()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'CHARGE_MANAGE')")
    @DeleteMapping(value = "/delete-charge-type/{chargeId}")
    public ResponseEntity<Map<String, Object>> deleteIncidentType(@PathVariable Long chargeId) {
        chargeService.deleteChargeType(chargeId);

         Map<String, Object> response = new HashMap<>();
        response.put("message", "Charge type deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
