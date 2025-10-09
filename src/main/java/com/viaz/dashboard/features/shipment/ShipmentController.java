package com.viaz.dashboard.features.shipment;

import com.viaz.dashboard.features.shipment.charge.dto.ShipmentChargeResponseDTO;
import com.viaz.dashboard.features.shipment.charge.ShipmentChargeService;
import com.viaz.dashboard.features.shipment.dto.ShipmentFilterOptionsDTO;
import com.viaz.dashboard.features.shipment.dto.ShipmentResponseDTO;
import com.viaz.dashboard.features.shipment.dto.ShipmentRequestDTO;
import com.viaz.dashboard.features.shipment.incident.ShipmentIncident;
import com.viaz.dashboard.features.shipment.incident.dto.ShipmentIncidentResponseDTO;
import com.viaz.dashboard.features.shipment.incident.ShipmentIncidentService;
import com.viaz.dashboard.features.shipment.incident.reschedule.ShipmentRescheduleService;
import com.viaz.dashboard.features.shipment.incident.reschedule.dto.RescheduleRequestDTO;
import com.viaz.dashboard.features.shipment.incident.reschedule.dto.ShipmentRescheduleResponseDTO;
import com.viaz.dashboard.features.shipment.subscription.ShipmentSubscriptionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;


import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "api/shipment")
public class ShipmentController {
    private final ShipmentService shipmentService;
    private final ShipmentIncidentService shipmentIncidentService;
    private final ShipmentChargeService shipmentChargeService;
    private final ShipmentSubscriptionService shipmentSubscriptionService;
    private final ShipmentRescheduleService shipmentRescheduleService;


    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_READ')")
    @GetMapping("/filters")
    public ShipmentFilterOptionsDTO getFilterOptions() {
        return shipmentService.getFilterOptions();
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_READ')")
    @GetMapping("/shipments")
    public Page<ShipmentResponseDTO> getShipments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) List<String> clients,
            @RequestParam(required = false) List<String> carriers,
            @RequestParam(required = false) String search
    ) {
        return shipmentService.getShipments(page, size, statuses, clients, carriers, search);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_READ')")
    @GetMapping("/open-shipments")
    public Page<ShipmentResponseDTO> getOpenShipments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) List<String> clients,
            @RequestParam(required = false) List<String> carriers,
            @RequestParam(required = false) String search
    ) {
        return shipmentService.getOpenShipments(page, size, statuses, clients, carriers, search);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_READ')")
    @GetMapping("/assigned-shipments")
    public Page<ShipmentResponseDTO> getAssignedShipments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) List<String> clients,
            @RequestParam(required = false) List<String> carriers,
            @RequestParam(required = false) String search
    ) {


        return shipmentService.getAssignedShipments(page, size, statuses, clients, carriers, search);
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
    public ShipmentResponseDTO updateShipment(@PathVariable("viazId") String viazId, @RequestBody Map<String, Object> updates) {
        return shipmentService.updateShipment(viazId, updates);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_DELETE')")
    @DeleteMapping(path = "/delete-shipment/{viazId}")
    public void deleteShipment(@PathVariable("viazId") String viazId) {
        shipmentService.deleteShipment(viazId);
    }


    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_RESTORE')")
    @PostMapping(path = "/restore-shipment/{viazId}")
    public void restoreShipment(@PathVariable("viazId") String viazId) {
        shipmentService.restoreShipment(viazId);
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
    @GetMapping(value = "/get-incidents/{viazId}")
    public List<ShipmentIncidentResponseDTO> getIncidents(@PathVariable String viazId) {
        return shipmentService.getIncidentsByViazId(viazId);
    }


    //    TODO FIX .ObjectOptimisticLockingFailureException
    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_INCIDENT_DELETE')")
    @DeleteMapping(value = "/delete-incident/{incidentId}")
    public ResponseEntity<Map<String, Object>> deleteIncident(@PathVariable Long incidentId) {

        shipmentService.deleteShipmentIncident(incidentId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Shipment charge deleted successfully ");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_READ')")
    @GetMapping(value = "/get-charges/{viazId}")
    public List<ShipmentChargeResponseDTO> getCharges(@PathVariable String viazId) {
        return shipmentService.getChargesByViazId(viazId);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_CHARGE_DELETE')")
    @DeleteMapping(value = "/delete-charge/{chargeId}")
    public ResponseEntity<Map<String, Object>> deleteCharge(@PathVariable Long chargeId) {

        shipmentService.deleteShipmentCharge(chargeId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Shipment charge deleted successfully ");
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_READ')")
    @PostMapping(value = "/subscribe-to-shipment/{userid}/{viazId}")
    public ResponseEntity<Map<String, Object>> subscribeToShipment(@PathVariable Long userid, @PathVariable String viazId) {
        shipmentSubscriptionService.subscribeUserToShipment(userid, viazId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Shipment subscription made successfully ");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_WRITE')")
    @PostMapping(value = "/change-shipment-status/{viazId}/")
    public ResponseEntity<Map<String, Object>> changeShipmentStatus(@PathVariable String viazId, @RequestParam("status") String status) {
        shipmentService.changeStatusOfShipment(viazId, status);


        Map<String, Object> response = new HashMap<>();
        response.put("message", "Status for shipment updates correctly");
        return new ResponseEntity<>(response, HttpStatus.OK);


    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_WRITE')")
    @PostMapping(value = "/create-shipment-variant/{viazId}")
    public ResponseEntity<Map<String, Object>> createShipmentVariant(@PathVariable String viazId) {
        String variantViazId = shipmentService.createVariantOfShipment(viazId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Variant shipment " + variantViazId + " successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_READ')")
    @GetMapping(value = "/get-variants/{viazId}")
    public List<String> getVariants(@PathVariable String viazId) {
        return shipmentService.getAllVariants(viazId);
    }


//    TODO Fix excel export
//    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_EXPORT')")
//    @GetMapping("/shipments/export")
//    public ResponseEntity<byte[]> exportShipments(
//            @RequestParam(required = false) List<String> statuses,
//            @RequestParam(required = false) List<String> clients,
//            @RequestParam(required = false) List<String> carriers,
//            @RequestParam(required = false) String idFrom,
//            @RequestParam(required = false) String idTo
//    ) throws IOException {
//        byte[] excelData = shipmentService.exportShipmentsToExcel(statuses, clients, carriers, idFrom, idTo);
//
//        // Generate a dynamic filename
//        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE); // e.g., 2025-09-27
//        String fileName = "shipments-export-" + currentDate + ".xlsx";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
//        headers.setContentDispositionFormData("attachment", fileName);
//
//        return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
//    }


    @PostMapping("/reschedule/{viazId}")
    public ResponseEntity<ShipmentIncidentResponseDTO> rescheduleShipment(
            @PathVariable String viazId,
            @RequestBody RescheduleRequestDTO request) {

        ShipmentIncident rescheduleIncident = shipmentRescheduleService.createRescheduleIncident(
                viazId, request.newUnloadTime(), request.comment());

        return ResponseEntity.ok(new ShipmentIncidentResponseDTO(rescheduleIncident));
    }

    @GetMapping("/get-reschedule-details/{viazId}")
    public ResponseEntity<ShipmentRescheduleResponseDTO> getRescheduleDetail(@PathVariable String viazId) {
        ShipmentRescheduleResponseDTO rescheduleDTO = shipmentRescheduleService.getRescheduleDetailByFullViazId(viazId);
        return ResponseEntity.ok(rescheduleDTO);
    }


    //    DEV
    @GetMapping(value = "/checkforoverdueshipments")
    public void checkForOverdueShipments() {
        shipmentService.notifyAdminsOfDelayedUnload();
    }


}
