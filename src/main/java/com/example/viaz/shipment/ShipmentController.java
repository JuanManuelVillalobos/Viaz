package com.example.viaz.shipment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/shipment")
public class ShipmentController {
    private static final Logger log = LoggerFactory.getLogger(ShipmentController.class);
    private final ShipmentService shipmentService;
    private final ShipmentRepository shipmentRepository;

    @Autowired
    public ShipmentController(ShipmentService shipmentService, ShipmentRepository shipmentRepository) {this.shipmentService = shipmentService;
        this.shipmentRepository = shipmentRepository;
    }

    @GetMapping
    public List<Shipment> getShipments() {return shipmentService.getShipments();}

    @PostMapping
    public Shipment createShipment(@RequestBody Shipment shipment) {
        return shipmentRepository.save(shipment);
    }

    @DeleteMapping(path = "{viazId}")
    public void deleteStudent(@PathVariable("viazId") Long viazId) {
        shipmentRepository.deleteById(viazId);
    }


    @PatchMapping(path = "{viasId}")
    public Shipment updateShipment(
        @PathVariable("viasId") Long viazId,
        @RequestBody Map<String, Object> updates) {
        Shipment existingShipment = shipmentRepository.findById(viazId).orElseThrow(() -> new IllegalArgumentException("Shipment not found"));
        updates.forEach((key, value) -> {
            try {
                Field field = Shipment.class.getDeclaredField(key);
                field.setAccessible(true);
                field.set(existingShipment, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
               //TODO Handle Error
            }
        });
    return shipmentRepository.save(existingShipment);
}



}
