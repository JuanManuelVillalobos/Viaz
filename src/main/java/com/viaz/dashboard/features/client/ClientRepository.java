package com.viaz.dashboard.features.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> getClientByName(String name);


    @Query("SELECT c.name FROM Client c WHERE c.clientId = :clientId")
    String getClientNameByClientId(@Param("clientId") Long clientId);

    Client getClientByClientId(Long clientId);
}
