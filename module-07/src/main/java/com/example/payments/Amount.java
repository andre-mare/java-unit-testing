package com.example.payments;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * A non-negative amount of money in one currency, Module 7 production code.
 * <p>
 * {@link #parse(String)} reads the form {@code "EUR 12.50"}. Note for Module 7.5:
 * there are <em>two</em> static {@code String}-to-{@code Amount} methods
 * ({@code parse} and {@code euro}), so JUnit's fallback conversion cannot pick one
 * and tests need an explicit converter. {@link com.example.iban.Iban} has only one.
 *
 * @param value    the amount, at the currency's number of decimals
 * @param currency the ISO 4217 currency
 */
public record Amount(BigDecimal value, Currency currency) {

    public Amount {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(currency, "currency");
        if (value.signum() < 0) {
            throw new IllegalArgumentException("Amount must not be negative: " + value.toPlainString());
        }
        try {
            value = value.setScale(currency.getDefaultFractionDigits(), RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Too many decimals for " + currency + ": " + value.toPlainString(), e);
        }
    }

    /** Parses "EUR 12.50" (currency code, one space, amount). */
    public static Amount parse(String text) {
        Objects.requireNonNull(text, "text");
        String[] parts = text.trim().split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Expected '<currency> <amount>', got: " + text);
        }
        return new Amount(new BigDecimal(parts[1]), Currency.getInstance(parts[0]));
    }

    public static Amount euro(String value) {
        return new Amount(new BigDecimal(value), Currency.getInstance("EUR"));
    }

    public boolean isZero() {
        return value.signum() == 0;
    }

    public boolean isMoreThan(Amount other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch: " + this + " vs " + other);
        }
        return value.compareTo(other.value) > 0;
    }

    /** "EUR 12.50". */
    @Override
    public String toString() {
        return currency.getCurrencyCode() + " " + value.toPlainString();
    }
}
