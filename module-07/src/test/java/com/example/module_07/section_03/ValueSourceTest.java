package com.example.module_07.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import com.example.iban.Iban;
import com.example.iban.IbanValidator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Module 7.3 – {@code @ValueSource}: an array of literals, one argument per invocation.
 * <p>
 * Supported literal types: {@code shorts}, {@code bytes}, {@code ints}, {@code longs},
 * {@code floats}, {@code doubles}, {@code chars}, {@code booleans}, {@code strings}
 * and {@code classes}. Use it when the method takes exactly one argument.
 */
class ValueSourceTest {

    @ParameterizedTest
    @ValueSource(strings = { "NL91ABNA0417164300", "nl91abna0417164300", "NL91 ABNA 0417 1643 00" })
    void strings(String spelling) {
        assertEquals("NL91ABNA0417164300", Iban.parse(spelling).toString());
    }

    @ParameterizedTest
    @ValueSource(ints = { 16, 18, 20, 21, 22, 24, 27 })
    void ints(int length) {
        assertTrue(IbanValidator.LENGTHS.containsValue(length));
    }

    @ParameterizedTest
    @ValueSource(chars = { '!', '#', '-', '.' })
    void chars(char bad) {
        assertFalse(IbanValidator.isValid("NL91ABNA041716430" + bad));
    }

    @ParameterizedTest
    @ValueSource(classes = { Iban.class, IbanValidator.class })
    void classes(Class<?> type) {
        assertTrue(Modifier.isFinal(type.getModifiers()), type + " should be final");
    }
}
