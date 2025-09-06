package com.viaz.dashboard.config;

import com.viaz.dashboard.entity.user.UserRepository;
import com.viaz.dashboard.entity.user.ViazUser;
import com.viaz.dashboard.entity.user.userDetails.ViazUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    
    @Bean
    public AuditorAware<ViazUser> auditorProvider(UserRepository userRepository) {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }
            Object principal = authentication.getPrincipal();
            if (principal instanceof ViazUserDetails) { // Check for your specific UserDetails
                return Optional.of(((ViazUserDetails) principal).viazUser()); // Get the underlying ViazUser
            }
            // Fallback for other UserDetails types if necessary, though ViazUserDetails is expected
            if (principal instanceof UserDetails) {
                // If for some reason it's a generic UserDetails but not ViazUserDetails,
                // you might still want to try to find the ViazUser by username.
                // However, given your setup, this path should ideally not be taken for authenticated ViazUsers.
                String username = ((UserDetails) principal).getUsername();
                return userRepository.findByEmail(username); // Assuming email is the username
            }
            return Optional.empty();
        };
    }
}
