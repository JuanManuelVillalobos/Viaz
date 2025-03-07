package com.viaz.dashboard.user.userDetails;

import com.viaz.dashboard.user.ViazUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class ViazUserDetails implements UserDetails {

    private final ViazUser viazUser;

    public ViazUserDetails(ViazUser viazUser) {
        this.viazUser = viazUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return viazUser.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
        return Collections.singletonList(new SimpleGrantedAuthority("ADMIN"));
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
