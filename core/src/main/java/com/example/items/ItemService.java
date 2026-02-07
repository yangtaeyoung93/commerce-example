package com.example.items;

import com.example.infra.redis.DistributedLock;
import com.example.infra.redis.RateLimit;
import com.example.item.ItemResponse;
import com.example.repository.item.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Cacheable(
            value = "items",
            key = "'item:' + #id",
            unless = "#result == null",  // null은 캐싱 안 함
            sync = true // 락 설정
    )
    @RateLimit(maxRequests = 100, windowSeconds = 60)
    @DistributedLock(
            key = "'item:' + #id",
            waitTime = 3,
            leaseTime = 30
    )
    public ItemResponse getItem(Long id) {
        log.info("DB 조회: Item = {}", id);  // 로그 추가

        // DB 조회 시뮬레이션 (500ms 소요)
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return itemRepository.findById(id)
                .map(ItemResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("상품 없음"));
    }
}
