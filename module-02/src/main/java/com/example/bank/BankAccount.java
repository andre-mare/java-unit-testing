package com.example.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple bank account, the production class under test from Module 2 onwards.
 * <p>
 * Design choices that make it a good testing subject:
 * <ul>
 *   <li>Money is {@link BigDecimal} at scale 2, which exposes the
 *       {@code equals} vs {@code compareTo} trap (Module 2.1).</li>
 *   <li>State ({@link AccountStatus}) changes what is allowed, which suits
 *       nested tests (Module 4.2).</li>
 *   <li>Every rule violation throws a specific exception (Module 2.3).</li>
 *   <li>{@link #statement()} returns text lines for {@code assertLinesMatch}.</li>
 * </ul>
 */
public class BankAccount {

    private static final int SCALE = 2;

    private final String iban;
    private final String owner;
    private BigDecimal balance;
    private AccountStatus status = AccountStatus.ACTIVE;
    private final List<Transaction> transactions = new ArrayList<>();

    public BankAccount(String iban, String owner, BigDecimal openingBalance) {
        this.iban = requireText(iban, "iban");
        this.owner = requireText(owner, "owner");
        Objects.requireNonNull(openingBalance, "openingBalance");
        if (openingBalance.signum() < 0) {
            throw new IllegalArgumentException("Opening balance must not be negative: " + openingBalance);
        }
        this.balance = scaled(openingBalance);
    }

    public String getIban() {
        return iban;
    }

    public String getOwner() {
        return owner;
    }

    /** Current balance, always scale 2 (so 100 is returned as 100.00). */
    public BigDecimal getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    /** An unmodifiable snapshot of the booked transactions, oldest first. */
    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    /** Deposits money; returns {@code this} so calls can be chained. */
    public BankAccount deposit(BigDecimal amount) {
        requireActive();
        BigDecimal value = requirePositive(amount);
        balance = balance.add(value);
        transactions.add(new Transaction(Transaction.Type.DEPOSIT, value, balance));
        return this;
    }

    /** Withdraws money; returns {@code this} so calls can be chained. */
    public BankAccount withdraw(BigDecimal amount) {
        requireActive();
        BigDecimal value = requirePositive(amount);
        if (balance.compareTo(value) < 0) {
            throw new InsufficientFundsException(iban, balance, value);
        }
        balance = balance.subtract(value);
        transactions.add(new Transaction(Transaction.Type.WITHDRAWAL, value, balance));
        return this;
    }

    public void freeze() {
        if (status == AccountStatus.CLOSED) {
            throw new AccountNotActiveException(iban, status);
        }
        status = AccountStatus.FROZEN;
    }

    public void unfreeze() {
        if (status != AccountStatus.FROZEN) {
            throw new IllegalStateException("Only a FROZEN account can be unfrozen; status is " + status);
        }
        status = AccountStatus.ACTIVE;
    }

    /** Closes the account; only allowed when the balance is exactly zero. */
    public void close() {
        if (balance.signum() != 0) {
            throw new IllegalStateException("Cannot close " + iban + " with balance " + balance.toPlainString());
        }
        status = AccountStatus.CLOSED;
    }

    /**
     * A printable statement, one line per transaction. Example:
     * <pre>
     * Statement NL91ABNA0417164300 (Jane Doe)
     * Opening balance: 100.00
     * DEPOSIT +50.00 -> 150.00
     * WITHDRAWAL -30.00 -> 120.00
     * Closing balance: 120.00
     * </pre>
     */
    public List<String> statement() {
        List<String> lines = new ArrayList<>();
        lines.add("Statement " + iban + " (" + owner + ")");
        lines.add("Opening balance: " + openingBalance().toPlainString());
        for (Transaction t : transactions) {
            String sign = t.type() == Transaction.Type.DEPOSIT ? "+" : "-";
            lines.add(t.type() + " " + sign + t.amount().toPlainString() + " -> " + t.balanceAfter().toPlainString());
        }
        lines.add("Closing balance: " + balance.toPlainString());
        return lines;
    }

    private BigDecimal openingBalance() {
        BigDecimal opening = balance;
        for (Transaction t : transactions) {
            opening = opening.subtract(t.signedAmount());
        }
        return opening;
    }

    private void requireActive() {
        if (status != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException(iban, status);
        }
    }

    private static BigDecimal requirePositive(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount");
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive: " + amount);
        }
        return scaled(amount);
    }

    private static BigDecimal scaled(BigDecimal amount) {
        return amount.setScale(SCALE, RoundingMode.HALF_EVEN);
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
