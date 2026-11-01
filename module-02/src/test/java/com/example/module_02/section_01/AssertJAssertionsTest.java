package com.example.module_02.section_01;

import static com.example.module_02.support.TestAccounts.eur;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.bank.AccountStatus;
import com.example.bank.BankAccount;
import com.example.bank.InsufficientFundsException;
import com.example.bank.Transaction;

import com.example.module_02.support.TestAccounts;

import org.junit.jupiter.api.Test;

/**
 * Module 2.1 – a third-party assertion library: AssertJ.
 * <p>
 * The same checks as the JUnit tests in this package, written fluently.
 * Compare with {@code BigDecimalAssertionsTest}: AssertJ's
 * {@code isEqualByComparingTo} solves the scale trap without a custom helper.
 * <p>
 * Requires the {@code assertj-core} test dependency. To stay on pure JUnit,
 * delete this class and the dependency.
 */
class AssertJAssertionsTest {

    private final BankAccount account = TestAccounts.withHundred();

    @Test
    void fluentValueAssertions() {
        account.deposit(eur("50"));

        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.getBalance()).isEqualByComparingTo("150");
        assertThat(account.getIban()).startsWith("NL").hasSize(18);
    }

    @Test
    void fluentCollectionAssertions() {
        account.deposit(eur("50")).withdraw(eur("30"));

        assertThat(account.getTransactions())
            .hasSize(2)
            .extracting(Transaction::type)
            .containsExactly(Transaction.Type.DEPOSIT, Transaction.Type.WITHDRAWAL);
    }

    @Test
    void fluentExceptionAssertions() {
        assertThatThrownBy(() -> account.withdraw(eur("500")))
            .isInstanceOf(InsufficientFundsException.class)
            .hasMessageContaining("requested 500.00");
    }
}
