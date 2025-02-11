package com.viaz.dashboard.auth;

import com.viaz.dashboard.jwt.Token;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Register User
    @PostMapping("/register")
    public ViazUser createUser(@RequestBody ViazUser viazUser) {
        return authService.registerUser(viazUser);
    }

    //    Login User
    @PostMapping("/login")
    public Token login(@RequestBody ViazUser viazUser, HttpServletResponse response) {
        return authService.verify(viazUser,response);
    }

    @PostMapping("/refresh-token")
    public Token refresh(@CookieValue(value = "refreshToken", defaultValue = "") String refreshToken) {
        return authService.refreshAccessToken(refreshToken);
    }
}