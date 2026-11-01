package com.example.module_07.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.iban.Iban;
import com.example.iban.IbanValidator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Module 7.3 – {@code @ArgumentsSource} with a custom provider, and combining sources.
 * <p>
 * Sources can be combined on one method: different kinds (here {@code @ValueSource}
 * plus {@code @NullSource}), and the same kind repeated. Repeatable sources:
 * {@code @ValueSource}, {@code @EnumSource}, {@code @MethodSource}, {@code @FieldSource},
 * {@code @CsvSource}, {@code @CsvFileSource}, {@code @ArgumentsSource}.
 * Invocations run in the order the sources are declared.
 */
class ArgumentsSourceTest {

    @ParameterizedTest
    @ArgumentsSource(RandomIbansProvider.class)
    void customProvider(Iban iban) {
        assertTrue(IbanValidator.isValid(iban.toString()));
        assertEquals(18, iban.toString().length());
    }

    @ParameterizedTest
    @CsvSource({ "NL91ABNA0417164300, NL" })
    @CsvSource({ "DE89370400440532013000, DE", "BE68539007547034, BE" })
    void repeatedCsvSource(String iban, String country) {
        assertEquals(country, Iban.parse(iban).countryCode());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "XX00", "NL91ABNA0417164301" })
    void differentSourcesCombined(String candidate) {
        assertTrue(!IbanValidator.isValid(candidate));
    }
}
