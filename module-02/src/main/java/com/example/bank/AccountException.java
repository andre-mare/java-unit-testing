package com.example.bank;

/**
 * Base type for business errors on a {@link BankAccount}.
 * <p>
 * Having a common supertype lets Module 2.3 show the difference between
 * {@code assertThrows} (accepts subtypes) and {@code assertThrowsExactly} (does not).
 */
public abstract class AccountException extends RuntimeException {

    private final String iban;

    protected AccountException(String iban, String message) {
        super(message);
        this.iban = iban;
    }

    public String getIban() {
        return iban;
    }
}
