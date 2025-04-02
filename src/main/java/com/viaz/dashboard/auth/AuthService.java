package com.viaz.dashboard.auth;

import com.viaz.dashboard.email.EmailService;
import com.viaz.dashboard.jwt.JwtUtil;
import com.viaz.dashboard.jwt.Token;
import com.viaz.dashboard.jwt.blacklist.BlacklistService;
import com.viaz.dashboard.user.UserService;
import com.viaz.dashboard.user.ViazUser;
import com.viaz.dashboard.user.helpers.ViazUserDTO;
import com.viaz.dashboard.user.userDetails.ViazUserDetailsService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ViazUserDetailsService viazUserDetailsService;
    private final BlacklistService blacklistService;
    private final EmailService emailService;
    private final UserService userService;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, ViazUserDetailsService viazUserDetailsService, BlacklistService blacklistService, EmailService emailService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.viazUserDetailsService = viazUserDetailsService;
        this.blacklistService = blacklistService;
        this.emailService = emailService;
        this.userService = userService;
    }

    public ViazUser register(ViazUserDTO viazUser) throws MessagingException {
        emailService.sendInvitationEmail(viazUser.email());
        ViazUser user = new ViazUser(viazUser.username(), viazUser.email());
        return userService.createUnValidatedUser(user);
    }

    public Token login(ViazUser viazUser, HttpServletResponse response) {
        String identifier = userService.getUsernameByEmail(viazUser.getEmail());
        String password = viazUser.getPassword();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, password)
        );

        if (!authentication.isAuthenticated()) {
            return null;
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String accessToken = jwtUtil.generateAccessToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/api/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 30); // 30 Days
//        TODO Have https certificate
//        refreshTokenCookie.setSecure(true);
        response.addCookie(refreshTokenCookie);

        return new Token(accessToken, username);
    }

    public void logout(String refreshToken) {
        Date expirationDate = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30); // 30days
        blacklistService.addToBlacklist(refreshToken, expirationDate);
    }

    public Token refreshAccessToken(String refreshToken, HttpServletResponse response) throws IOException {
        UserDetails userDetails = viazUserDetailsService.loadUserByUsername(jwtUtil.extractUsername(refreshToken));
        if (refreshToken.isEmpty()) {
            return null;
        }
        if (!jwtUtil.validateToken(refreshToken, userDetails)) {
            return null;
        }
        if (blacklistService.isBlacklisted(refreshToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Revoked Token\"}");
            return null;
        }
        String username = jwtUtil.extractUsername(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(username);
        return new Token(newAccessToken, userDetails.getUsername());
    }

    public ViazUserDTO changePassword(String email, String newPassword, String token) throws Exception {
        // Check if the token is valid for the given email
        String storedToken = emailService.getToken(email);
        if (storedToken != null && storedToken.equals(token)) {
            // Change the password and save the DTO
            ViazUserDTO changedPasswordUser = userService.changePassword(email, newPassword);

            // After successfully changing the password, remove the token
            emailService.removeToken(email);
            return changedPasswordUser;
        }
        return null; // Token is invalid or does not match
    }

    public void deleteExpiredInvites() {
        userService.deleteUsersWithNullPassword();
    }

    public void forgotPassword(String email) throws MessagingException {
        emailService.sendPasswordResetEmail(email);
    }

    // Helper method to check if the identifier is an email
    private boolean isEmail(String identifier) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return identifier.matches(emailRegex);
    }
}
