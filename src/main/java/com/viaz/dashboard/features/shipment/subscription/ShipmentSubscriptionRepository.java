package com.viaz.dashboard.features.shipment.subscription;

import com.viaz.dashboard.features.user.ViazUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentSubscriptionRepository extends JpaRepository<ShipmentSubscription, Long> {

    List<ShipmentSubscription> findByShipmentID(String shipmentID);


    List<ShipmentSubscription> findByShipmentIDAndUser(String shipmentID, ViazUser user);
}
