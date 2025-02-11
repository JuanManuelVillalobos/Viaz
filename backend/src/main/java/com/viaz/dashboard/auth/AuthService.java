package com.viaz.dashboard.auth;

import com.viaz.dashboard.jwt.JwtUtil;
import com.viaz.dashboard.jwt.Token;
import com.viaz.dashboard.auth.userDetails.CustomUserDetailsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public ViazUser registerUser(ViazUser viazUser) {
        viazUser.setPassword(encoder.encode(viazUser.getPassword()));
        return userRepository.save(viazUser);
    }

    public Token verify(ViazUser viazUser, HttpServletResponse response) {
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
        //   TODO SET TRUE
        //    refreshTokenCookie.setSecure(true);
        response.addCookie(refreshTokenCookie);

        return new Token(accessToken, username);

    }

    public Token refreshAccessToken(String refreshToken) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(jwtUtil.extractUsername(refreshToken));


        if (refreshToken.isEmpty() || !jwtUtil.validateToken(refreshToken, userDetails)) {
            return null;
        }

        String username = jwtUtil.extractUsername(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(username);
        return new Token(newAccessToken, userDetails.getUsername());
    }

}
