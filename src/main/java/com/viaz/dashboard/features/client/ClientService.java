package com.viaz.dashboard.features.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ClientService {


    private final ClientRepository clientRepository;

    public List<Client> getPatios() {
        return clientRepository.findAll();
    }
}
