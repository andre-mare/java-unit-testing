package com.example.module_01.section_01;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.calculator.Calculator;

import org.junit.jupiter.api.Test;


/**
 * Module 1.1 – your very first test class.
 * <p>
 * Rules shown here:
 * <ul>
 *   <li>The class is not {@code public}, not {@code abstract}, and has one (implicit) constructor.</li>
 *   <li>Test methods are not {@code private}, not {@code static}, and return {@code void}.</li>
 *   <li>Each test follows Arrange – Act – Assert.</li>
 * </ul>
 */
class CalculatorFirstTest {

    private final Calculator calculator = new Calculator();

    @Test
    void addsTwoPositiveNumbers() {
        // Arrange
        int a = 2;
        int b = 3;

        // Act
        int result = calculator.add(a, b);

        // Assert (expected value first, actual value second)
        assertEquals(5, result);
    }

    @Test
    void subtractsToANegativeNumber() {
        assertEquals(-1, calculator.subtract(2, 3));
    }

    @Test
    void multipliesByZero() {
        assertEquals(0, calculator.multiply(42, 0));
    }

    @Test
    void integerDivisionTruncates() {
        assertEquals(2, calculator.divide(7, 3), "integer division drops the remainder");
    }
}
