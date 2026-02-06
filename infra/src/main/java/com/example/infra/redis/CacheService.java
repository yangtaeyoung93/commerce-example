package com.example.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CacheService {
    private final RedisTemplate<String,Object> redisTemplate;

    public void evictUserCondition(Long memberId) {
        String key = "orders::member" + memberId + ":*";
        Set<String> keys = redisTemplate.keys(key);

        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
