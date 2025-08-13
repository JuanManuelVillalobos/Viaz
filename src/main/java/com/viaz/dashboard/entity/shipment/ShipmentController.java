package com.viaz.dashboard.entity.shipment;

import com.viaz.dashboard.entity.rating.RatingService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;


import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(path = "api/shipment")
public class ShipmentController {
    private final ShipmentService shipmentService;
    private final RatingService ratingService;

    public ShipmentController(ShipmentService shipmentService, RatingService ratingService) {
        this.shipmentService = shipmentService;
        this.ratingService = ratingService;
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

    @PostMapping(value = "/get-upload-url")
    public ResponseEntity<String> getUploadUrl() {
//        TODO
        return null;
    }

    @PostMapping(value = "/{viazId}/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFileToShipment(@PathVariable String viazId, @RequestParam("file") MultipartFile file) throws IOException {
        PutObjectResponse s3response = shipmentService.uploadFileToShipment(viazId,file);
        return ResponseEntity.ok("File uploaded successfully" + s3response);
    }


    @GetMapping("/download-file/{viazId}/{objectKey}")
    public String getFilesFromShipment(@PathVariable String viazId,@PathVariable String objectKey) {
        return shipmentService.getFileFromShipment(viazId, objectKey);
    }

    @GetMapping(value = "/{viazId}/list-objects")
    public List<String> getDocuments(@PathVariable String viazId) {
        return shipmentService.getDocuments(viazId);
    }

    @DeleteMapping(value = "/delete-object/{viazId}/{objectKey}")
    public ResponseEntity<String> deleteObject(@PathVariable String viazId,@PathVariable String objectKey) {
        return shipmentService.deleteObject(viazId, objectKey);
    }

//    @PostMapping(value = "/rate/{viazId}/{rating}")
//    public String rateShipment(@PathVariable String viazId, @PathVariable Integer rating){
//        return ratingService
//    }
}
