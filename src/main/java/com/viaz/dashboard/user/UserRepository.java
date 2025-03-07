package com.viaz.dashboard.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<ViazUser, Long> {
    ViazUser findByUsername(String username);

    List<ViazUser> findByPasswordIsNull();

    ViazUser findByEmail(String email);

    void deleteByUserId(Long userId);
}
