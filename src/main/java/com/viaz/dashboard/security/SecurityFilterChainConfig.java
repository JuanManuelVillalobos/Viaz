package com.viaz.dashboard.security;

import com.viaz.dashboard.exception.AuthEntryPoint;
import com.viaz.dashboard.filters.JwtFilter;
import com.viaz.dashboard.filters.RateLimitingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    private final JwtFilter jwtFilter;
    private final AuthEntryPoint authEntryPoint;
    private final RateLimitingFilter rateLimitingFilter;

    public SecurityFilterChainConfig(JwtFilter jwtFilter, AuthEntryPoint authEntryPoint, RateLimitingFilter rateLimitingFilter) {
        this.authEntryPoint = authEntryPoint;
        this.rateLimitingFilter = rateLimitingFilter;
        this.jwtFilter = jwtFilter;
    }


    @Bean
    public org.springframework.security.web.SecurityFilterChain securityFilterChain(final HttpSecurity http)
            throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(authEntryPoint))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/shipment/upload-excel",
                                        "/api/auth/login",
                                        "/api/auth/forgot-password",
                                        "/api/auth/set-password",
                                        "/api/auth/refresh-token").permitAll()
                                .requestMatchers("/api/**").authenticated()
                                .requestMatchers("/**").permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
