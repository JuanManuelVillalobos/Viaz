package com.viaz.dashboard.user;

import com.viaz.dashboard.exception.exceptions.ResourceNotFoundException;
import com.viaz.dashboard.user.helpers.ViazUserDTO;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final int PASSWORD_EXPIRY_DAYS = 90;
    private static final int ACCOUNT_INACTIVE_DAYS = 180;
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public void addUserToRepository(ViazUser viazUser) {
        if (viazUser.getPassword() != null) {
            viazUser.setPassword(encoder.encode(viazUser.getPassword()));
            viazUser.setPasswordChangedDate(LocalDateTime.now());
        }
        
        // Assign default role if no roles assigned
        if (viazUser.getRoles() == null || viazUser.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            roleRepository.findByName("USER").ifPresentOrElse(
                roles::add,
                () -> roles.add(roleRepository.save(new Role("USER", "Standard user")))
            );
            viazUser.setRoles(roles);
        }
        
        userRepository.save(viazUser);
    }

    public ViazUserDTO convertToDTO(ViazUser user) {
        if (user == null) {
            return null;
        }
            
            // Convert roles to set of role names
            Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
                
            return new ViazUserDTO(
                user.getUserId(), 
                user.getUsername(), 
                user.getEmail(),
                user.getLastLoginDate(),
                user.getStatus(),
                roleNames
            );
    }

    public List<ViazUserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ViazUserDTO changePassword(String viazUserEmail, String newPassword) {
        ViazUser user = userRepository.findByEmail(viazUserEmail)
            .orElseThrow(() -> new ResourceNotFoundException("No user with email " + viazUserEmail));
            
        // Validate password complexity
        if (!isPasswordValid(newPassword)) {
            throw new IllegalArgumentException("Password does not meet complexity requirements");
        }
        
        String hashedPassword = encoder.encode(newPassword);
        user.setPassword(hashedPassword);
        user.recordPasswordChange();
        user.setCredentialsNonExpired(true);

        userRepository.save(user);

        return convertToDTO(user);
    }

    private boolean isPasswordValid(String password) {
        // Check for minimum length
        if (password.length() < 8) return false;

        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) return false;

        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) return false;

        // Check for at least one digit
        if (!password.matches(".*\\d.*")) return false;

        // Check for at least one special character
        return password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
    }

    public ViazUser createUnValidatedUser(ViazUser user) {
        // Set default account status
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        
        // Assign default role
        Set<Role> roles = new HashSet<>();
        roleRepository.findByName("USER").ifPresentOrElse(
            roles::add,
            () -> roles.add(roleRepository.save(new Role("USER", "Standard user")))
        );
        user.setRoles(roles);
        
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
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("No user with email " + email))
            .getUsername();
    }
    
    public void recordSuccessfulLogin(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.recordSuccessfulLogin();
            userRepository.save(user);
        });
    }
    
    public void incrementFailedLoginAttempts(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.incrementFailedLoginAttempts();
            userRepository.save(user);
        });
    }
    
    public void unlockAccount(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setAccountNonLocked(true);
            user.resetFailedLoginAttempts();
            userRepository.save(user);
        });
    }
    
    public void assignRoleToUser(Long userId, String roleName) {
        ViazUser user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
            
        Role role = roleRepository.findByName(roleName)
            .orElseGet(() -> roleRepository.save(new Role(roleName)));
            
        user.getRoles().add(role);
        userRepository.save(user);
    }
    
    public void removeRoleFromUser(Long userId, String roleName) {
        ViazUser user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
            
        roleRepository.findByName(roleName).ifPresent(role -> {
            user.getRoles().remove(role);
            userRepository.save(user);
        });
    }
    
    @Scheduled(cron = "0 0 0 * * *") // Run daily at midnight
    public void checkAndExpireStalePasswords() {
        LocalDateTime expiryDate = LocalDateTime.now().minusDays(PASSWORD_EXPIRY_DAYS);
        List<ViazUser> usersWithStalePasswords = userRepository.findUsersWithPasswordOlderThan(expiryDate);
        
        usersWithStalePasswords.forEach(user -> {
            user.setCredentialsNonExpired(false);
            userRepository.save(user);
        });
    }
    
    @Scheduled(cron = "0 0 0 * * 0") // Run weekly on Sunday at midnight
    public void disableInactiveAccounts() {
        LocalDateTime inactiveDate = LocalDateTime.now().minusDays(ACCOUNT_INACTIVE_DAYS);
        userRepository.disableInactiveUsersSince(inactiveDate);
    }
}
