package com.viaz.dashboard.features.client;

import com.viaz.dashboard.exception.exceptions.ResourceNotFoundException;
import com.viaz.dashboard.features.client.dto.ClientRequestDTO;
import com.viaz.dashboard.features.client.dto.ClientResponseDTO;
import com.viaz.dashboard.features.shipment.ShipmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ShipmentRepository shipmentRepository;

    public Optional<Client> getOptionalByName(String clientName) {
        return clientRepository.getClientByName(clientName);
    }

    public void createClient(ClientRequestDTO client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }

        if (clientRepository.getClientByName(client.name()).isPresent()) {
            throw new IllegalArgumentException("Client with name " + client.name() + " already exists.");
        }

        Client newclient = new Client(client.name());

        clientRepository.save(newclient);
    }

    public List<ClientResponseDTO> getClients() {
        return clientRepository.findAll().stream().map(ClientResponseDTO::new).collect(java.util.stream.Collectors.toList());
    }

    public List<ClientResponseDTO> getClientsWithSensitiveFields() {
        return clientRepository.findAll().stream().map(ClientResponseDTO::new).collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public void deleteClient(Long clientId) {
         if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }
        if (!clientRepository.existsById(clientId)) {
            throw new IllegalArgumentException("Client with ID " + clientId + " does not exist");
        }

        if(shipmentRepository.existsShipmentByClientClientId(clientId)){
            throw new IllegalStateException("Cannot delete client with shipments");
        }

        clientRepository.deleteByClientId(clientId);
    }

    public ClientResponseDTO updateClient(Long clientId, Map<String, Object> updates) {
        Client existingClient = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + clientId));

        // Allowed fields to update
        Set<String> allowedFields = Set.of("name");

        updates.forEach((key, value) -> {
            if (!allowedFields.contains(key)) {
                // Ignore fields not allowed to be changed
                return;
            }

            try {
                Field field = Client.class.getDeclaredField(key);
                field.setAccessible(true);

                if (value == null) {
                    // For these String fields, allow nulling except maybe name? (you can decide)
                    field.set(existingClient, null);
                    return;
                }

                String stringValue = value.toString();

                // Validate and set according to field
                switch (key) {
                    case "name":
                        if (stringValue.trim().isEmpty()) {
                            throw new IllegalArgumentException("Name cannot be empty");
                        }
                        field.set(existingClient, stringValue);
                        break;

                    default:
                        // Should never happen because of allowedFields check
                        break;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to update field '" + key + "': " + e.getMessage());
            }
        });

        return new ClientResponseDTO(clientRepository.save(existingClient));
    }

    public Client getClientByNameOrThrow(String name) {
        return clientRepository.getClientByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found: " + name));
    }

    public String getClientNameById(Long clientId) {
        return clientRepository.getClientNameByClientId(clientId);
    }
}
