package com.viaz.dashboard.auth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<ViazUser, Long> {
    ViazUser findByUsername(String username);
}
