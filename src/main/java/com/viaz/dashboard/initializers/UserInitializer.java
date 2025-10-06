package com.viaz.dashboard.initializers;

import com.viaz.dashboard.features.role.Role;
import com.viaz.dashboard.features.role.RoleService;
import com.viaz.dashboard.features.user.*;
import com.viaz.dashboard.exception.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

@Slf4j
@Order(2)
@Component
public class UserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final String defaultAdminUsername;
    private final String defaultAdminPassword;
    private final UserService userService;
    private final RoleService roleService;
    private final Environment environment;

    public UserInitializer(
            @Value("${default.admin.user}") String defaultAdminUser,
            @Value("${default.admin.password}") String defaultAdminPassword,
            UserRepository userRepository,
            UserService userService, RoleService roleService, Environment environment) {
        this.userRepository = userRepository;
        this.defaultAdminUsername = defaultAdminUser;
        this.defaultAdminPassword = defaultAdminPassword;
        this.userService = userService;
        this.roleService = roleService;
        this.environment = environment;
    }

    @Override
    public void run(String... args) {

//        Create default admin user if not present
        String defaultEmail = "admin@example.com";
        createUserIfNotPresent(
                new ViazUser(defaultAdminUsername, defaultAdminPassword, defaultEmail),
                "ADMIN");

        UserInitializer.log.info("Default admin user created: {}", defaultAdminUsername);

        if (isDevProfile()) {
            log.info("Development profile active — creating dev/test users...");

            createUserIfNotPresent(
                    new ViazUser("customerServiceUser", "password", "customerService@example.com"),
                    "Client Service"
            );

            createUserIfNotPresent(
                    new ViazUser("OtherUser", "password", "otheruser@example.com"),
                    "User"
            );
        }
    }

    private boolean isDevProfile() {
        return Arrays.asList(environment.getActiveProfiles()).contains("dev");
    }

    private void createUserIfNotPresent(ViazUser viazUser, String roleName) {
        if (userRepository.findByUsername(viazUser.getUsername()).isPresent()) {
            UserInitializer.log.info("Default admin user already exists: {}", defaultAdminUsername);
            return;
        }

        // If role doesn't exist, raise exception as PermissionInitializer should run before UserInitializer
        // If role exists, get the Role object from the Optional
        Role roleToAssign = roleService.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Role %s not found", roleName)));

        viazUser.setRoles(Set.of(roleToAssign));
        viazUser.setEnabled(true);
        viazUser.recordSuccessfulLogin();

        userService.addUserToRepository(viazUser);
    }
}