package com.viaz.dashboard.jwt.blacklist;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BlacklistService {
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public BlacklistService(BlacklistedTokenRepository blacklistedTokenRepository) {
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    public void addToBlacklist(String token, Date expirationDate) {
        blacklistedTokenRepository.save(new BlacklistedToken(token, expirationDate));
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }

    @Transactional
    public void removeExpiredTokens() {
        List<BlacklistedToken> expiredTokens = blacklistedTokenRepository.findByExpirationDateBefore(new Date());
        blacklistedTokenRepository.deleteAll(expiredTokens);
    }
}
