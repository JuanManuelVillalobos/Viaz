package com.viaz.dashboard.initializers;

import com.viaz.dashboard.entity.role.Role;
import com.viaz.dashboard.entity.role.RoleRepository;
import com.viaz.dashboard.entity.role.RoleService;
import com.viaz.dashboard.entity.user.*;
import com.viaz.dashboard.exception.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

@Order(2)
@Component
public class DefaultUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final String defaultAdminUsername;
    private final String defaultAdminPassword;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    public DefaultUserInitializer(
            @Value("${default.admin.user}") String defaultAdminUser,
            @Value("${default.admin.password}") String defaultAdminPassword,
            UserRepository userRepository,
            UserService userService, RoleRepository roleRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.defaultAdminUsername = defaultAdminUser;
        this.defaultAdminPassword = defaultAdminPassword;
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
    }

    @Override
    public void run(String... args) {
        String defaultEmail = "admin@example.com";

        if (userRepository.findByUsername(defaultAdminUsername).isPresent()) {
            System.out.println("Default admin user already exists: " + defaultAdminUsername);
            return;
        }


        // If role doesn't exist, create it and assign the returned Role object
        // If role exists, get the Role object from the Optional
        Role adminRole = roleService.findByName("ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("ADMIN role not found"));


        // Create a new admin user if it does not exist
        ViazUser defaultViazUser = new ViazUser(defaultAdminUsername, defaultAdminPassword, defaultEmail);
        defaultViazUser.setRoles(Set.of(adminRole)); // Use the extracted Role instance
        defaultViazUser.setEnabled(true);
        defaultViazUser.recordSuccessfulLogin();


        userService.addUserToRepository(defaultViazUser);
        System.out.println("Default admin user created: " + defaultAdminUsername);
    }
}