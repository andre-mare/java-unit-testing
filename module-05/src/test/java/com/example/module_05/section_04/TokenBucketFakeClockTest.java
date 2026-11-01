package com.example.module_05.section_04;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import com.example.module_05.support.FakeNanoClock;
import com.example.ratelimit.TokenBucket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Module 5.4 – start here: the best timeout is the one you do not need.
 * <p>
 * With a fake clock, time is under the test's control: these tests check the
 * refill logic exactly, finish in microseconds, and can never be flaky or hang.
 * The rest of this package uses the real clock, where {@code @Timeout} is the
 * safety net.
 */
class TokenBucketFakeClockTest {

    private final FakeNanoClock clock = new FakeNanoClock();
    private TokenBucket bucket;

    @BeforeEach
    void createBucket() {
        bucket = new TokenBucket(3, 10, clock); // 3 tokens, one back every 100 ms
    }

    @Test
    void startsFull() {
        assertEquals(3, bucket.availableTokens());
    }

    @Test
    void refusesWhenEmpty() {
        assertTrue(bucket.tryAcquire());
        assertTrue(bucket.tryAcquire());
        assertTrue(bucket.tryAcquire());
        assertFalse(bucket.tryAcquire());
    }

    @Test
    void refillsOneTokenPerHundredMilliseconds() {
        drain();
        clock.advance(Duration.ofMillis(99));
        assertFalse(bucket.tryAcquire(), "not yet: 1 ms short");
        clock.advance(Duration.ofMillis(1));
        assertTrue(bucket.tryAcquire());
        assertFalse(bucket.tryAcquire(), "exactly one token came back");
    }

    @Test
    void neverExceedsCapacity() {
        drain();
        clock.advance(Duration.ofHours(1));
        assertEquals(3, bucket.availableTokens());
    }

    @Test
    void rejectsInvalidSettings() {
        assertThrows(IllegalArgumentException.class, () -> new TokenBucket(0, 10, clock));
        assertThrows(IllegalArgumentException.class, () -> new TokenBucket(3, 0, clock));
    }

    private void drain() {
        while (bucket.tryAcquire()) {
            // take all tokens
        }
    }
}
