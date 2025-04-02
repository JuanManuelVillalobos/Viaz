package com.viaz.dashboard.user;

import com.viaz.dashboard.user.helpers.ViazUserDTO;
import jakarta.transaction.Transactional;
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

    public ViazUserDTO changePassword(String viazUserEmail, String newPassword) throws Exception {
        ViazUser user = userRepository.findByEmail(viazUserEmail);
        if (user == null) {
            throw new Exception("No user with email " + viazUserEmail);
        }
        String hashedPassword = encoder.encode(newPassword);
        user.setPassword(hashedPassword);

        userRepository.save(user);

        return convertToDTO(user);
    }

    public ViazUser createUnValidatedUser(ViazUser user) {
        return userRepository.save(user);
    }

    public void deleteUsersWithNullPassword() {
        List<ViazUser> usersWithNullPassword = userRepository.findByPasswordIsNull();
        userRepository.deleteAll(usersWithNullPassword);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteByUserId(userId);
    }


    public String getUsernameByEmail(String email) {
        return userRepository.findByEmail(email).getUsername();
    }

}
