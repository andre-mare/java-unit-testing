package com.example.module_05.section_02;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.ratelimit.TokenBucket;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Module 5.2 lab – filtering with tag expressions.
 * <p>
 * Five tests with different tags. Predict which run for each expression, then check:
 * <pre>
 * mvn test -Dtest=TagExpressionsTest -Dgroups="EXPRESSION"
 * </pre>
 * <table>
 *   <caption>Tag expressions and the tests they select</caption>
 *   <tr><th>{@code -Dgroups=...}</th><th>runs</th></tr>
 *   <tr><td>{@code fast}</td><td>fastOnly, fastAndSecurity</td></tr>
 *   <tr><td>{@code !slow}</td><td>everything except slowOnly</td></tr>
 *   <tr><td>{@code fast & security}</td><td>fastAndSecurity</td></tr>
 *   <tr><td>{@code smoke | slow}</td><td>slowOnly, smokeAndSecurity</td></tr>
 *   <tr><td>{@code (fast | smoke) & !security}</td><td>fastOnly</td></tr>
 *   <tr><td>{@code any()}</td><td>every tagged test (all but untagged)</td></tr>
 *   <tr><td>{@code none()}</td><td>untagged</td></tr>
 * </table>
 * Operators: {@code !} not, {@code &} and, {@code |} or, parentheses for grouping.
 * {@code -DexcludedGroups=...} removes tests in the same way, applied after {@code groups}.
 * Quote expressions in the shell: {@code &}, {@code |} and {@code !} have their own meaning there.
 */
class TagExpressionsTest {

    @Test
    @Tag("fast")
    void fastOnly() {
        assertTrue(new TokenBucket(1, 1).tryAcquire());
    }

    @Test
    @Tag("slow")
    void slowOnly() throws InterruptedException {
        TokenBucket bucket = new TokenBucket(1, 5);
        bucket.acquire();
        bucket.acquire(); // waits about 200 ms for a refill
    }

    @Test
    @Tag("fast")
    @Tag("security")
    void fastAndSecurity() {
        TokenBucket bucket = new TokenBucket(2, 1);
        bucket.tryAcquire();
        bucket.tryAcquire();
        assertFalse(bucket.tryAcquire(), "a brute-force burst is cut off at capacity");
    }

    @Test
    @Tag("smoke")
    @Tag("security")
    void smokeAndSecurity() {
        assertTrue(new TokenBucket(10, 10).availableTokens() > 0);
    }

    @Test
    void untagged() {
        assertTrue(new TokenBucket(3, 1).capacity() == 3);
    }
}
