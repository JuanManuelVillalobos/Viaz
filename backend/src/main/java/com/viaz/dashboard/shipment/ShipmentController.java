package com.viaz.dashboard.shipment;

import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/shipment")
public class ShipmentController {
    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping("/")
    public String hello(){
        return "Hello World";
    }

    @GetMapping("/get-shipments")
    public List<Shipment> getShipments() {
        return shipmentService.getShipments();
    }

    @GetMapping("/get-shipments/last5")
    public List<Shipment> getLastFiveShipments() {
        return shipmentService.getLastFiveShipments();
    }

    @GetMapping("/get-shipment/{viazId}")
    public Shipment getShipment(@PathVariable("viazId") String viazId) {
        return shipmentService.getShipmentById(viazId);
    }

    @PostMapping("/create-shipment")
    public Shipment createShipment(@RequestBody Shipment shipment) {
        return shipmentService.createShipment(shipment);
    }

    @DeleteMapping(path = "/{viazId}")
    public void deleteStudent(@PathVariable("viazId") Long viazId) {
        shipmentService.deleteShipment(viazId);
    }


    @PatchMapping(path = "/update-shipment/{viazId}")
    public Shipment updateShipment(
            @PathVariable("viazId") String viazId,
            @RequestBody Map<String, Object> updates) {
        Shipment existingShipment = shipmentService.getShipmentById(viazId);
        updates.forEach((key, value) -> {
            try {
                Field field = Shipment.class.getDeclaredField(key);
                field.setAccessible(true);
                field.set(existingShipment, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                //TODO Handle Error
            }
        });
        return shipmentService.getShipmentById(viazId);
    }


}
