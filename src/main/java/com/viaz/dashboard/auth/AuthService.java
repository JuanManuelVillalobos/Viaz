package com.viaz.dashboard.auth;

import com.viaz.dashboard.auth.helper.UserAuthResponse;
import com.viaz.dashboard.email.EmailService;
import com.viaz.dashboard.entity.permission.Permission;
import com.viaz.dashboard.entity.user.userDetails.ViazUserDetails;
import com.viaz.dashboard.exception.exceptions.ResourceNotFoundException;
import com.viaz.dashboard.jwt.JwtUtil;
import com.viaz.dashboard.jwt.Token;
import com.viaz.dashboard.jwt.blacklist.BlacklistService;
import com.viaz.dashboard.entity.user.UserService;
import com.viaz.dashboard.entity.user.ViazUser;
import com.viaz.dashboard.entity.user.helpers.ViazUserDTO;
import com.viaz.dashboard.entity.user.userDetails.ViazUserDetailsService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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


    public ResponseEntity<UserAuthResponse> getCurrentUserAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ViazUserDetails userDetails = (ViazUserDetails) authentication.getPrincipal();

        UserAuthResponse response = UserAuthResponse.builder()
            .user(mapToUserInfo(userDetails))
            .effectivePermissions(getEffectivePermissions(userDetails))
            .build();

        return ResponseEntity.ok(response);
    }

    // Helper methods
    private UserAuthResponse.UserInfo mapToUserInfo(ViazUserDetails userDetails) {
        var user = userDetails.viazUser();

        List<UserAuthResponse.RoleInfo> roleInfos = user.getRoles().stream()
            .map(role -> UserAuthResponse.RoleInfo.builder()
                .name(role.getName())
                .displayName(role.getDisplayName())
                .description(role.getDescription())
                .permissions(role.getPermissions().stream()
                    .map(Permission::getName)
                    .collect(Collectors.toList()))
                .build())
            .collect(Collectors.toList());

        return UserAuthResponse.UserInfo.builder()
            .userId(user.getUserId().toString())
            .email(user.getEmail())
            .username(user.getUsername())
            .roles(roleInfos)
            .build();
    }

    private List<String> getEffectivePermissions(ViazUserDetails userDetails) {
        return userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(auth -> !auth.startsWith("ROLE_")) // Filter out role authorities, keep permissions
            .collect(Collectors.toList());
    }

    public ViazUser register(ViazUserDTO viazUser) throws MessagingException {
        ViazUser user = new ViazUser(viazUser.username(), viazUser.email());
        user = userService.createUnValidatedUser(user);
        emailService.sendInvitationEmail(viazUser.email());
        return user;
    }

    public Token login(ViazUser viazUser, HttpServletResponse response) {
        String identifier = userService.getUsernameByEmail(viazUser.getEmail());
        String password = viazUser.getPassword();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(identifier, password)
            );
    
            if (!authentication.isAuthenticated()) {
                userService.incrementFailedLoginAttempts(identifier);
                return null;
            }
    
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            
            // Record successful login to reset failed attempts and update last login date
            userService.recordSuccessfulLogin(username);
            
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
        } catch (BadCredentialsException e) {
            // Record failed login attempt
            userService.incrementFailedLoginAttempts(identifier);
            throw e;
        }
    }

    public void logout(String refreshToken) {
            LocalDateTime expirationDate = LocalDateTime.now().plusDays(30); // 30 days
        blacklistService.addToBlacklist(refreshToken, expirationDate);
    }

    public Token refreshAccessToken(String refreshToken, HttpServletResponse response) throws IOException {
        UserDetails userDetails = viazUserDetailsService.loadUserByUsername(jwtUtil.extractUsername(refreshToken));
        if (refreshToken.isEmpty()) {
            throw new  ResourceNotFoundException("Refresh token is empty");
        }
        if (!jwtUtil.validateToken(refreshToken, userDetails)) {
            throw new  BadCredentialsException("Invalid refresh token");
        }
        if (blacklistService.isBlacklisted(refreshToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Revoked Token\"}");
            throw new BadCredentialsException("Revoked Token");
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
        throw new BadCredentialsException("Token is invalid"); // Token is invalid or does not match
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredInvites() {
        userService.deleteUsersWithNullPassword();
    }

    public void forgotPassword(String email) throws MessagingException {
        emailService.sendPasswordResetEmail(email);
    }
}
