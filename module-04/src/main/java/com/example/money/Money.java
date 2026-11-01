package com.example.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * An amount in a currency – a value object, Module 4 production code.
 * <p>
 * The amount is stored at the currency's own number of decimals (EUR 2, JPY 0),
 * so {@code Money.of("10", "EUR")} equals {@code Money.of("10.00", "EUR")}.
 * More decimals than the currency allows is rejected, never silently rounded.
 * <p>
 * Ordering only makes sense within one currency: comparing EUR with USD throws
 * an {@link IllegalArgumentException}.
 *
 * @param amount   the amount, normalised to the currency's scale
 * @param currency the ISO 4217 currency
 */
public record Money(BigDecimal amount, Currency currency) implements Comparable<Money> {

    public Money {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        try {
            amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(
                currency.getCurrencyCode() + " allows " + currency.getDefaultFractionDigits()
                    + " decimals, got " + amount.toPlainString(), e);
        }
    }

    /** {@code Money.of("12.50", "EUR")}. */
    public static Money of(String amount, String currencyCode) {
        return new Money(new BigDecimal(amount), Currency.getInstance(currencyCode));
    }

    public Money plus(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    @Override
    public int compareTo(Money other) {
        requireSameCurrency(other);
        return amount.compareTo(other.amount);
    }

    /** "EUR 12.50". */
    @Override
    public String toString() {
        return currency.getCurrencyCode() + " " + amount.toPlainString();
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(other, "other");
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch: " + this + " vs " + other);
        }
    }
}
