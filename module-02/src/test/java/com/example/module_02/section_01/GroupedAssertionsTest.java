package com.example.module_02.section_01;

import static com.example.module_02.support.MoneyAssertions.assertAmountEquals;
import static com.example.module_02.support.TestAccounts.eur;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.bank.AccountStatus;
import com.example.bank.BankAccount;
import com.example.bank.Transaction;
import com.example.module_02.support.TestAccounts;

/**
 * Module 2.1 – grouped assertions with {@code assertAll}.
 * <p>
 * Without {@code assertAll}, the first failing assertion stops the test and
 * hides the rest. With it, every assertion in the group runs and all failures
 * are reported together (see {@code AssertionFailuresDemoTest}).
 */
class GroupedAssertionsTest {

    private final BankAccount account = TestAccounts.withHundred();

    @Test
    void groupedAssertions() {
        account.deposit(eur("50"));

        assertAll("account after one deposit",
            () -> assertEquals(AccountStatus.ACTIVE, account.getStatus()),
            () -> assertAmountEquals("150.00", account.getBalance()),
            () -> assertEquals(1, account.getTransactions().size()));
    }

    @Test
    void dependentAssertions() {
        account.deposit(eur("50")).withdraw(eur("30"));

        assertAll("transactions",
            () -> {
                List<Transaction> history = account.getTransactions();
                assertNotNull(history);
                // Only runs if the line above passed: inside one block, a failure stops the block.
                assertAll("history",
                    () -> assertEquals(2, history.size()),
                    () -> assertEquals(Transaction.Type.DEPOSIT, history.get(0).type()),
                    () -> assertEquals(Transaction.Type.WITHDRAWAL, history.get(1).type()));
            },
            () -> {
                // Independent of the block above: runs even if that one failed.
                String iban = account.getIban();
                assertNotNull(iban);
                assertAll("iban",
                    () -> assertTrue(iban.startsWith("NL")),
                    () -> assertFalse(iban.contains(" ")));
            });
    }
}
