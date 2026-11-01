package com.example.fx;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Converts amounts between currencies with fixed reference rates, Module 8 production code.
 * <p>
 * Every rate says how many units of a currency one euro buys. Results are rounded
 * half-even ("banker's rounding") to the target currency's number of decimals:
 * two for EUR, USD and GBP, none for JPY. The rates are made up for the course.
 */
public final class CurrencyConverter {

    private static final MathContext PRECISION = MathContext.DECIMAL64;

    private final Map<Currency, BigDecimal> unitsPerEuro;

    public CurrencyConverter(Map<Currency, BigDecimal> unitsPerEuro) {
        this.unitsPerEuro = Map.copyOf(unitsPerEuro);
        if (!this.unitsPerEuro.containsKey(euro())) {
            throw new IllegalArgumentException("Rates must include EUR");
        }
        this.unitsPerEuro.forEach((currency, rate) -> {
            if (rate.signum() <= 0) {
                throw new IllegalArgumentException("Rate for " + currency + " must be positive: " + rate);
            }
        });
    }

    /** A converter with the course's reference rates for EUR, USD, GBP, CHF and JPY. */
    public static CurrencyConverter withReferenceRates() {
        return new CurrencyConverter(Map.of(
            euro(), BigDecimal.ONE,
            Currency.getInstance("USD"), new BigDecimal("1.0850"),
            Currency.getInstance("GBP"), new BigDecimal("0.8550"),
            Currency.getInstance("CHF"), new BigDecimal("0.9400"),
            Currency.getInstance("JPY"), new BigDecimal("162.50")));
    }

    public Set<Currency> supportedCurrencies() {
        return unitsPerEuro.keySet();
    }

    /**
     * Converts a non-negative amount from one currency to another.
     *
     * @throws IllegalArgumentException for a negative amount or an unsupported currency
     */
    public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        Objects.requireNonNull(amount, "amount");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Amount must not be negative: " + amount.toPlainString());
        }
        BigDecimal inEuro = amount.divide(rate(from), PRECISION);
        return round(inEuro.multiply(rate(to), PRECISION), to);
    }

    /** Rounds to the currency's number of decimals, half-even. */
    public static BigDecimal round(BigDecimal amount, Currency currency) {
        return amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_EVEN);
    }

    /** The smallest unit of a currency: 0.01 for EUR, 1 for JPY. */
    public static BigDecimal minorUnit(Currency currency) {
        return BigDecimal.ONE.movePointLeft(currency.getDefaultFractionDigits());
    }

    private BigDecimal rate(Currency currency) {
        BigDecimal rate = unitsPerEuro.get(Objects.requireNonNull(currency, "currency"));
        if (rate == null) {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
        return rate;
    }

    private static Currency euro() {
        return Currency.getInstance("EUR");
    }
}
