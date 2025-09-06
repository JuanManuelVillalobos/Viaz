package com.viaz.dashboard.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
public class PublicUrlConfig {

    private final List<RequestMatcher> publicMatchers = List.of(
        new AntPathRequestMatcher("/api/shipment/upload-excel"),
        new AntPathRequestMatcher("/api/auth/login"),
        new AntPathRequestMatcher("/api/auth/forgot-password"),
        new AntPathRequestMatcher("/api/auth/set-password"),
        new AntPathRequestMatcher("/api/auth/refresh-token")
    );

    public boolean isPublic(HttpServletRequest request) {
        return publicMatchers.stream().anyMatch(matcher -> matcher.matches(request))
            || !request.getRequestURI().startsWith("/api/");
    }
}
