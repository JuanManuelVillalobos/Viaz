package com.viaz.dashboard.auth;

import com.viaz.dashboard.jwt.Token;
import com.viaz.dashboard.user.ViazUser;
import com.viaz.dashboard.user.ViazUserDTO;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/ping")
    public String ping(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        return "pong";
    }
    // Get all users using Data Transfer Object
    @GetMapping("/get-all-users")
    public List<ViazUserDTO> getAllUsers() {
        return authService.getAllUsers();
    }

    // Register User
    @PostMapping("/register")
    public ViazUser registerUser(@RequestBody ViazUserDTO viazUser) throws MessagingException {
        return authService.register(viazUser);
    }

    //    Login User
    @PostMapping("/login")
    public Token verify(@RequestBody ViazUser viazUser, HttpServletResponse response) {
        return authService.login(viazUser,response);
    }

    @PostMapping("/logout")
    public void logout(@CookieValue(value = "refreshToken") String refreshToken) {
        authService.logout(refreshToken);
    }

    @PostMapping("/refresh-token")
    public Token refresh(@CookieValue(value = "refreshToken") String refreshToken) {
        return authService.refreshAccessToken(refreshToken);
    }
}