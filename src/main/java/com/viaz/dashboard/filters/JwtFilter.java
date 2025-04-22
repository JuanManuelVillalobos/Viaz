package com.viaz.dashboard.filters;

import com.viaz.dashboard.exception.exceptions.TokenValidationException;
import com.viaz.dashboard.jwt.JwtUtil;
import com.viaz.dashboard.jwt.blacklist.BlacklistService;
import com.viaz.dashboard.user.userDetails.ViazUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ApplicationContext applicationContext;
    private final BlacklistService blacklistService;

    public JwtFilter(JwtUtil jwtUtil, ApplicationContext applicationContext, BlacklistService blacklistService) {
        this.jwtUtil = jwtUtil;
        this.applicationContext = applicationContext;
        this.blacklistService = blacklistService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            Optional<String> tokenOpt = extractToken(request);
            if (tokenOpt.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = tokenOpt.get();
            
            // Check if token is blacklisted
            if (blacklistService.isBlacklisted(token)) {
                handleError(response, "Token has been revoked", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            processToken(token, request);
            
            // Add security headers
            response.setHeader("X-Content-Type-Options", "nosniff");
            response.setHeader("X-Frame-Options", "DENY");
            response.setHeader("X-XSS-Protection", "1; mode=block");
            
            filterChain.doFilter(request, response);
            
        } catch (ExpiredJwtException e) {
            log.warn("JWT token has expired", e);
            handleError(response, "JWT token has expired", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (JwtException e) {
            log.warn("Invalid JWT token", e);
            handleError(response, "Invalid JWT token", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (TokenValidationException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            handleError(response, e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Authentication error", e);
            handleError(response, "Authentication failed", HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return Optional.of(authHeader.substring(7));
        }
        return Optional.empty();
    }

    private void processToken(String token, HttpServletRequest request) {
        String username = jwtUtil.extractUsername(token);
        if (username == null) {
            throw new TokenValidationException("Username not found in token");
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = applicationContext
                    .getBean(ViazUserDetailsService.class)
                    .loadUserByUsername(username);

            if (!jwtUtil.validateToken(token, userDetails)) {
                throw new TokenValidationException("Invalid JWT token");
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authentication set for user: {}", username);
        }
    }

    private void handleError(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(String.format("{\"error\": \"%s\", \"timestamp\": \"%s\"}", 
                message, java.time.LocalDateTime.now()));
    }
}
