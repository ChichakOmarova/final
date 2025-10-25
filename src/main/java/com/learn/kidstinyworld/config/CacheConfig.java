package com.learn.kidstinyworld.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Configuration
@EnableCaching // Bu annotasiya @Cacheable-i aktiv edir
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        // Cache-in umumiyyetle nece isleyeceyini konfiqurasiya edir
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                // Cache-de saxlanilan obyektler ne qeder muddet aktiv qalsin
                .entryTtl(Duration.ofMinutes(5))

                // Null deyerleri cache-e yazmasin
                .disableCachingNullValues();

        // Redis əlaqəsini idarə edən CacheManager-i yaradir
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();
    }
}