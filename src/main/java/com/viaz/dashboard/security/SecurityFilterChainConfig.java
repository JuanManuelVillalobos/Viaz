package com.viaz.dashboard.security;

import com.viaz.dashboard.config.PublicUrlConfig;
import com.viaz.dashboard.exception.AuthEntryPoint;
import com.viaz.dashboard.filters.JwtFilter;
import com.viaz.dashboard.filters.RateLimitingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    private final JwtFilter jwtFilter;
    private final AuthEntryPoint authEntryPoint;
    private final RateLimitingFilter rateLimitingFilter;


    @Bean
    public org.springframework.security.web.SecurityFilterChain securityFilterChain(final HttpSecurity http, PublicUrlConfig publicUrlConfig)
            throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(authEntryPoint))
                .authorizeHttpRequests(authorizeRequests -> {
                    publicUrlConfig.getPublicMatchers().forEach(matcher -> {
                        authorizeRequests.requestMatchers(matcher).permitAll();
                    });
                    authorizeRequests
                            .requestMatchers("/api/**").authenticated()
                            .requestMatchers("/**").permitAll();
                })
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
