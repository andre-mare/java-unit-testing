package com.example.module_02.section_01;

import static com.example.module_02.support.TestAccounts.eur;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import com.example.bank.BankAccount;
import com.example.module_02.support.TestAccounts;

import org.junit.jupiter.api.Test;


/**
 * Module 2.1 – optional failure messages: a String or a lazy {@code Supplier<String>}.
 * <p>
 * The message is always the LAST argument. A Supplier is only evaluated when the
 * assertion fails, so expensive messages cost nothing on passing tests.
 * To see the messages printed, look at {@code AssertionFailuresDemoTest}.
 */
class FailureMessagesTest {

    private final BankAccount account = TestAccounts.withHundred();

    @Test
    void stringMessage() {
        assertEquals(1, account.deposit(eur("5")).getTransactions().size(),
            "one deposit should book exactly one transaction");
    }

    @Test
    void supplierMessageIsLazy() {
        AtomicInteger messagesBuilt = new AtomicInteger();

        assertTrue(account.getBalance().signum() > 0, () -> {
            messagesBuilt.incrementAndGet();
            return "balance should be positive but statement was:\n"
                + String.join("\n", account.statement());
        });

        assertEquals(0, messagesBuilt.get(), "the supplier only runs when the assertion fails");
    }
}
