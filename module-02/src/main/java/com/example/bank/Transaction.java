package com.example.bank;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * One booked movement on a {@link BankAccount}.
 *
 * @param type         deposit or withdrawal
 * @param amount       always positive, scale 2
 * @param balanceAfter the account balance right after this transaction, scale 2
 */
public record Transaction(Type type, BigDecimal amount, BigDecimal balanceAfter) {

    public enum Type { DEPOSIT, WITHDRAWAL }

    public Transaction {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(balanceAfter, "balanceAfter");
    }

    /** Amount with a sign: positive for deposits, negative for withdrawals. */
    public BigDecimal signedAmount() {
        return type == Type.DEPOSIT ? amount : amount.negate();
    }
}
