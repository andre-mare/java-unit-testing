package com.example.module_02.section_03;

import static com.example.module_02.support.TestAccounts.eur;
import static com.example.module_02.support.MoneyAssertions.assertAmountEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.example.bank.AccountException;
import com.example.bank.AccountNotActiveException;
import com.example.bank.AccountStatus;
import com.example.bank.BankAccount;
import com.example.bank.InsufficientFundsException;
import com.example.module_02.support.BasicArithmetic;
import com.example.module_02.support.TestAccounts;

/**
 * Module 2.3 lab – asserting that the right exception is thrown.
 * <ul>
 *   <li>{@code assertThrows(Type, code)} passes for Type OR any subtype.</li>
 *   <li>{@code assertThrowsExactly(Type, code)} passes only for exactly Type.</li>
 *   <li>Both return the exception, so you can check its message and fields.</li>
 * </ul>
 * Always also check that the failed operation changed nothing.
 */
class ExpectedExceptionsTest {

    private final BankAccount account = TestAccounts.withHundred();

    @Test
    void withdrawMoreThanBalance() {
        InsufficientFundsException e = assertThrows(InsufficientFundsException.class,
            () -> account.withdraw(eur("150")));

        assertEquals("Insufficient funds on NL91ABNA0417164300: balance 100.00, requested 150.00",
            e.getMessage());
        assertAmountEquals("50", e.getShortfall());
        assertAmountEquals("100.00", account.getBalance()); // nothing changed
        assertTrue(account.getTransactions().isEmpty());
    }

    @Test
    void assertThrowsAcceptsASupertype() {
        // InsufficientFundsException IS-A AccountException IS-A RuntimeException.
        assertThrows(AccountException.class, () -> account.withdraw(eur("150")));
        assertThrows(RuntimeException.class, () -> account.withdraw(eur("150")));
    }

    @Test
    void assertThrowsExactlyWantsTheExactType() {
        assertThrowsExactly(InsufficientFundsException.class, () -> account.withdraw(eur("150")));
        // assertThrowsExactly(AccountException.class, ...) would FAIL – see ExceptionFailuresDemoTest.
    }

    @Test
    void frozenAccountRejectsDeposits() {
        account.freeze();

        AccountNotActiveException e = assertThrows(AccountNotActiveException.class,
            () -> account.deposit(eur("10")));

        assertEquals(AccountStatus.FROZEN, e.getStatus());
        assertEquals(account.getIban(), e.getIban());
    }

    @Test
    void invalidAmounts() {
        assertThrows(IllegalArgumentException.class, () -> account.deposit(eur("0")));
        assertThrows(IllegalArgumentException.class, () -> account.deposit(eur("-5")));
        NullPointerException npe = assertThrows(NullPointerException.class, () -> account.deposit(null));
        assertEquals("amount", npe.getMessage());
    }

    @Test
    void invalidConstruction() {
        assertThrows(IllegalArgumentException.class,
            () -> new BankAccount("NL91ABNA0417164300", "Jane Doe", new BigDecimal("-1")));
        assertThrows(IllegalArgumentException.class,
            () -> new BankAccount(" ", "Jane Doe", BigDecimal.ZERO));
    }

    @Test
    void closeWithMoneyLeftIsIllegalState() {
        IllegalStateException e = assertThrows(IllegalStateException.class, account::close);
        assertTrue(e.getMessage().contains("100.00"));
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
    }

    @Test
    void calculatorErrorPaths() {
        BasicArithmetic calculator = new BasicArithmetic();

        ArithmeticException divide = assertThrows(ArithmeticException.class, () -> calculator.divide(1, 0));
        assertEquals("/ by zero", divide.getMessage());

        assertThrows(ArithmeticException.class, () -> calculator.add(Integer.MAX_VALUE, 1));
        assertThrows(IllegalArgumentException.class, () -> calculator.sqrt(-1));
        assertThrows(IllegalArgumentException.class, () -> calculator.factorial(-1));
        assertThrows(ArithmeticException.class, () -> calculator.factorial(21));
    }
}
