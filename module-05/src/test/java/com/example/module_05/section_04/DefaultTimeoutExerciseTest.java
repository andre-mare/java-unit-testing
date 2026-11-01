package com.example.module_05.section_04;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.ratelimit.TokenBucket;

import org.junit.jupiter.api.Test;

/**
 * Module 5.4 lab – default timeouts and switching timeouts off, via configuration.
 * <p>
 * No {@code @Timeout} here; the test takes about 200 ms. Run it four ways:
 * <pre>
 * 1. mvn test -Dtest=DefaultTimeoutExerciseTest
 *      passes: no timeout at all
 * 2. ... -Djunit.jupiter.execution.timeout.test.method.default=100ms
 *      fails: every @Test now has a 100 ms default
 * 3. ... -Djunit.jupiter.execution.timeout.test.method.default=100ms -Djunit.jupiter.execution.timeout.mode=disabled
 *      passes: timeouts are ignored (use disabled_on_debug to ignore them only under a debugger)
 * 4. ... -Djunit.jupiter.execution.timeout.test.method.default=100ms -Djunit.jupiter.execution.timeout.threaddump.enabled=true
 *      fails, and prints a dump of all threads first: handy to see where a test hangs
 * </pre>
 * Value format: a number plus an optional unit ({@code ns}, {@code μs}, {@code ms},
 * {@code s}, {@code m}, {@code h}, {@code d}); no unit means seconds. More specific
 * parameters win: {@code ...test.method.default} over
 * {@code ...testable.method.default} over {@code ...timeout.default}.
 * Lifecycle methods have their own ({@code ...beforeeach.method.default} and so on).
 */
class DefaultTimeoutExerciseTest {

    @Test
    void takesAboutTwoHundredMilliseconds() throws InterruptedException {
        TokenBucket bucket = new TokenBucket(1, 5); // one token back every 200 ms
        bucket.acquire();
        bucket.acquire();
        assertEquals(0, bucket.availableTokens());
    }
}
