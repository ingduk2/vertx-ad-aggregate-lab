package com.ad.aggregate.lab.deadlock;

import com.ad.aggregate.lab.aggregate.AggregateService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootTest
class DeadlockSimulatorTest {

    @Autowired
    private AggregateService aggregateService;

    @Test
    void deadlockSimulate() throws InterruptedException {
        int threadCount = 100;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    aggregateService.upsert(10881244L, 4799L, "20260521", "10");
                    log.info("[{}] upsert 성공", Thread.currentThread().getName());
                } catch (Exception e) {
                    log.error("[{}] upsert 실패: {}", Thread.currentThread().getName(), e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        log.info("=== DeadLock 시뮬레이션 완료 ===");
    }
}
