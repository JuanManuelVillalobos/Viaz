package com.viaz.dashboard.exception;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Slf4j
@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {

    private static final String REFRESH_TOKEN_URI = "/api/auth/refresh-token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final HandlerExceptionResolver handlerExceptionResolver;

    public AuthEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) {

        try {
            if (REFRESH_TOKEN_URI.equals(request.getRequestURI())) {
                boolean hasRefreshTokenCookie = false;
                if (request.getCookies() != null) {
                    for (Cookie cookie : request.getCookies()) {
                        if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                            hasRefreshTokenCookie = true;
                        }
                    }
                }

                if (!hasRefreshTokenCookie) {
                    log.warn("No refresh token cookie found in request");
                    throw new BadCredentialsException("Could not find refresh token");
                }
            }

            // If not a refresh-token request, throw the original exception to be handled
            log.warn("Exception called for {} {}",request.getRequestURI(), authException.getMessage());
            throw authException;

        } catch (Exception ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
