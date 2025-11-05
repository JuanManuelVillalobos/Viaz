package com.viaz.dashboard.auth;

import com.viaz.dashboard.auth.dto.LoginRequestDTO;
import com.viaz.dashboard.auth.dto.SetPasswordDTO;
import com.viaz.dashboard.auth.dto.UserAuthResponseDTO;
import com.viaz.dashboard.jwt.Token;
import com.viaz.dashboard.features.user.ViazUser;
import com.viaz.dashboard.features.user.dto.ViazUserResponseDTO;
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
    public ResponseEntity<UserAuthResponseDTO> getCurrentUserAuth(Authentication authentication){
        return authService.getCurrentUserAuth(authentication);
    }

    // Register User
    @PostMapping("/register")
    public ViazUser registerUser(@RequestBody ViazUserResponseDTO viazUser) throws MessagingException {
        return authService.register(viazUser);
    }

    @PostMapping("/set-password")
    public ViazUserResponseDTO createUser(@RequestBody SetPasswordDTO request, @RequestParam String email, @RequestParam String token) {
        return authService.changePassword(email,request,token);
    }

    @PostMapping("/forgot-password/{email}")
    public void forgotPassword(@PathVariable String email) throws MessagingException {
        authService.forgotPassword(email);
    }

    //    Login User
    @PostMapping("/login")
    public Token verify(@RequestBody LoginRequestDTO request, HttpServletResponse response) {
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