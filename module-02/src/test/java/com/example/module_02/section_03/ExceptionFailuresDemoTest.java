package com.example.module_02.section_03;

import static com.example.module_02.support.TestAccounts.eur;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.example.bank.AccountException;
import com.example.bank.AccountNotActiveException;
import com.example.bank.BankAccount;
import com.example.module_02.support.TestAccounts;

/**
 * Module 2.3 – every test here FAILS on purpose. Read each failure message.
 * <p>
 * Tagged {@code expected-failure-tests} and excluded from the normal build. Run it with:
 * <pre>mvn test -Dtest=ExceptionFailuresDemoTest -DexcludedGroups=none</pre>
 */
@Tag("expected-failure-tests")
class ExceptionFailuresDemoTest {

    private final BankAccount account = TestAccounts.withHundred();

    @Test
    void uncaughtExceptionFailsTheTest() {
        // No assertion: the InsufficientFundsException itself fails the test.
        account.withdraw(eur("500"));
    }

    @Test
    void nothingWasThrown() {
        // Expected InsufficientFundsException to be thrown, but nothing was thrown.
        assertThrows(AccountException.class, () -> account.withdraw(eur("10")));
    }

    @Test
    void wrongTypeWasThrown() {
        // Unexpected exception type thrown: expected AccountNotActive..., but was InsufficientFunds...
        assertThrows(AccountNotActiveException.class, () -> account.withdraw(eur("500")));
    }

    @Test
    void exactlyRejectsASubtype() {
        // Passes with assertThrows, fails with assertThrowsExactly.
        assertThrowsExactly(AccountException.class, () -> account.withdraw(eur("500")));
    }

    @Test
    void assertDoesNotThrowReportsTheException() {
        // Unexpected exception thrown: InsufficientFundsException: Insufficient funds on ...
        assertDoesNotThrow(() -> {
            account.withdraw(eur("500"));
        });
    }
}
