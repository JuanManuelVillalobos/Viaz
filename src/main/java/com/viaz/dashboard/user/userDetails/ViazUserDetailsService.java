package com.viaz.dashboard.user.userDetails;

import com.viaz.dashboard.user.ViazUser;
import com.viaz.dashboard.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ViazUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;

    public ViazUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ViazUser viazUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (viazUser == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new ViazUserDetails(viazUser);
    }
}