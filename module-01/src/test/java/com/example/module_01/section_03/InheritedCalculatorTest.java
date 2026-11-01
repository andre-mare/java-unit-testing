package com.example.module_01.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Module 1.3 – a concrete test class that inherits from {@link AbstractCalculatorTest}.
 * <p>
 * Expected result tree: three tests
 * <ul>
 *   <li>{@code inheritedAdditionTest()} – from the superclass</li>
 *   <li>{@code overriddenTest()} – the subclass version only</li>
 *   <li>{@code primeCheck()} – declared here</li>
 * </ul>
 * The superclass {@code @BeforeEach} runs before each of them.
 */
class InheritedCalculatorTest extends AbstractCalculatorTest {

    @Override
    @Test
    void overriddenTest() {
        assertEquals(-5, calculator.subtract(0, 5));
    }

    @Test
    void primeCheck() {
        assertTrue(calculator.isPrime(13));
    }
}
