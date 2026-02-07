package com.example.items;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @InjectMocks
    private CacheManager cacheManager;

    @Test
    void cacheStampedeTest() throws InterruptedException{

        Long itemId = 1L;

        //캐시 삭제(만료 상황)
        cacheManager.getCache("items").evict("item:" + itemId);

        //동시요청
        int count = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(count);
        //count가 0이 될때 까지 대기
        CountDownLatch latch = new CountDownLatch(count);
        AtomicInteger dbCallCount = new AtomicInteger(0);
        for (int i = 0; i < count; i++) {
            executorService.submit(()->{
                try {
                    itemService.getItem(itemId);
                    dbCallCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        System.out.println("DB 호출 수 : " + dbCallCount.get());

    }
}