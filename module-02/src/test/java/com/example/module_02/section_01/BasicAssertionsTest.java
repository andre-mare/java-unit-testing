package com.example.module_02.section_01;

import static com.example.module_02.support.TestAccounts.eur;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.example.bank.AccountException;
import com.example.bank.AccountStatus;
import com.example.bank.BankAccount;
import com.example.bank.InsufficientFundsException;

import com.example.module_02.support.TestAccounts;

import org.junit.jupiter.api.Test;

/**
 * Module 2.1 – the single-value assertions in {@code org.junit.jupiter.api.Assertions}.
 * <p>
 * Start here. Each test uses one assertion family so you can read them one by one.
 * From this module on, tests use the {@code @UnitTest} annotation built in Module 1.7.
 */
class BasicAssertionsTest {

    private final BankAccount account = TestAccounts.withHundred();

    @Test
    void assertEqualsAndNotEquals() {
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertEquals("Jane Doe", account.getOwner());
        assertNotEquals(AccountStatus.FROZEN, account.getStatus());
    }

    @Test
    void assertTrueAndFalse() {
        assertTrue(account.getTransactions().isEmpty());
        account.deposit(eur("50"));
        assertFalse(account.getTransactions().isEmpty());
    }

    @Test
    void assertNullAndNotNull() {
        assertNotNull(account.getIban());
        String noMiddleName = null;
        assertNull(noMiddleName);
    }

    @Test
    void assertSameAndNotSame() {
        // deposit() returns `this` for chaining: the very same object, not just an equal one.
        assertSame(account, account.deposit(eur("1")));
        assertNotSame(account, TestAccounts.withHundred(), "a second factory call creates a new object");
    }

    @Test
    void assertInstanceOfReturnsTheCastValue() {
        Exception thrown = new InsufficientFundsException("NL00TEST", eur("10.00"), eur("20.00"));
        // assertInstanceOf checks the type and hands back a typed reference.
        AccountException accountError = assertInstanceOf(AccountException.class, thrown);
        assertEquals("NL00TEST", accountError.getIban());
    }

    @Test
    void failMarksAnUnreachableBranch() {
        switch (account.getStatus()) {
            case ACTIVE -> assertTrue(true);
            case FROZEN, CLOSED -> fail("a new account must be ACTIVE");
        }
    }
}
