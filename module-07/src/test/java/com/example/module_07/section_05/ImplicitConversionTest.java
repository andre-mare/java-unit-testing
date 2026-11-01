package com.example.module_07.section_05;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;

import com.example.payments.Amount;
import com.example.payments.PaymentScheme;
import com.example.tokens.TokenValidator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Module 7.5 – implicit conversion: JUnit turns source values into parameter types.
 * <ul>
 *   <li>Widening: an {@code int} literal fits a {@code long} or {@code double} parameter.</li>
 *   <li>From {@code String} to: primitives and wrappers (also hex and octal integers,
 *       such as {@code 0x1F}), enums by name, {@code java.time} types in ISO-8601 form,
 *       {@code BigDecimal}, {@code BigInteger}, {@code Currency}, {@code Locale}
 *       (an IETF tag such as {@code nl-NL}), {@code UUID}, {@code ZoneId}, {@code Class},
 *       {@code File}, {@code Path}, {@code URI}, {@code URL}, {@code Charset} and more.</li>
 * </ul>
 * The parameters below are typed; the values are plain text.
 */
class ImplicitConversionTest {

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3 })
    void wideningToLong(long hours) {
        assertTrue(hours > 0);
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3 })
    void wideningToDouble(double hours) {
        assertEquals(Math.floor(hours), hours);
    }

    @ParameterizedTest
    @CsvSource({
        "SEPA_INSTANT, 2026-03-01, PT30S, 2026-03-01T10:00:00Z, 12.50, EUR"
    })
    void textToTypes(PaymentScheme scheme, LocalDate date, Duration skew, Instant now,
            BigDecimal value, Currency currency) {
        assertTrue(scheme.isInstant());
        assertEquals(2026, date.getYear());
        assertEquals(30, skew.toSeconds());
        assertEquals(date, LocalDate.ofInstant(now, ZoneId.of("UTC")));
        assertTrue(scheme.allows(new Amount(value, currency)));
    }

    @ParameterizedTest
    @CsvSource({ "nl-NL, Europe/Amsterdam, 2026-03, 0x1F, 3f2504e0-4f89-11d3-9a0c-0305e82c3301" })
    void moreTextToTypes(Locale locale, ZoneId zone, YearMonth month, int hex, UUID id) {
        assertEquals("NL", locale.getCountry());
        assertEquals("Europe/Amsterdam", zone.getId());
        assertEquals(31, month.lengthOfMonth());
        assertEquals(31, hex);
        assertEquals(1, id.version());
    }

    @ParameterizedTest
    @ValueSource(strings = { "com.example.tokens.TokenValidator" })
    void textToClass(Class<?> type) {
        assertEquals(TokenValidator.class, type);
    }
}
