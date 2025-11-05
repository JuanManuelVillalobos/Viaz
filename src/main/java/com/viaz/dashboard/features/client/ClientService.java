package com.viaz.dashboard.features.client;

import com.viaz.dashboard.exception.exceptions.ResourceNotFoundException;
import com.viaz.dashboard.features.client.dto.ClientRequestDTO;
import com.viaz.dashboard.features.client.dto.ClientResponseDTO;
import com.viaz.dashboard.features.user.UserRepository;
import com.viaz.dashboard.features.user.ViazUser;
import com.viaz.dashboard.features.user.userDetails.ViazUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepository userRepository;

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
        return clientRepository.findAll().stream().filter(client -> !client.isDeleted()).map(ClientResponseDTO::new).collect(java.util.stream.Collectors.toList());
    }

    public List<ClientResponseDTO> getClientsWithSensitiveFields() {
        return clientRepository.findAll().stream().filter(client -> !client.isDeleted()).map(ClientResponseDTO::new).collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public void deleteClient(Long clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }
        if (!clientRepository.existsById(clientId)) {
            throw new IllegalArgumentException("Client with ID " + clientId + " does not exist");
        }

        Client client = clientRepository.getClientByClientId(clientId);

        //        Get auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ViazUserDetails viazUserDetails = (ViazUserDetails) auth.getPrincipal();
        ViazUser currentUser = userRepository.findByUsername(viazUserDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));


        client.softDelete(currentUser);
        clientRepository.save(client);
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
                // Should never happen because of allowedFields check
                if (key.equals("name")) {
                    if (stringValue.trim().isEmpty()) {
                        throw new IllegalArgumentException("Name cannot be empty");
                    }
                    field.set(existingClient, stringValue);
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
