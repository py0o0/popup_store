package com.example.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RedisTemplate<String, String> redisTemplate;

    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    public void setRefreshToken(String email, String refreshToken) {
        redisTemplate.opsForValue().set(email, refreshToken);
    }

    public void removeRefreshToken(String email) {
        redisTemplate.delete(email);
    }
}
