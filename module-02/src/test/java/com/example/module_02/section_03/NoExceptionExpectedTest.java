package com.example.module_02.section_03;

import static com.example.module_02.support.MoneyAssertions.assertAmountEquals;
import static com.example.module_02.support.TestAccounts.eur;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.example.bank.AccountStatus;
import com.example.bank.BankAccount;
import com.example.module_02.support.TestAccounts;

/**
 * Module 2.3 – asserting that NO exception is thrown, and what a {@code throws} clause means.
 */
class NoExceptionExpectedTest {

    private final BankAccount account = TestAccounts.withHundred();

    @Test
    void withdrawingTheExactBalanceIsAllowed() {
        // Any exception would fail the test anyway; assertDoesNotThrow states the intent explicitly.
        assertDoesNotThrow(() -> {
            account.withdraw(eur("100"));
        });
        assertAmountEquals("0", account.getBalance());
    }

    @Test
    void assertDoesNotThrowReturnsTheResult() {
        BigDecimal balance = assertDoesNotThrow(() -> account.deposit(eur("1")).getBalance(),
            "deposit on an active account must succeed");
        assertAmountEquals("101", balance);
    }

    @Test
    void freezeAndUnfreezeRoundTrip() {
        assertDoesNotThrow(() -> {
            account.freeze();
            account.unfreeze();
        });
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
    }

    @Test
    void aThrowsClauseIsNotAnExpectation() throws IOException {
        // Declaring "throws IOException" does NOT mean the test expects one.
        // This test passes because nothing is thrown.
        assertEquals(3, account.statement().size());
    }
}
