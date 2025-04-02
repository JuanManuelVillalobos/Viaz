package com.viaz.dashboard.filters;

import com.viaz.dashboard.jwt.JwtUtil;
import com.viaz.dashboard.user.userDetails.ViazUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final com.viaz.dashboard.jwt.JwtUtil JwtUtil;
    private final ApplicationContext applicationContext;

    public JwtFilter(JwtUtil JwtUtil, ApplicationContext applicationContext) {
        this.JwtUtil = JwtUtil;
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {


        String authHeader = request.getHeader("Authorization");
        String username;
        String jwtToken;


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtToken = authHeader.substring(7);
            username = JwtUtil.extractUsername(jwtToken);
        } catch (ExpiredJwtException e) {
            // Handle expired token
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json"); // Set content type if returning JSON
            response.getWriter().write("{\"error\": \"JWT token is expired\"}");
            return; // Return immediately to prevent further processing
        } catch (Exception e) {
            // Handle other exceptions (e.g., invalid token)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json"); // Set content type if returning JSON
            response.getWriter().write("{\"error\": \"Invalid JWT token\"}");
            return; // Return immediately to prevent further processing
        }


        if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = applicationContext
                .getBean(ViazUserDetailsService.class)
                .loadUserByUsername(username);

        if (!JwtUtil.validateToken(jwtToken, userDetails)) {
            // Token is invalid or expired
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token");
            return;
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);


        filterChain.doFilter(request, response);
    }
}
