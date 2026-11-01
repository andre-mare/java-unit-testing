package com.example.bank;

import java.math.BigDecimal;

/** Thrown when a withdrawal is larger than the current balance. */
public class InsufficientFundsException extends AccountException {

    private final BigDecimal balance;
    private final BigDecimal requested;

    public InsufficientFundsException(String iban, BigDecimal balance, BigDecimal requested) {
        super(iban, "Insufficient funds on " + iban + ": balance " + balance.toPlainString()
            + ", requested " + requested.toPlainString());
        this.balance = balance;
        this.requested = requested;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getRequested() {
        return requested;
    }

    /** How much more money would have been needed. */
    public BigDecimal getShortfall() {
        return requested.subtract(balance);
    }
}
