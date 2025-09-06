package com.viaz.dashboard.entity.shipment;

import com.viaz.dashboard.entity.rating.RatingService;
import com.viaz.dashboard.entity.shipment.charge.ShipmentCharge;
import com.viaz.dashboard.entity.shipment.charge.ShipmentChargeResponseDTO;
import com.viaz.dashboard.entity.shipment.helpers.ShipmentResponseDTO;
import com.viaz.dashboard.entity.shipment.helpers.ShipmentRequestDTO;
import com.viaz.dashboard.entity.shipment.incident.ShipmentIncident;
import com.viaz.dashboard.entity.shipment.incident.ShipmentIncidentResponseDTO;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/get-last-month-total-shipments")
    public Integer getLastMonthTotalShipments() {
        return shipmentService.getLastMonthTotalShipments();
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_READ')")
    @GetMapping("/get-all-shipments")
    public List<ShipmentResponseDTO> getShipments() {
        return shipmentService.getShipments();
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_READ')")
    @GetMapping("get-pending-shipments")
    public List<ShipmentResponseDTO> getPendingShipments() {
        return shipmentService.getPendingShipments();
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_READ')")
    @GetMapping("/get-shipment/{viazId}")
    public ShipmentResponseDTO getShipment(@PathVariable("viazId") String viazId) {
        return shipmentService.getShipmentByIdDTO(viazId);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_WRITE')")
    @PostMapping("/create-shipment")
    public ShipmentResponseDTO createShipment(@RequestBody ShipmentRequestDTO shipment) {
        return shipmentService.createShipment(shipment);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_WRITE')")
    @PatchMapping(path = "/update-shipment/{viazId}")
    public ShipmentResponseDTO updateShipment(
            @PathVariable("viazId") String viazId,
            @RequestBody Map<String, Object> updates) {
        return shipmentService.updateShipment(viazId, updates);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_DELETE')")
    @DeleteMapping(path = "/delete-shipment/{viazId}")
    public void deleteShipment(@PathVariable("viazId") String viazId) {
        shipmentService.deleteShipment(viazId);
    }


    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_WRITE')")
    @PostMapping(value = "/upload-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        return shipmentService.uploadExcel(file);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_WRITE')")
    @PostMapping(value = "/{viazId}/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFileToShipment(@PathVariable String viazId, @RequestParam("file") MultipartFile file) throws IOException {
        PutObjectResponse s3response = shipmentService.uploadFileToShipment(viazId, file);
        return ResponseEntity.ok("File uploaded successfully" + s3response);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_READ')")
    @GetMapping("/download-file/{viazId}/{objectKey}")
    public String getFilesFromShipment(@PathVariable String viazId, @PathVariable String objectKey) {
        return shipmentService.getFileFromShipment(viazId, objectKey);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_READ')")
    @GetMapping(value = "/{viazId}/list-objects")
    public List<String> getDocuments(@PathVariable String viazId) {
        return shipmentService.getDocuments(viazId);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_DELETE')")
    @DeleteMapping(value = "/delete-object/{viazId}/{objectKey}")
    public ResponseEntity<String> deleteObject(@PathVariable String viazId, @PathVariable String objectKey) {
        return shipmentService.deleteObject(viazId, objectKey);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_CLOSE')")
    @PostMapping(value = "/close-shipment/{viazId}/{rating}/{observations}")
    public ResponseEntity<Map<String, Object>> closeShipment(@NonNull @PathVariable String viazId, @NonNull @PathVariable Integer rating, @PathVariable String observations) {

        String shipmentViazId = shipmentService.closeShipment(viazId, rating, observations);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Shipment closed successfully " + shipmentViazId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_CLOSE')")
    @PostMapping(value = "/reopen-shipment/{viazId}")
    public ResponseEntity<Map<String, Object>> reopenShipment(@NonNull @PathVariable String viazId) {
        shipmentService.reopenShipment(viazId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Shipment reopened successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_WRITE')")
    @PostMapping(value = "/add-incident/{viazId}/{incidentId}/{comment}")
    public ResponseEntity<Map<String, Object>> addIncidentToShipment(@NonNull @PathVariable String viazId, @NonNull @PathVariable Long incidentId, @NonNull @PathVariable String comment) {
        shipmentService.addIncidentToShipment(viazId, incidentId, comment);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Incident added to shipment successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_WRITE')")
    @PostMapping(value = "/add-charge/{viazId}/{chargeId}/{comment}")
    public ResponseEntity<Map<String, Object>> addChargeToShipment(@NonNull @PathVariable String viazId, @NonNull @PathVariable Long chargeId, @NonNull @PathVariable String comment) {
        shipmentService.addChargeToShipment(viazId, chargeId, comment);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Charge added to shipment successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_READ')")
    @GetMapping(value = "get-incidents/{viazId}")
    public List<ShipmentIncidentResponseDTO> getIncidents(@PathVariable String viazId) {
        return shipmentService.getIncidentsByViazId(viazId);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_READ')")
    @GetMapping(value = "get-charges/{viazId}")
    public List<ShipmentChargeResponseDTO> getCharges(@PathVariable String viazId) {
        return shipmentService.getChargesByViazId(viazId);
    }
}
