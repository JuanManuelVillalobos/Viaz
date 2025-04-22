package com.viaz.dashboard.user.userDetails;

import com.viaz.dashboard.user.ViazUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public record ViazUserDetails(ViazUser viazUser) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (viazUser.getRoles() == null || viazUser.getRoles().isEmpty()) {
            // Fallback to 'default' role if no roles assigned
            return java.util.Collections.singletonList(new SimpleGrantedAuthority("USER"));
        }
        return viazUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return viazUser.getPassword();
    }

    @Override
    public String getUsername() {
        return viazUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return viazUser.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return viazUser.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return viazUser.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return viazUser.isEnabled();
    }

    /**
     * Get the underlying ViazUser entity
     *
     * @return the ViazUser
     */
    @Override
    public ViazUser viazUser() {
        return viazUser;
    }
}
