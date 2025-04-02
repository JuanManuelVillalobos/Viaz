package com.viaz.dashboard.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {

    private static final String REFRESH_TOKEN_URI = "/api/auth/refresh-token"; // Define your endpoint URI here
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken"; // Define your cookie name here

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // Check if the request is for the refresh token endpoint
        if (REFRESH_TOKEN_URI.equals(request.getRequestURI())) {
            // Check for the presence of the refresh token cookie
            boolean hasRefreshTokenCookie = false;
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                        hasRefreshTokenCookie = true;
                    }
                }
            }

            // If the cookie is not present, deny the request and return 401 with a JSON response
            if (!hasRefreshTokenCookie) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Refresh token is missing.\"}");
                return; // Stop further processing
            }
        }

        // If the request is not for the refresh token endpoint, proceed with the default behavior
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\"}");
    }
}
