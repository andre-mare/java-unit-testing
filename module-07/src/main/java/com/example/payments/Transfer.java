package com.example.payments;

import java.time.LocalDate;
import java.util.Objects;

import com.example.iban.Iban;

/**
 * A payment order from one account to another, validated on creation.
 * <p>
 * Six fields make it a good subject for argument aggregation (Module 7.6):
 * one CSV row becomes one {@code Transfer}.
 *
 * @param debtor        the account that pays
 * @param creditor      the account that receives
 * @param amount        more than zero
 * @param executionDate the requested execution date
 * @param scheme        must allow the amount
 * @param reference     free text for the receiver, at most 140 characters; may be empty
 */
public record Transfer(Iban debtor, Iban creditor, Amount amount, LocalDate executionDate,
        PaymentScheme scheme, String reference) {

    public static final int MAX_REFERENCE_LENGTH = 140;

    public Transfer {
        Objects.requireNonNull(debtor, "debtor");
        Objects.requireNonNull(creditor, "creditor");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(executionDate, "executionDate");
        Objects.requireNonNull(scheme, "scheme");
        reference = reference == null ? "" : reference;
        if (debtor.equals(creditor)) {
            throw new IllegalArgumentException("Debtor and creditor must differ");
        }
        if (amount.isZero()) {
            throw new IllegalArgumentException("Amount must be more than zero");
        }
        if (!scheme.allows(amount)) {
            throw new IllegalArgumentException(scheme + " does not allow " + amount);
        }
        if (reference.length() > MAX_REFERENCE_LENGTH) {
            throw new IllegalArgumentException("Reference longer than " + MAX_REFERENCE_LENGTH + " characters");
        }
    }

    /** Debtor and creditor in different countries. */
    public boolean isCrossBorder() {
        return !debtor.countryCode().equals(creditor.countryCode());
    }
}
