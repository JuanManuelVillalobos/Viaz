package com.viaz.dashboard.user;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
public interface UserRepository extends JpaRepository<ViazUser, Long> {
    Optional<ViazUser> findByUsername(String username);

    List<ViazUser> findByPasswordIsNull();

    Optional<ViazUser> findByEmail(String email);

    void deleteByUserId(Long userId);
    
    @Query("SELECT u FROM ViazUser u WHERE u.lastLoginDate < :date")
    List<ViazUser> findInactiveUsersSince(LocalDateTime date);
    
    @Query("SELECT u FROM ViazUser u WHERE u.credentialsNonExpired = false")
    List<ViazUser> findUsersWithExpiredCredentials();
    
    @Query("SELECT u FROM ViazUser u WHERE u.passwordChangedDate IS NULL OR u.passwordChangedDate < :date")
    List<ViazUser> findUsersWithPasswordOlderThan(LocalDateTime date);
    
    @Modifying
    @Query("UPDATE ViazUser u SET u.accountNonLocked = false WHERE u.failedLoginAttempts >= :maxAttempts")
    void lockAccountsWithExcessiveFailedAttempts(int maxAttempts);
    
    @Modifying
    @Query("UPDATE ViazUser u SET u.enabled = false WHERE u.lastLoginDate < :date")
    void disableInactiveUsersSince(LocalDateTime date);
    
    List<ViazUser> findByStatus(ViazUser.UserStatus status);
}
