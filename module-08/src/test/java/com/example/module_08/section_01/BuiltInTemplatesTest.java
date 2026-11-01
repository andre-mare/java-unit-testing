package com.example.module_08.section_01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Currency;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Module 8.1 – {@code @RepeatedTest} and {@code @ParameterizedTest} are test templates.
 * <p>
 * Both annotations are meta-annotated with {@code @TestTemplate} and register their
 * own provider, just like {@link ForEachCurrency}. The reflection checks prove it;
 * the two templates below are the built-in way to get what 8.1 builds by hand.
 */
class BuiltInTemplatesTest {

    @Test
    void repeatedAndParameterizedTestsAreTemplates() {
        assertTrue(RepeatedTest.class.isAnnotationPresent(TestTemplate.class));
        assertTrue(ParameterizedTest.class.isAnnotationPresent(TestTemplate.class));
        assertTrue(ForEachCurrency.class.isAnnotationPresent(TestTemplate.class));
    }

    @RepeatedTest(3)
    void repeated(RepetitionInfo repetition) {
        assertEquals(3, repetition.getTotalRepetitions());
    }

    @ParameterizedTest
    @ValueSource(strings = { "EUR", "USD", "GBP" })
    void parameterized(Currency currency) {
        assertEquals(2, currency.getDefaultFractionDigits());
    }
}
