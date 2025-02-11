package com.viaz.dashboard.auth.userDetails;

import com.viaz.dashboard.auth.ViazUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private final ViazUser viazUser;

    public UserPrincipal(ViazUser viazUser) {
        this.viazUser = viazUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of();
        return Collections.singleton(new SimpleGrantedAuthority("USER")) ;
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
//        return UserDetails.super.isAccountNonExpired();
        // TODO
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
//        return UserDetails.super.isAccountNonLocked();
        // TODO
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
//        return UserDetails.super.isCredentialsNonExpired();
        // TODO
        return true;
    }

    @Override
    public boolean isEnabled() {
//        return UserDetails.super.isEnabled();
        // TODO
        return true;
    }
}
