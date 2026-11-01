package com.example.module_05.section_04;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.example.ratelimit.TokenBucket;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.Timeout;

/**
 * Module 5.4 – where {@code @Timeout} applies for repeated tests and test factories.
 * <ul>
 *   <li>On a test template such as {@code @RepeatedTest} or {@code @ParameterizedTest}:
 *       each invocation gets the full timeout.</li>
 *   <li>On a {@code @TestFactory}: only the factory method itself is timed, not
 *       the dynamic tests it returns. Here three dynamic tests take about
 *       1.2 s together against a 1 s timeout, and still pass. Time individual
 *       dynamic tests with {@code assertTimeout} instead.</li>
 * </ul>
 * Repeated tests and dynamic tests are covered in Modules 7 and 8.
 */
class TimeoutOnTemplatesTest {

    @RepeatedTest(3)
    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS) // per repetition, not for all three
    void eachRepetitionHasItsOwnTimeout() throws InterruptedException {
        Thread.sleep(300); // 3 x 300 ms = 900 ms in total, but under 500 ms each
        assertTrue(new TokenBucket(1, 1).tryAcquire());
    }

    @TestFactory
    @Timeout(1) // checks only how long the factory takes to return the stream
    Stream<DynamicTest> factoryTimeoutDoesNotCoverDynamicTests() {
        return Stream.of(1, 2, 3).map(n -> dynamicTest("dynamic test " + n, () -> {
            Thread.sleep(400);
            assertTrue(new TokenBucket(n, 1).availableTokens() == n);
        }));
    }
}
