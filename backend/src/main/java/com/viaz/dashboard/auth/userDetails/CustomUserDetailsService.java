package com.viaz.dashboard.auth.userDetails;

import com.viaz.dashboard.auth.ViazUser;
import com.viaz.dashboard.auth.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        ViazUser viazUser = userRepository.findByUsername(username);

        if (viazUser == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new UserPrincipal(viazUser);
    }

}
