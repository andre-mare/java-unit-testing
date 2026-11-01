package com.example.payments;

import java.util.Optional;

/**
 * Ways to move money, each with its own rules. The rules are simplified for
 * this course and are not a statement of real scheme limits.
 */
public enum PaymentScheme {

    SEPA_CREDIT_TRANSFER(true, false, null),
    SEPA_INSTANT(true, true, Amount.euro("100000.00")),
    SEPA_DIRECT_DEBIT(true, false, null),
    SWIFT_WIRE(false, false, null);

    private final boolean sepa;
    private final boolean instant;
    private final Amount maximum;

    PaymentScheme(boolean sepa, boolean instant, Amount maximum) {
        this.sepa = sepa;
        this.instant = instant;
        this.maximum = maximum;
    }

    /** SEPA schemes only move euros. */
    public boolean isSepa() {
        return sepa;
    }

    /** Settles within seconds. */
    public boolean isInstant() {
        return instant;
    }

    public Optional<Amount> maximum() {
        return Optional.ofNullable(maximum);
    }

    /** True if the scheme can carry this amount (currency and maximum). */
    public boolean allows(Amount amount) {
        if (sepa && !amount.currency().getCurrencyCode().equals("EUR")) {
            return false;
        }
        return maximum == null || !amount.isMoreThan(maximum);
    }
}
