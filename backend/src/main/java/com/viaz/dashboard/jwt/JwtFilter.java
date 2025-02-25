package com.viaz.dashboard.jwt;

import com.viaz.dashboard.jwt.blacklist.BlacklistService;
import com.viaz.dashboard.user.userDetails.ViazUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    public JwtFilter(JwtUtil JwtUtil, ApplicationContext applicationContext, BlacklistService blacklistService) {
        this.JwtUtil = JwtUtil;
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
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
            throw new ExpiredJwtException(null, null, "Expired JWT token");
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
