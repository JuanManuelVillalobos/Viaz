package com.viaz.dashboard.scheduled;

import com.viaz.dashboard.auth.AuthService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserCleanupService {

    private final AuthService authService;

    public UserCleanupService(AuthService authService) {
        this.authService = authService;
    }

    @Scheduled(fixedRate = 3600000 * 24) // Runs every day
    public void cleanUpExpiredTokens() {
        authService.deleteExpiredInvites();
    }
}