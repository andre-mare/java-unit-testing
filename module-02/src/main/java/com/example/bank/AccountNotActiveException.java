package com.example.bank;

/** Thrown when money is moved on an account that is FROZEN or CLOSED. */
public class AccountNotActiveException extends AccountException {

    private final AccountStatus status;

    public AccountNotActiveException(String iban, AccountStatus status) {
        super(iban, "Account " + iban + " is " + status);
        this.status = status;
    }

    public AccountStatus getStatus() {
        return status;
    }
}
