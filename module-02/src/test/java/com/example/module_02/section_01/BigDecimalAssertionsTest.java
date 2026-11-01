package com.example.module_02.section_01;

import static com.example.module_02.support.MoneyAssertions.assertAmountEquals;
import static com.example.module_02.support.TestAccounts.eur;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.math.BigDecimal;

import com.example.bank.BankAccount;
import com.example.module_02.support.TestAccounts;

import org.junit.jupiter.api.Test;

/**
 * Module 2.1 lab – the BigDecimal scale trap, and the custom assertion that fixes it.
 */
class BigDecimalAssertionsTest {

    private final BankAccount account = TestAccounts.withHundred();

    @Test
    void equalsComparesScaleToo() {
        // Same number, different scale: equals() says they differ.
        assertNotEquals(new BigDecimal("10.0"), new BigDecimal("10.00"));
        // compareTo() looks at the value only.
        assertEquals(0, new BigDecimal("10.0").compareTo(new BigDecimal("10.00")));
    }

    @Test
    void balanceIsAlwaysScaleTwo() {
        // The account was opened with "100" but stores 100.00.
        assertNotEquals(new BigDecimal("100"), account.getBalance());
        assertEquals(new BigDecimal("100.00"), account.getBalance());
    }

    @Test
    void customAssertionIgnoresScale() {
        account.deposit(eur("50")).withdraw(eur("0.5"));
        assertAmountEquals("149.5", account.getBalance());
        assertAmountEquals("149.50", account.getBalance());
    }
}
