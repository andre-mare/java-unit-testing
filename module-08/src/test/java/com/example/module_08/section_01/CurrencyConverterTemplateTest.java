package com.example.module_08.section_01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Currency;

import com.example.fx.CurrencyConverter;

import org.junit.jupiter.api.Test;

/**
 * Module 8.1 lab – every conversion rule, checked once per currency.
 * <p>
 * The same properties must hold whatever the currency, so each test is a
 * {@code @ForEachCurrency} template: one test method, three (or more) results in
 * the report, each named after its currency.
 */
class CurrencyConverterTemplateTest {

    private static final Currency EUR = Currency.getInstance("EUR");

    private final CurrencyConverter converter = CurrencyConverter.withReferenceRates();

    @ForEachCurrency
    void sameCurrencyChangesNothing(Currency currency) {
        assertEquals(new BigDecimal("123.45"), converter.convert(new BigDecimal("123.45"), currency, currency));
    }

    @ForEachCurrency
    void zeroStaysZero(Currency currency) {
        assertThat(converter.convert(BigDecimal.ZERO, EUR, currency)).isZero();
    }

    @ForEachCurrency({ "EUR", "USD", "GBP", "CHF", "JPY" })
    void resultHasTheDecimalsOfTheTargetCurrency(Currency currency) {
        BigDecimal result = converter.convert(new BigDecimal("10.00"), EUR, currency);
        assertEquals(currency.getDefaultFractionDigits(), result.scale());
    }

    @ForEachCurrency
    void euroRoundTripIsWithinOneCent(Currency currency) {
        BigDecimal start = new BigDecimal("100.00");
        BigDecimal back = converter.convert(converter.convert(start, EUR, currency), currency, EUR);
        assertThat(back.subtract(start).abs()).isLessThanOrEqualTo(CurrencyConverter.minorUnit(EUR));
    }

    @ForEachCurrency
    void negativeAmountsAreRejected(Currency currency) {
        assertThrows(IllegalArgumentException.class, () -> converter.convert(new BigDecimal("-1"), EUR, currency));
    }

    @ForEachCurrency({ "SEK", "NOK" })
    void unsupportedCurrenciesAreRejected(Currency currency) {
        assertThrows(IllegalArgumentException.class, () -> converter.convert(BigDecimal.ONE, EUR, currency));
    }

    @Test
    void yenHasNoDecimals() {
        assertEquals(new BigDecimal("1625"), converter.convert(BigDecimal.TEN, EUR, Currency.getInstance("JPY")));
    }

    @Test
    void halfEvenRounding() {
        Currency usd = Currency.getInstance("USD");
        assertEquals(new BigDecimal("0.12"), CurrencyConverter.round(new BigDecimal("0.125"), usd));
        assertEquals(new BigDecimal("0.14"), CurrencyConverter.round(new BigDecimal("0.135"), usd));
    }
}
