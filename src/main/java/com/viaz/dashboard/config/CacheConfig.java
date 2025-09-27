package com.viaz.dashboard.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        List<CaffeineCache> caches = List.of(
                new CaffeineCache("shipmentsPageCache", Caffeine.newBuilder()
                        .maximumSize(300)
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .recordStats()
                        .build()),

                new CaffeineCache("openShipmentsPageCache", Caffeine.newBuilder()
                        .maximumSize(300)
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .recordStats()
                        .build()),

                new CaffeineCache("shipment", Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(15, TimeUnit.MINUTES)
                        .recordStats()
                        .build()),

                new CaffeineCache("tokenBlacklist", Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(15, TimeUnit.MINUTES)
                        .recordStats()
                        .build()),

                new CaffeineCache("monthlyRatings", Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(15, TimeUnit.MINUTES)
                        .recordStats()
                        .build()),

                new CaffeineCache("monthlyRatingsForCarrier", Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(15, TimeUnit.MINUTES)
                        .recordStats()
                        .build()),


                new CaffeineCache("s3Objects", Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(15, TimeUnit.MINUTES)
                        .recordStats()
                        .build()),

                new CaffeineCache("s3DownloadUrls", Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(10, TimeUnit.MINUTES)
                        .recordStats()
                        .build()),

                new CaffeineCache("default", Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(10, TimeUnit.MINUTES)
                        .recordStats()
                        .build())
        );

        cacheManager.setCaches(caches);
        return cacheManager;
    }

}
