package com.viaz.dashboard.features.user;

import com.viaz.dashboard.email.EmailService;
import com.viaz.dashboard.features.role.Role;
import com.viaz.dashboard.features.role.RoleService;
import com.viaz.dashboard.features.role.dto.RoleCreationRequestDTO;
import com.viaz.dashboard.features.user.dto.UserSummaryDTO;
import com.viaz.dashboard.exception.exceptions.ResourceNotFoundException;
import com.viaz.dashboard.features.user.dto.ViazUserResponseDTO;
import com.viaz.dashboard.features.user.userDetails.ViazUserDetails;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final int PASSWORD_EXPIRY_DAYS = 180;
    private static final int ACCOUNT_INACTIVE_DAYS = 180;
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, RoleService roleService, EmailService emailService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.emailService = emailService;
    }

    @Transactional
    public ViazUser addUserToRepository(ViazUser viazUser) {
        if (viazUser.getPassword() != null) {
            viazUser.setPassword(encoder.encode(viazUser.getPassword()));
            viazUser.setPasswordChangedDate(Instant.now());
        }

        // Assign default role if no roles assigned
        if (viazUser.getRoles() == null || viazUser.getRoles().isEmpty()) {
            createDefaultUserIfNotPresent(viazUser);
        }

        return userRepository.save(viazUser);
    }

    private void createDefaultUserIfNotPresent(ViazUser viazUser) {
        Set<Role> roles = new HashSet<>();
        roleService.findByName("User").ifPresentOrElse(roles::add, () -> roleService.createRole(new RoleCreationRequestDTO("User", "Basic user with read access", Set.of("SHIPMENT_READ"))));
        viazUser.setRoles(roles);
    }

    public ViazUserResponseDTO convertToDTO(ViazUser user) {
        if (user == null) {
            return null;
        }

        // Convert roles to set of role names
        Set<String> roleNames = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        return new ViazUserResponseDTO(user.getUserId(), user.getUsername(), user.getEmail(), user.getLastLoginDate(), user.getStatus(), roleNames);
    }

    public List<ViazUserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public ViazUserResponseDTO changePassword(String viazUserEmail, String newPassword) {
        ViazUser user = userRepository.findByEmail(viazUserEmail).orElseThrow(() -> new ResourceNotFoundException("No user with email " + viazUserEmail));

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
        return password.length() >= 8;
    }

    @Transactional
    public ViazUser createUnValidatedUser(ViazUser user, Set<String> roles) {
        // Set default account status
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        // Assign default role if user data is missing
        createDefaultUserIfNotPresent(user);

        ViazUser createdUser = userRepository.save(user);

        boolean allCosmetic = roles == null || roles.isEmpty() || roles.stream()
                .map(roleService::findByName)
                .allMatch(role -> role.isPresent() && role.get().isCosmetic());

        if (allCosmetic) {
            // Assign default non-cosmetic "User" role
            Optional<Role> userRole = roleService.findByName("User");
            if (userRole.isEmpty()) {
                throw new IllegalStateException("Default 'User' role not found in the system.");
            }
            assignRoleToUser(user.getUserId(), userRole.get().getName());
        } else {
            // Assign all provided roles
            for (String roleName : roles) {
                assignRoleToUser(user.getUserId(), roleName);
            }
        }

        return createdUser;
    }


    @Transactional
    public void deleteUsersWithNullPassword() {
        List<ViazUser> usersWithNullPassword = userRepository.findByPasswordIsNull();
        userRepository.deleteAll(usersWithNullPassword);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        ViazUser user = userRepository.findById(userId).get();

        // Check if user has ADMIN role
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> "ADMIN".equalsIgnoreCase(role.getName()));

        if (isAdmin) {
            throw new IllegalStateException("Cannot delete user with ADMIN role");
        }


        //        Get User
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ViazUserDetails viazUserDetails = (ViazUserDetails) auth.getPrincipal();
        ViazUser currentUser = userRepository.findByUsername(viazUserDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        user.softDelete(currentUser);
        userRepository.save(user);
    }

    public String getUsernameByEmailIgnoreCase(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new ResourceNotFoundException("No user with email " + email)).getUsername();
    }

    public boolean findByEmail(String email){
        ViazUser user = userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new ResourceNotFoundException("No user with email "));
        return user != null;
    }

    @Transactional
    public void recordSuccessfulLogin(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.recordSuccessfulLogin();
            userRepository.save(user);
        });
    }

    @Transactional
    public void incrementFailedLoginAttempts(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.incrementFailedLoginAttempts();
            userRepository.save(user);
        });
    }

    @Transactional
    public void unlockAccount(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setAccountNonLocked(true);
            user.resetFailedLoginAttempts();
            userRepository.save(user);
        });
    }

    @Transactional

    public Map<String, Object> assignRoleToUser(Long userId, String roleName) {
        ViazUser user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Role role = roleService.findByName(roleName).orElseThrow(() -> new NoSuchElementException("Role not found with name: " + roleName));

        if (user.getRoles().contains(role)) {
            throw new IllegalStateException("User already has the role: " + roleName);
        }

        user.getRoles().add(role);
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Role " + roleName + " assigned to user " + user.getUsername());
        return response;

    }

    @Transactional
    public Map<String, Object> removeRoleFromUser(Long userId, String roleName) {
        ViazUser user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));
        Role role = roleService.findByName(roleName).orElseThrow(() -> new NoSuchElementException("Role not found with name: " + roleName));

        // Remove the role if it exists in the user's roles
        boolean removed = user.getRoles().remove(role);
        if (!removed) {
            throw new IllegalStateException("User " + userId + " does not have role " + roleName);
        }

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Role " + roleName + " assigned to user " + user.getUsername());
        return response;
    }


    @Transactional
    public List<UserSummaryDTO> getUsersByRole(String roleName) {
        Optional<Role> roleOpt = roleService.findByName(roleName);
        if (roleOpt.isPresent()) {
            return roleOpt.get().getUsers().stream().map(user -> new UserSummaryDTO(user.getUserId(), user.getUsername())).collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Role not found: " + roleName);
        }
    }


    public void checkAndExpireStalePasswords() {
        Instant expiryDate = Instant.now().minus(PASSWORD_EXPIRY_DAYS, ChronoUnit.DAYS);
        List<ViazUser> usersWithStalePasswords = userRepository.findUsersWithPasswordOlderThan(expiryDate);

        usersWithStalePasswords.forEach(user -> {
            user.setCredentialsNonExpired(false);
            try {
                emailService.sendPasswordExpiredEmail(user.getEmail());
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            userRepository.save(user);
        });
    }

    public void disableInactiveAccounts() {
        Instant inactiveDate = Instant.now().minus(ACCOUNT_INACTIVE_DAYS, ChronoUnit.DAYS);
        userRepository.disableInactiveUsersSince(inactiveDate);
    }


    public ViazUser getUserByName(String username) {
        ViazUser user = userRepository.getByUsername(username);
        if (user == null) throw new NoSuchElementException("User not found with username: " + username);

        return user;
    }

    public ViazUser getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));
    }

    public List<ViazUser> getAllUsersByRole(String roleName) {
        Optional<Role> role = roleService.findRoleByName(roleName);
        if (role.isEmpty()) {
            throw new IllegalArgumentException("Role not found: " + roleName);
        }
        return userRepository.findAllByRolesContains(role.get());
    }
}
