package com.viaz.dashboard.shipment;

import com.viaz.dashboard.s3.S3Service;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.*;

@RestController
@RequestMapping(path = "api/shipment")
public class ShipmentController {
    private final ShipmentService shipmentService;
    private final S3Service s3Service;

    public ShipmentController(ShipmentService shipmentService, S3Service s3Service) {
        this.shipmentService = shipmentService;
        this.s3Service = s3Service;
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


    @PostMapping(value = "/upload-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        return shipmentService.uploadExcel(file);
    }

    @PostMapping(value = "/{viazId}/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadFileToShipment(@PathVariable String viazId, @RequestParam("file") MultipartFile file) {
        shipmentService.uploadFileToShipment(viazId,file);
    }

    @GetMapping("/{viazId}/download-file/{filename}")
    public MultipartFile getFilsFromShipment(@PathVariable String viazId, @PathVariable String filename) {
        return shipmentService.getFileFromShipment(viazId,filename);
    }
}
