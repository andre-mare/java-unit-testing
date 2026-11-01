package com.example.bank;

/** Lifecycle state of a {@link BankAccount}. Only ACTIVE accounts accept deposits and withdrawals. */
public enum AccountStatus {
    ACTIVE,
    FROZEN,
    CLOSED
}
