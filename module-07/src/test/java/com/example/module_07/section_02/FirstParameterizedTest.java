package com.example.module_07.section_02;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.iban.IbanValidator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Module 7.2 – your first parameterized test.
 * <p>
 * A {@code @ParameterizedTest} needs two things: a source of arguments (here
 * {@code @ValueSource}) and parameters that consume them. Each argument becomes
 * one invocation, reported separately:
 * <pre>
 * validIbans(String)
 * ├─ [1] iban = "NL91ABNA0417164300"
 * ├─ [2] iban = "DE89370400440532013000"
 * └─ [3] iban = "BE68539007547034"
 * </pre>
 * The {@code iban =} part needs parameter names in the bytecode; the course pom
 * compiles with {@code -parameters}. Without it you would see {@code arg0 =}.
 * <p>
 * {@code junit-jupiter-params} is already part of the {@code junit-jupiter} dependency.
 * Regular {@code @Test} methods and parameterized tests mix freely in one class.
 */
class FirstParameterizedTest {

    @ParameterizedTest
    @ValueSource(strings = { "NL91ABNA0417164300", "DE89370400440532013000", "BE68539007547034" })
    void validIbans(String iban, TestInfo info) {
        assertTrue(IbanValidator.isValid(iban));
        assertTrue(info.getDisplayName().endsWith("\"" + iban + "\""), info.getDisplayName());
    }

    @ParameterizedTest
    @ValueSource(strings = { "NL91ABNA0417164301", "NL91ABNA041716430", "XX91ABNA0417164300" })
    void invalidIbans(String iban) {
        assertFalse(IbanValidator.isValid(iban));
    }

    @Test
    void anOrdinaryTestInTheSameClass() {
        assertTrue(IbanValidator.isValid("nl91 abna 0417 1643 00"), "spaces and lower case are fine");
    }
}
