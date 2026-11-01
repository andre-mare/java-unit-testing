package com.example.module_07.section_04;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import com.example.iban.Iban;
import com.example.iban.IbanValidator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ArgumentCountValidationMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.FieldSource;

/**
 * Module 7.4 – argument count validation and zero invocations.
 * <p>
 * By default a source may supply <em>more</em> arguments than the method declares:
 * the extra ones are ignored. That hides mistakes, such as a CSV column you forgot
 * to assert. Two ways to make it an error:
 * <ul>
 *   <li>per test: {@code @ParameterizedTest(argumentCountValidation = STRICT)};</li>
 *   <li>for the whole run: {@code junit.jupiter.params.argumentCountValidation=strict}
 *       in {@code junit-platform.properties} (values {@code none}, {@code strict}).</li>
 * </ul>
 * Fewer arguments than declared is always an error, unless the missing parameters
 * are resolved by something else (such as {@code TestInfo}).
 * <p>
 * A parameterized test whose source supplies nothing fails, because that usually
 * means the data is missing. {@code allowZeroInvocations = true} accepts it.
 */
class ArgumentCountValidationTest {

    static final List<String> blockedIbans = List.of(); // nothing blocked today

    private static int zeroInvocationRuns;

    @ParameterizedTest
    @CsvSource({ "NL91ABNA0417164300, NL, this column is silently ignored" })
    void lenientByDefault(String iban, String country) {
        assertEquals(country, Iban.parse(iban).countryCode());
    }

    @ParameterizedTest(argumentCountValidation = ArgumentCountValidationMode.STRICT)
    @CsvSource({ "NL91ABNA0417164300, NL", "BE68539007547034, BE" })
    void strictWithMatchingColumns(String iban, String country) {
        assertEquals(country, Iban.parse(iban).countryCode());
    }

    @ParameterizedTest(allowZeroInvocations = true)
    @FieldSource("blockedIbans")
    void zeroInvocationsAllowed(String iban) {
        zeroInvocationRuns++;
        assertFalse(IbanValidator.isValid(iban));
    }

    @AfterAll
    static void noInvocationsHappened() {
        assertEquals(0, zeroInvocationRuns);
    }
}
