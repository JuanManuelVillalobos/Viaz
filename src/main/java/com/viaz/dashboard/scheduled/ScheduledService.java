package com.viaz.dashboard.scheduled;

import com.viaz.dashboard.auth.AuthService;
import com.viaz.dashboard.features.shipment.ShipmentService;
import com.viaz.dashboard.features.user.UserService;
import com.viaz.dashboard.jwt.blacklist.BlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ScheduledService {

    private final UserService userService;
    private final AuthService authService;
    private final BlacklistService blacklistService;
    private final ShipmentService shipmentService;

    @Scheduled(cron = "0 0 0 * * *") // Run daily at midnight
    public void expirePasswords() {
        userService.checkAndExpireStalePasswords();
    }

    @Scheduled(cron = "0 0 0 * * 0") // Run weekly on Sunday at midnight
    public void disableInactiveAccounts() {
        userService.disableInactiveAccounts();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredInvites() {
        authService.deleteExpiredInvites();
    }

    @Scheduled(cron = "0 0 */1 * * *") // Run every hour
    public void removeExpiredBlacklistedTokens() {
        blacklistService.removeExpiredTokens();
    }

    @Scheduled(cron = "0 0 * * * *") // Run every hour
    public void notifyAdminsOfDelayedUnload() {
        shipmentService.notifyAdminsOfDelayedUnload();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void notifyCustomerServiceThatCleaningManuovreIsPending() {
        shipmentService.notifyCustomerServiceThatCleaningManuovreIsPending();
    }


}
