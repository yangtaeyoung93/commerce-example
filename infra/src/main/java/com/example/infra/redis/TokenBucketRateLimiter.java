package com.example.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class TokenBucketRateLimiter {

    private final RedisTemplate<String,Object> redisTemplate;

    /**
     * Lua 스크립트로 토큰 버킷 구현
     */
    public boolean allowRequest(
            String memberId,
            int maxTokens,
            double refillRate  // 초당 재충전 토큰 수
    ) {
        String key = "ratelimit:tokenbucket:" + memberId;
        long now = System.currentTimeMillis() / 1000;  // 초 단위

        String script =
                //변수 설정
                "local key = KEYS[1] " +
                "local max_tokens = tonumber(ARGV[1]) " +
                "local refill_rate = tonumber(ARGV[2]) " +
                "local now = tonumber(ARGV[3]) " +

                //현재 상태 가져오기
                "local bucket = redis.call('HMGET', key, 'tokens', 'last_refill') " +
                "local tokens = tonumber(bucket[1]) or max_tokens " +
                "local last_refill = tonumber(bucket[2]) or now " +

                //토큰 충전 계산
                "local elapsed = now - last_refill " +
                "local new_tokens = math.min(max_tokens, tokens + elapsed * refill_rate) " +

                //요청 허용 여부 결정
                "if new_tokens >= 1 then " +
                "  new_tokens = new_tokens - 1 " +
                "  redis.call('HMSET', key, 'tokens', new_tokens, 'last_refill', now) " +
                "  redis.call('EXPIRE', key, 3600) " +
                "  return 1 " +
                "else " +
                "  redis.call('HMSET', key, 'tokens', new_tokens, 'last_refill', now) " +
                "  return 0 " +
                "end";

        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(key),
                maxTokens, refillRate, now
        );

        return result != null && result == 1;
    }
}
