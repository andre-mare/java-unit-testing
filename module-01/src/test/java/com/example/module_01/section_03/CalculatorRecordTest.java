package com.example.module_01.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.calculator.Calculator;

import org.junit.jupiter.api.Test;

/**
 * Module 1.3 – a Java {@code record} used as a test class.
 * <p>
 * A record has exactly one canonical constructor, so it satisfies the
 * "single constructor" rule for test classes. With no components it is simply
 * a compact, immutable test class.
 */
record CalculatorRecordTest() {

    @Test
    void addition() {
        assertEquals(2, new Calculator().add(1, 1));
    }

    @Test
    void factorialOfFive() {
        assertEquals(120L, new Calculator().factorial(5));
    }
}
