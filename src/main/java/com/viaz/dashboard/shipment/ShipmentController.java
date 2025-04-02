package com.viaz.dashboard.shipment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.*;

@RestController
@RequestMapping(path = "api/shipment")
public class ShipmentController {
    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }


    @GetMapping("/get-all-shipments")
    public List<Shipment> getShipments() {
        return shipmentService.getShipments();
    }

    @GetMapping("get-pending-shipments")
    public List<Shipment> getPendingShipments() { return shipmentService.getPendingShipments(); }

    @GetMapping("/get-shipment/{viazId}")
    public Shipment getShipment(@PathVariable("viazId") String viazId) {
        return shipmentService.getShipmentById(viazId);
    }

    @PostMapping("/create-shipment")
    public Shipment createShipment(@RequestBody Shipment shipment) {
        return shipmentService.createShipment(shipment);
    }


    @PatchMapping(path = "/update-shipment/{viazId}")
    public Shipment updateShipment(
            @PathVariable("viazId") String viazId,
            @RequestBody Map<String, Object> updates) {
        return shipmentService.updateShipment(viazId, updates);
    }

    @DeleteMapping(path = "/delete-shipment/{viazId}")
    public void deleteShipment(@PathVariable("viazId") String viazId) {
        shipmentService.deleteShipment(viazId);
    }


    @PostMapping("/upload-excel")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        return shipmentService.uploadExcel(file);
    }
}
