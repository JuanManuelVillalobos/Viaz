package com.viaz.dashboard.entity.user.userDetails;

import com.viaz.dashboard.entity.permission.Permission;
import com.viaz.dashboard.entity.role.Role;
import com.viaz.dashboard.entity.user.ViazUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public record ViazUserDetails(ViazUser viazUser) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        // Add role-based authorities
        for (Role role : viazUser.getRoles()) {
            if (!role.isCosmetic()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

                // Add permission-based authorities from this role
                for (Permission permission : role.getPermissions()) {
                    authorities.add(new SimpleGrantedAuthority(permission.getName()));
                }
            }
        }

        return authorities;
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
