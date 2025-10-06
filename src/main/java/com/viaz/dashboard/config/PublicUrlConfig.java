package com.viaz.dashboard.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component

public class PublicUrlConfig {

    private final List<RequestMatcher> publicMatchers = List.of(
            PathPatternRequestMatcher.withDefaults().matcher("/api/shipment/upload-excel"),
            PathPatternRequestMatcher.withDefaults().matcher("/api/auth/login"),
            PathPatternRequestMatcher.withDefaults().matcher("/api/auth/forgot-password"),
            PathPatternRequestMatcher.withDefaults().matcher("/api/auth/set-password"),
            PathPatternRequestMatcher.withDefaults().matcher("/api/auth/refresh-token")
    );

    public boolean isPublic(HttpServletRequest request) {
        return publicMatchers.stream().anyMatch(matcher -> matcher.matches(request))
                || !request.getRequestURI().startsWith("/api/");
    }
}