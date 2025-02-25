package com.viaz.dashboard.jwt.blacklist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    boolean existsByToken(String token);
    List<BlacklistedToken> findByExpirationDateBefore(Date date);
}
