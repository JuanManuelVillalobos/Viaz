package com.viaz.dashboard.jwt.blacklist;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class BlacklistService {
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public BlacklistService(BlacklistedTokenRepository blacklistedTokenRepository) {
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    @CacheEvict(value = "tokenBlacklist", key = "#token")
    public void addToBlacklist(String token, Instant expirationDate) {
        log.debug("Adding token to blacklist with expiration date: {}", expirationDate);
        blacklistedTokenRepository.save(new BlacklistedToken(token, expirationDate));
    }

    @Cacheable(value = "tokenBlacklist", key = "#token")
    public boolean isBlacklisted(String token) {
        log.debug("Checking if token is blacklisted: {}", token.substring(0, 10) + "...");
        return blacklistedTokenRepository.existsByToken(token);
    }

    @Transactional
    @CacheEvict(value = "tokenBlacklist", allEntries = true)
    public void removeExpiredTokens() {
        log.info("Running scheduled cleanup of expired blacklisted tokens");
        List<BlacklistedToken> expiredTokens = 
            blacklistedTokenRepository.findByExpirationDateBefore(LocalDateTime.now());
        
        if (!expiredTokens.isEmpty()) {
            blacklistedTokenRepository.deleteAll(expiredTokens);
            log.info("Removed {} expired tokens from blacklist", expiredTokens.size());
        }
    }
}
