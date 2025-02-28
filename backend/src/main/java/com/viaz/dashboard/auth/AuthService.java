package com.viaz.dashboard.auth;

import com.viaz.dashboard.email.EmailService;
import com.viaz.dashboard.jwt.JwtUtil;
import com.viaz.dashboard.jwt.Token;
import com.viaz.dashboard.jwt.blacklist.BlacklistService;
import com.viaz.dashboard.user.UserService;
import com.viaz.dashboard.user.ViazUser;
import com.viaz.dashboard.user.ViazUserDTO;
import com.viaz.dashboard.user.userDetails.ViazUserDetailsService;
import com.viaz.dashboard.user.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ViazUserDetailsService viazUserDetailsService;
    private final BlacklistService blacklistService;
    private final EmailService emailService;
    private final UserService userService;

    public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil, ViazUserDetailsService viazUserDetailsService, BlacklistService blacklistService, EmailService emailService, UserService userService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.viazUserDetailsService = viazUserDetailsService;
        this.blacklistService = blacklistService;
        this.emailService = emailService;
        this.userService = userService;
    }

    public ViazUser register(ViazUserDTO viazUser) throws MessagingException {
        emailService.sendInvitationEmail(viazUser.getEmail());
        ViazUser user = new ViazUser(viazUser.getUsername(), viazUser.getEmail());
        return userRepository.save(user);
    }

    public Token login(ViazUser viazUser, HttpServletResponse response) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                viazUser.getUsername(), viazUser.getPassword()));
        if (!authentication.isAuthenticated()) {
            return null;
        }
        String username = viazUser.getUsername();
        String accessToken = jwtUtil.generateAccessToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/api/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 30); // 30 Days
        refreshTokenCookie.setSecure(true);
        response.addCookie(refreshTokenCookie);

        return new Token(accessToken, username);
    }

    public void logout(String refreshToken) {
        Date expirationDate = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30); // 30days
        blacklistService.addToBlacklist(refreshToken, expirationDate);
    }

    public Token refreshAccessToken(String refreshToken) {
        UserDetails userDetails = viazUserDetailsService.loadUserByUsername(jwtUtil.extractUsername(refreshToken));


        if (refreshToken.isEmpty()) {
            return null;}

        if (!jwtUtil.validateToken(refreshToken, userDetails)){
            return null;}

        if (blacklistService.isBlacklisted(refreshToken)) {
            throw new JwtException("Revoked token");}

        String username = jwtUtil.extractUsername(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(username);
        return new Token(newAccessToken, userDetails.getUsername());
    }

    public void deleteExpiredInvites(){
        List<ViazUser> usersWithNullPassword = userRepository.findByPasswordIsNull();
        userRepository.deleteAll(usersWithNullPassword);
    }

     public List<ViazUserDTO> getAllUsers() {
        return userService.getAllUsers();
    }


}
