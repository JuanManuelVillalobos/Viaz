package com.viaz.dashboard.scheduled;

import com.viaz.dashboard.jwt.blacklist.BlacklistService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TokenCleanupScheduler {
    private final BlacklistService blacklistService;

    public TokenCleanupScheduler(BlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }

    @Scheduled(fixedRate = 3600000) // Runs every hour
    public void cleanUpExpiredTokens() {
        blacklistService.removeExpiredTokens();
    }
}
