package com.example.module_02.section_01;

import static com.example.module_02.support.TestAccounts.eur;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.bank.BankAccount;
import com.example.bank.Transaction;
import com.example.module_02.support.TestAccounts;


/**
 * Module 2.1 – assertions for arrays, iterables and lines of text.
 */
class CollectionAssertionsTest {

    private BankAccount account;

    @BeforeEach
    void bookThreeTransactions() {
        account = TestAccounts.withHundred()
            .deposit(eur("50"))
            .withdraw(eur("30"))
            .deposit(eur("5.25"));
    }

    @Test
    void assertArrayEqualsComparesElementByElement() {
        BigDecimal[] amounts = account.getTransactions().stream()
            .map(Transaction::amount)
            .toArray(BigDecimal[]::new);

        assertArrayEquals(new BigDecimal[] { eur("50.00"), eur("30.00"), eur("5.25") }, amounts);
    }

    @Test
    void assertIterableEqualsComparesInOrder() {
        List<Transaction.Type> types = account.getTransactions().stream()
            .map(Transaction::type)
            .toList();

        assertIterableEquals(
            List.of(Transaction.Type.DEPOSIT, Transaction.Type.WITHDRAWAL, Transaction.Type.DEPOSIT),
            types);
    }

    @Test
    void assertLinesMatchExactLines() {
        assertLinesMatch(List.of(
            "Statement NL91ABNA0417164300 (Jane Doe)",
            "Opening balance: 100.00",
            "DEPOSIT +50.00 -> 150.00",
            "WITHDRAWAL -30.00 -> 120.00",
            "DEPOSIT +5.25 -> 125.25",
            "Closing balance: 125.25"),
            account.statement());
    }

    @Test
    void assertLinesMatchWithRegexAndSkip() {
        // Each expected line is tried as equals() first, then as a regular expression.
        // ">> ... >>" is a fast-forward marker: skip any number of lines until the next match.
        assertLinesMatch(List.of(
            "Statement NL\\d{2}[A-Z]{4}\\d{10} \\(.+\\)",
            ">> transactions >>",
            "Closing balance: \\d+\\.\\d{2}"),
            account.statement());
    }
}
