package com.viaz.dashboard.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUserToRepository(ViazUser viazUser) {
        viazUser.setPassword(encoder.encode(viazUser.getPassword()));
        userRepository.save(viazUser);
    }

    public ViazUserDTO convertToDTO(ViazUser user) {
        if (user == null) {
            return null;
        }
        return new ViazUserDTO(user.getUserId(), user.getUsername(), user.getEmail());
    }

    public List<ViazUserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}
