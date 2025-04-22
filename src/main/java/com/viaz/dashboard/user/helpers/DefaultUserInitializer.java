package com.viaz.dashboard.user.helpers;

import com.viaz.dashboard.user.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DefaultUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final String defaultAdminUsername;
    private final String defaultAdminPassword;
    private final UserService userService;
    private final RoleRepository roleRepository;

    public DefaultUserInitializer(
            @Value("${default.admin.user}") String defaultAdminUser,
            @Value("${default.admin.password}") String defaultAdminPassword,
            UserRepository userRepository,
            UserService userService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.defaultAdminUsername = defaultAdminUser;
        this.defaultAdminPassword = defaultAdminPassword;
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        String defaultEmail = "admin@example.com";

        if (userRepository.findByUsername(defaultAdminUsername).isEmpty()) {
            // Create admin role if it doesn't exist
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ADMIN")));

            // Create a new admin user if it does not exist
            ViazUser defaultViazUser = new ViazUser(defaultAdminUsername, defaultAdminPassword, defaultEmail);
            defaultViazUser.setRoles(Set.of(adminRole)); // Use the saved role instance


            userService.addUserToRepository(defaultViazUser);
            System.out.println("Default admin user created: " + defaultAdminUsername);
        } else {
            System.out.println("Default admin user already exists: " + defaultAdminUsername);
        }
    }
}