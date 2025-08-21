package com.viaz.dashboard.entity.carrier;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/carrier")
public class CarrierController {

    private final CarrierService carrierService;

    @PostMapping("/create-carrier")
    public ResponseEntity<String> createCarrier(@RequestBody Carrier carrier) {
        return carrierService.createCarrier(carrier);
    }

    @GetMapping("/get-carriers")
    public List<Carrier> getCarriers() {
        return carrierService.getCarriers();
    }

    @PostMapping("/change-name/{carrierId}/{newname}")
    public ResponseEntity<String> changeCarrierName(@PathVariable Long carrierId, @PathVariable String newname) {
        return carrierService.changeCarrierName(carrierId, newname);
    }

}
