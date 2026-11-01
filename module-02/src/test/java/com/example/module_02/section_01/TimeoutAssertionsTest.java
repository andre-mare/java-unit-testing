package com.example.module_02.section_01;

import static com.example.module_02.support.TestAccounts.eur;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.example.bank.BankAccount;
import com.example.module_02.support.TestAccounts;

/**
 * Module 2.1 – {@code assertTimeout} vs {@code assertTimeoutPreemptively}.
 * <ul>
 *   <li>{@code assertTimeout} runs the code in the SAME thread and waits for it to
 *       finish, then fails if it took too long.</li>
 *   <li>{@code assertTimeoutPreemptively} runs the code in a SEPARATE thread and
 *       aborts it as soon as the time is up. Anything bound to the test thread,
 *       such as a {@link ThreadLocal}, is not visible inside it.</li>
 * </ul>
 * The {@code ThreadLocal} tests show exactly that difference. Failing examples are
 * in {@code AssertionFailuresDemoTest}.
 */
class TimeoutAssertionsTest {

    /** Stands in for thread-bound state such as a Spring-managed transaction. */
    private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();

    private final BankAccount account = TestAccounts.withHundred();

    @AfterEach
    void clearThreadLocal() {
        CURRENT_USER.remove();
    }

    @Test
    void timeoutNotExceeded() {
        assertTimeout(ofMillis(500), () -> account.deposit(eur("10")));
    }

    @Test
    void timeoutReturnsTheResult() {
        List<String> statement = assertTimeout(ofSeconds(1), account::statement);
        assertEquals(3, statement.size());
    }

    @Test
    void preemptiveTimeoutNotExceeded() {
        BigDecimal balance = assertTimeoutPreemptively(ofMillis(500), account::getBalance);
        assertEquals(eur("100.00"), balance);
    }

    @Test
    void sameThreadSeesThreadLocal() {
        CURRENT_USER.set("jane");
        String seen = assertTimeout(ofSeconds(1), CURRENT_USER::get);
        assertEquals("jane", seen);
    }

    @Test
    void separateThreadDoesNotSeeThreadLocal() {
        CURRENT_USER.set("jane");
        String seen = assertTimeoutPreemptively(ofSeconds(1), CURRENT_USER::get);
        assertNull(seen, "assertTimeoutPreemptively runs in another thread");
    }
}
