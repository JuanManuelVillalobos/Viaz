package com.example.viaz.shipment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/shipment")
public class ShipmentController {
    private final ShipmentService shipmentService;

    @Autowired
    public ShipmentController(ShipmentService shipmentService) {this.shipmentService = shipmentService;}

    @GetMapping
    public List<Shipment> getShipments() {return shipmentService.getShipments();}








}
