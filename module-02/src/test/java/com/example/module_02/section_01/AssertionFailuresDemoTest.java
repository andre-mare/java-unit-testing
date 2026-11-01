package com.example.module_02.section_01;

import static com.example.module_02.support.MoneyAssertions.assertAmountEquals;
import static com.example.module_02.support.TestAccounts.eur;
import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.example.bank.AccountStatus;
import com.example.bank.BankAccount;
import com.example.module_02.support.TestAccounts;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Module 2.1 – every test here FAILS on purpose, so you can read JUnit's failure messages.
 * <p>
 * Tagged {@code failure-demo} and excluded from the normal build. Run it with:
 * <pre>mvn test -Dtest=AssertionFailuresDemoTest -DexcludedGroups=none</pre>
 * For each test, predict the message before you run it.
 */
@Tag("expected-failure-tests")
class AssertionFailuresDemoTest {

    private final BankAccount account = TestAccounts.withHundred();

    @Test
    void plainMismatch() {
        // expected: <150.00> but was: <100.00>
        assertEquals(eur("150.00"), account.getBalance());
    }

    @Test
    void scaleTrap() {
        // expected: <100> but was: <100.00>  – looks equal, is not
        assertEquals(new BigDecimal("100"), account.getBalance());
    }

    @Test
    void customMoneyAssertion() {
        // amounts differ ==> expected: <99.99> but was: <100.00>
        assertAmountEquals("99.99", account.getBalance());
    }

    @Test
    void lazyMessageIsBuiltOnFailure() {
        assertTrue(account.getBalance().signum() < 0,
            () -> "expected a negative balance; statement:\n" + String.join("\n", account.statement()));
    }

    @Test
    void groupedFailuresAreAllReported() {
        // One report listing BOTH failures, not just the first.
        assertAll("account",
            () -> assertEquals(AccountStatus.FROZEN, account.getStatus()),
            () -> assertEquals("John Doe", account.getOwner()),
            () -> assertEquals(18, account.getIban().length()));
    }

    @Test
    void iterableMismatchShowsTheIndex() {
        assertIterableEquals(List.of("a", "b", "c"), List.of("a", "x", "c"));
    }

    @Test
    void linesMismatch() {
        assertLinesMatch(List.of("Statement .*", "Closing balance: 0.00"), account.statement());
    }

    @Test
    void timeoutExceededAfterCompletion() {
        // execution exceeded timeout of 10 ms by ~90 ms  (waited for the code to finish)
        assertTimeout(ofMillis(10), () -> Thread.sleep(100));
    }

    @Test
    void timeoutExceededPreemptively() {
        // execution timed out after 10 ms  (the code was aborted; it would wait forever)
        assertTimeoutPreemptively(ofMillis(10), () -> new CountDownLatch(1).await());
    }
}
