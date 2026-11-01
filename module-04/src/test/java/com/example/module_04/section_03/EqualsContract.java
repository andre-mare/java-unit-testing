package com.example.module_04.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Module 4.3 – a contract test for {@link Object#equals} and {@link Object#hashCode}.
 * <p>
 * Implement this interface in a test class, supply the values, and the five
 * {@code default} test methods run as if you had written them in that class.
 * Write the rules once, check every value type with them.
 *
 * @param <T> the value type
 */
interface EqualsContract<T> extends ValueFactory<T> {

    /** A different object that must be equal to {@link #createValue()}. */
    T createEqualValue();

    /** A value that must NOT be equal to {@link #createValue()}. */
    T createNotEqualValue();

    @Test
    default void valueEqualsItself() {
        T value = createValue();
        assertEquals(value, value);
    }

    @Test
    default void valueDoesNotEqualNull() {
        assertNotEquals(null, createValue());
    }

    @Test
    default void equalValuesAreEqualBothWays() {
        T value = createValue();
        T equal = createEqualValue();
        assertEquals(value, equal);
        assertEquals(equal, value);
    }

    @Test
    default void equalValuesHaveTheSameHashCode() {
        assertEquals(createValue().hashCode(), createEqualValue().hashCode());
    }

    @Test
    default void valueDoesNotEqualDifferentValue() {
        T value = createValue();
        T different = createNotEqualValue();
        assertNotEquals(value, different);
        assertNotEquals(different, value);
    }
}
