package com.viaz.dashboard.user;

import com.viaz.dashboard.auth.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserInitializer implements CommandLineRunner {


    private final UserRepository userRepository;
    private final String defaultAdminUsername;
    private final String defaultAdminPassword;
    private final UserService userService;

    public DefaultUserInitializer(@Value("${default.admin.user}") String defaultAdminUser, @Value("${default.admin.password}") String defaultAdminPassword , UserRepository userRepository, AuthService authService, UserService userService) {
        this.userRepository = userRepository;
        this.defaultAdminUsername = defaultAdminUser;
        this.defaultAdminPassword = defaultAdminPassword;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        String defaultEmail = "admin@example.com";

        if (userRepository.findByUsername(defaultAdminUsername) == null) {
            // Create a new user if it does not exist
            ViazUser defaultViazUser = new ViazUser(defaultAdminUsername, defaultAdminPassword, defaultEmail);

            userService.addUserToRepository(defaultViazUser);
            System.out.println("Default user created: " + defaultAdminUsername);
        } else {
            System.out.println("Default user already exists: " + defaultAdminUsername);
        }
    }
}