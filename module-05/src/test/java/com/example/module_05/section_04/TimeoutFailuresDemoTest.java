package com.example.module_05.section_04;

import java.util.concurrent.TimeUnit;

import com.example.ratelimit.TokenBucket;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Timeout.ThreadMode;

/**
 * Module 5.4 – every test here FAILS on purpose with a timeout. Read the messages.
 * <p>
 * Tagged {@code expected-failure-tests} and excluded from the normal build. Run it with:
 * <pre>mvn test -Dtest=TimeoutFailuresDemoTest -DexcludedGroups=none</pre>
 * Expected messages look like
 * {@code waitForever() timed out after 200 milliseconds}.
 */
@Tag("expected-failure-tests")
class TimeoutFailuresDemoTest {

    @Test
    @Timeout(value = 200, unit = TimeUnit.MILLISECONDS)
    void waitForever() throws InterruptedException {
        TokenBucket bucket = new TokenBucket(1, 1);
        bucket.acquire();
        bucket.acquire();
        bucket.acquire(); // needs about 2 s; interrupted at 200 ms
    }

    @Test
    @Timeout(value = 200, unit = TimeUnit.MILLISECONDS, threadMode = ThreadMode.SEPARATE_THREAD)
    void waitForeverInASeparateThread() throws InterruptedException {
        Thread.sleep(2_000);
    }
}
