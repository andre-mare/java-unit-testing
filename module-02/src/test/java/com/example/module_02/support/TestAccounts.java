package com.example.module_02.support;

import java.math.BigDecimal;

import com.example.bank.BankAccount;

/**
 * Test data factory: one place to create accounts so tests stay short.
 * The IBAN is a well-known valid Dutch example IBAN.
 */
public final class TestAccounts {

    public static final String IBAN = "NL91ABNA0417164300";
    public static final String OWNER = "Jane Doe";

    private TestAccounts() {
    }

    /** An ACTIVE account with an opening balance of 100.00. */
    public static BankAccount withHundred() {
        return new BankAccount(IBAN, OWNER, new BigDecimal("100"));
    }

    /** An ACTIVE account with a zero balance. */
    public static BankAccount empty() {
        return new BankAccount(IBAN, OWNER, BigDecimal.ZERO);
    }

    /** Shorthand for {@code new BigDecimal(text)}. */
    public static BigDecimal eur(String text) {
        return new BigDecimal(text);
    }
}
