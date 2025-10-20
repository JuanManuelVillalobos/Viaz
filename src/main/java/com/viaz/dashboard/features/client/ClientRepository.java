package com.viaz.dashboard.features.client;

import com.viaz.dashboard.features.carrier.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> getClientByName(String name);

    void deleteByClientId(Long clientId);


    @Query("SELECT c.name FROM Client c WHERE c.clientId = :clientId")
    String getClientNameByClientId(@Param("clientId") Long clientId);
}
