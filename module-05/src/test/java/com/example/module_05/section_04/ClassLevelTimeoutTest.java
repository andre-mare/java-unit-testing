package com.example.module_05.section_04;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import com.example.ratelimit.TokenBucket;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Module 5.4 – {@code @Timeout} on a class.
 * <ul>
 *   <li>Applies to every test, test factory and test template method in the class
 *       and in its {@code @Nested} classes.</li>
 *   <li>Does NOT apply to lifecycle methods: {@code @BeforeAll} below takes 1.2 s
 *       against a 1 s class timeout and still passes.</li>
 *   <li>A method-level {@code @Timeout} overrides the class value.</li>
 * </ul>
 * The class takes about 3 seconds to run.
 */
@Timeout(value = 1, unit = TimeUnit.SECONDS)
class ClassLevelTimeoutTest {

    @BeforeAll
    static void slowSetup() throws InterruptedException {
        Thread.sleep(1_200); // longer than the class timeout: lifecycle methods are exempt
    }

    @Test
    void inheritsTheClassTimeout() {
        assertTrue(new TokenBucket(1, 1).tryAcquire());
    }

    @Test
    @Timeout(3) // overrides the class-level 1 second
    void methodLevelTimeoutWins() throws InterruptedException {
        Thread.sleep(1_500);
        assertTrue(true);
    }

    @Nested
    class NestedTests {

        @Test
        void nestedTestsInheritTheTimeoutToo() {
            assertTrue(new TokenBucket(2, 1).availableTokens() == 2);
        }
    }
}
