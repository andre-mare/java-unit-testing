package com.example.module_05.section_04;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.example.ratelimit.TokenBucket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Timeout.ThreadMode;

/**
 * Module 5.4 lab – {@code @Timeout} on tests and lifecycle methods.
 * <ul>
 *   <li>{@code value} plus {@code unit} (default {@code SECONDS}).</li>
 *   <li>{@code threadMode}: {@code SAME_THREAD} interrupts the test thread when
 *       time is up; {@code SEPARATE_THREAD} runs the code in another thread, like
 *       {@code assertTimeoutPreemptively} (no access to the test thread's
 *       {@code ThreadLocal}s); {@code INFERRED} (the default) reads
 *       {@code junit.jupiter.execution.timeout.thread.mode.default} and falls
 *       back to {@code SAME_THREAD}.</li>
 * </ul>
 * The bucket takes about 150 ms to hand out five tokens, well inside 500 ms.
 */
class TimeoutAnnotationTest {

    private TokenBucket bucket;

    @BeforeEach
    @Timeout(2) // lifecycle methods can have their own timeout, in seconds by default
    void createBucket() {
        bucket = new TokenBucket(2, 20); // 2 tokens, one back every 50 ms
    }

    @Test
    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
    void acquiresFiveTokensWithinHalfASecond() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            bucket.acquire(); // 2 immediately, then 3 waits of about 50 ms
        }
        assertEquals(0, bucket.availableTokens());
    }

    @Test
    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS, threadMode = ThreadMode.SEPARATE_THREAD)
    void sameWorkInASeparateThread() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            bucket.acquire();
        }
        assertEquals(0, bucket.availableTokens());
    }

    @Test
    @Timeout(1)
    void boundedWaitGivesUpOnItsOwn() throws InterruptedException {
        TokenBucket slow = new TokenBucket(1, 1); // one token per second
        slow.acquire();
        // The production code has its own timeout here; @Timeout is only the safety net.
        assertFalse(slow.tryAcquire(Duration.ofMillis(100)));
    }
}
