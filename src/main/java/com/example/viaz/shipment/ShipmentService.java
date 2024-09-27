package com.example.viaz.shipment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;

    @Autowired
    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public List<Shipment> getShipments() {
		return shipmentRepository.findAll();
	}

    public Shipment addShipment(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }



}
