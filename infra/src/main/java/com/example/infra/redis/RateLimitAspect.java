package com.example.infra.redis;

import com.example.global.exception.RateLimitExceededException;
import com.example.global.response.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {
    private TokenBucketRateLimiter rateLimiter;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable{

        // 인증 정보에서 가져옴
        String memberId = "member123";
        String key = rateLimit.keyPrefix() + ":" + memberId;

        boolean allowed = rateLimiter.allowRequest(
                key,
                rateLimit.windowSeconds(),
                rateLimit.maxRequests()
        );

        if (!allowed) {
            throw new RateLimitExceededException(ErrorCode.INVALID_INPUT, "Too many request");
        }

        return joinPoint.proceed();
    }
}
