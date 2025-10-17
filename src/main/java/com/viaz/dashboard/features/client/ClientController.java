package com.viaz.dashboard.features.client;

import com.viaz.dashboard.features.client.dto.ClientResponseDTO;
import com.viaz.dashboard.features.client.dto.ClientRequestDTO;
import com.viaz.dashboard.features.client.dto.ClientResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/client")
public class ClientController {

    private final ClientService clientService;
    
    
    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication,'CLIENT_WRITE')")
    @PostMapping(value = "/create-client")
    public ResponseEntity<Map<String, Object>> createClient(@RequestBody ClientRequestDTO client) {

        clientService.createClient(client);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Client created successfully ");
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication,'SHIPMENT_WRITE')")
    @GetMapping("/get-clients")
    public List<ClientResponseDTO> getClients() {
        return clientService.getClients();
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication,'CLIENT_WRITE')")
    @GetMapping("/get-clients-with-sensitive-fields")
    public List<ClientResponseDTO> getClientsWithSensitiveFields() {
        return clientService.getClientsWithSensitiveFields();
    }


    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication,'CLIENT_WRITE')")
    @PatchMapping("/update-client/{clientId}")
    public ClientResponseDTO UpdateClient(@PathVariable Long clientId, @RequestBody Map<String, Object> updates) {
        return clientService.updateClient(clientId, updates);

    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'CLIENT_DELETE')")
    @DeleteMapping("/delete-client/{clientId}")
    public ResponseEntity<Map<String, Object>> deleteClient(@PathVariable Long clientId) {
        clientService.deleteClient(clientId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Client deleted successfully ");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
