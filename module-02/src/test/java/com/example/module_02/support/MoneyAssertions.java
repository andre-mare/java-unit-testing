package com.example.module_02.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

/**
 * Module 2.1 lab deliverable – a custom assertion for money.
 * <p>
 * {@code assertEquals(new BigDecimal("10.0"), new BigDecimal("10.00"))} FAILS,
 * because {@link BigDecimal#equals} compares value AND scale. Money should be
 * compared by value only, which is what {@link BigDecimal#compareTo} does.
 * Wrapping that in a named assertion keeps tests readable and the failure
 * message clear.
 */
public final class MoneyAssertions {

    private MoneyAssertions() {
    }

    /** Asserts that two amounts are numerically equal, ignoring scale. */
    public static void assertAmountEquals(String expected, BigDecimal actual) {
        assertAmountEquals(new BigDecimal(expected), actual);
    }

    /** Asserts that two amounts are numerically equal, ignoring scale. */
    public static void assertAmountEquals(BigDecimal expected, BigDecimal actual) {
        assertNotNull(actual, "amount must not be null");
        // Compare the plain-string forms after aligning scale, so the failure
        // message shows "expected: <150.00> but was: <149.99>".
        int scale = Math.max(expected.scale(), actual.scale());
        if (expected.compareTo(actual) != 0) {
            assertEquals(expected.setScale(scale).toPlainString(), actual.setScale(scale).toPlainString(),
                "amounts differ");
        }
    }
}
