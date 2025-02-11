package com.viaz.dashboard.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RefreshTokenEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // Check if the request is for the refresh token endpoint
        if ("/api/auth/refresh-token".equals(request.getRequestURI())) {
            // Check for the presence of the refresh token cookie
            boolean hasRefreshTokenCookie = false;
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("refreshToken".equals(cookie.getName())) { // Replace with your cookie name
                        hasRefreshTokenCookie = true;
                        break;
                    }
                }
            }

            // If the cookie is not present, deny the request and return 401
            if (!hasRefreshTokenCookie) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return; // Stop further processing
            }
        }

        // If the request is not for the refresh token endpoint, proceed with the default behavior
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}