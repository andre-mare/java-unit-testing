package com.example.module_05.section_04;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.ratelimit.TokenBucket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Module 5.4 lab – poll until a condition holds, with {@code @Timeout} as the upper bound.
 * <p>
 * For asynchronous behaviour that has no latch or callback to wait on, "poll
 * until" is the simplest reliable test: check, sleep a little, check again. The
 * timeout guarantees the test can never hang the build if the condition never
 * becomes true. For richer polling (intervals, ignored exceptions) use a library
 * such as Awaitility.
 */
class PollingTimeoutTest {

    @Test
    @Timeout(5) // poll at most 5 seconds
    void bucketRefillsCompletely() throws InterruptedException {
        TokenBucket bucket = new TokenBucket(3, 20);
        while (bucket.tryAcquire()) {
            // drain
        }

        while (bucket.availableTokens() < 3) {
            Thread.sleep(25); // poll interval
        }

        assertEquals(3, bucket.availableTokens());
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void backgroundWorkerFinishes() throws InterruptedException {
        TokenBucket bucket = new TokenBucket(1, 10);
        AtomicInteger served = new AtomicInteger();

        // A background "worker" serves 5 requests, throttled by the bucket.
        CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    bucket.acquire();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                served.incrementAndGet();
            }
        });

        while (served.get() < 5) {
            Thread.sleep(20);
        }
        assertTrue(served.get() == 5);
    }
}
