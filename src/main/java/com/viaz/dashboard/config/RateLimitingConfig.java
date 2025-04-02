package com.viaz.dashboard.config;

import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitingConfig {

    private final int rateLimit;

    public RateLimitingConfig(@Value("${rate.limit:150}") int rateLimit) {
        this.rateLimit = rateLimit;
    }

    @Bean
    public Bucket bucket() {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(rateLimit).refillGreedy(100, Duration.ofMinutes(1)))
                .build();
    }
}