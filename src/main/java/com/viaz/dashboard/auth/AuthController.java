package com.viaz.dashboard.auth;

import com.viaz.dashboard.auth.helper.LoginRequest;
import com.viaz.dashboard.auth.helper.SetPassword;
import com.viaz.dashboard.auth.helper.UserAuthResponse;
import com.viaz.dashboard.jwt.Token;
import com.viaz.dashboard.entity.user.ViazUser;
import com.viaz.dashboard.entity.user.helpers.ViazUserResponseDTO;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserAuthResponse> getCurrentUserAuth(Authentication authentication){
        return authService.getCurrentUserAuth(authentication);
    }

    // Register User
    @PostMapping("/register")
    public ViazUser registerUser(@RequestBody ViazUserResponseDTO viazUser) throws MessagingException {
        return authService.register(viazUser);
    }

    @PostMapping("/set-password")
    public ViazUserResponseDTO createUser(@RequestBody SetPassword request, @RequestParam String email, @RequestParam String token) throws Exception {
        return authService.changePassword(email,request,token);
    }

    @PostMapping("/forgot-password")
    public void forgotPassword(@RequestBody String email) throws MessagingException {
        authService.forgotPassword(email);
    }

    //    Login User
    @PostMapping("/login")
    public Token verify(@RequestBody LoginRequest request, HttpServletResponse response) {
        return authService.login(request,response);
    }

    @PostMapping("/logout")
    public void logout(@CookieValue(value = "refreshToken") String refreshToken) {
        authService.logout(refreshToken);
    }

    @PostMapping("/refresh-token")
    public Token refresh(@CookieValue(value = "refreshToken") String refreshToken, HttpServletResponse response) throws IOException {
        return authService.refreshAccessToken(refreshToken, response);
    }
}