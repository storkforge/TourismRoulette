package com.example.tourismroullete.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ChatCacheService {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public ChatCacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public void cacheResponse(Long userId, String message, String response) {
        String key = generateCacheKey(userId, message);
        redisTemplate.opsForValue().set(key, response, Duration.ofMinutes(30)); // 30 minute TTL
    }


    public String getCachedResponse(Long userId, String message) {
        String key = generateCacheKey(userId, message);
        return redisTemplate.opsForValue().get(key);
    }


    private String generateCacheKey(Long userId, String message) {
        return "chat:response:" + userId + ":" + message.hashCode();
    }
}
