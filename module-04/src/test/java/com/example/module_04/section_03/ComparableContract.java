package com.example.module_04.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Module 4.3 – a contract test for {@link Comparable#compareTo}.
 *
 * @param <T> the comparable value type
 */
interface ComparableContract<T extends Comparable<T>> extends ValueFactory<T> {

    /** A value that must sort before {@link #createValue()}. */
    T createSmallerValue();

    @Test
    default void returnsZeroWhenComparedToItself() {
        T value = createValue();
        assertEquals(0, value.compareTo(value));
    }

    @Test
    default void returnsPositiveNumberWhenComparedToSmallerValue() {
        assertTrue(createValue().compareTo(createSmallerValue()) > 0);
    }

    @Test
    default void returnsNegativeNumberWhenComparedToLargerValue() {
        assertTrue(createSmallerValue().compareTo(createValue()) < 0);
    }
}
