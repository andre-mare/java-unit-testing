package com.example.module_07.section_05;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.example.iban.Iban;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Module 7.5 – fallback {@code String}-to-object conversion.
 * <p>
 * For a type without a built-in converter, JUnit looks in the target type for
 * <ol>
 *   <li>exactly one non-private static <em>factory method</em> taking one
 *       {@code String} and returning the type (any name), or else</li>
 *   <li>exactly one non-private <em>constructor</em> taking one {@code String}.</li>
 * </ol>
 * If it finds several factory methods it ignores them all. {@link Iban} has exactly
 * one ({@code parse}), so the CSV text below becomes {@code Iban} objects.
 * {@code Amount} has two ({@code parse} and {@code euro}): it needs an explicit
 * converter, see {@link ExplicitConversionTest}.
 */
class FallbackConversionTest {

    @ParameterizedTest
    @ValueSource(strings = { "NL91ABNA0417164300", "nl91 abna 0417 1643 00" })
    void textBecomesAnIban(Iban iban) {
        assertEquals("NL91 ABNA 0417 1643 00", iban.formatted());
    }

    @ParameterizedTest
    @CsvSource({
        "NL91ABNA0417164300, DE89370400440532013000",
        "BE68539007547034,   GB29NWBK60161331926819"
    })
    void twoIbansPerRow(Iban debtor, Iban creditor) {
        assertNotEquals(debtor, creditor);
        assertNotEquals(debtor.countryCode(), creditor.countryCode());
    }
}
