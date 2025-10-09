package com.viaz.dashboard.features.shipment.subscription;


import com.viaz.dashboard.features.notification.NotificationService;
import com.viaz.dashboard.features.shipment.Shipment;
import com.viaz.dashboard.features.user.UserService;
import com.viaz.dashboard.features.user.ViazUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentSubscriptionService {


    private final NotificationService notificationService;
    private final ShipmentSubscriptionRepository shipmentSubscriptionRepository;
    private final UserService userService;

    public void notifySubscribers(Shipment shipment, String message) {
        List<ShipmentSubscription> subscriptions = shipmentSubscriptionRepository.findByShipmentID(shipment.getFullViazId());

        for (ShipmentSubscription subscription : subscriptions) {
            ViazUser user = subscription.getUser();

            notificationService.createNotification(user, message);
        }
    }


    public void subscribeUserToShipment(Long userid, String viazId) {
        if (userid == null || viazId == null) {
            throw new IllegalArgumentException("User id or shipment id cannot be null");
        }

        ShipmentSubscription shipmentSubscription = new ShipmentSubscription();
        shipmentSubscription.setUser(userService.getUserById(userid));
        shipmentSubscription.setShipmentID(viazId);
        shipmentSubscriptionRepository.save(shipmentSubscription);
    }

    @Modifying
    @Transactional
    public void unsubscribeUserFromShipment(ViazUser oldUser, String viazId) {
        if (oldUser == null || viazId == null) {
            throw new IllegalArgumentException("User id or shipment id cannot be null");
        }

        shipmentSubscriptionRepository.findByShipmentIDAndUser(viazId, oldUser);

    }
}
