package com.example.module_01.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.calculator.Calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Module 1.3 – inheritance of fields, lifecycle methods and test methods.
 * <p>
 * This class is {@code abstract}, so JUnit never runs it directly (test classes
 * must not be abstract). Its {@code @BeforeEach} and {@code @Test} methods are
 * inherited by {@link InheritedCalculatorTest} and run there.
 */
abstract class AbstractCalculatorTest {

    protected Calculator calculator;

    @BeforeEach
    void createCalculator() {
        calculator = new Calculator();
    }

    @Test
    void inheritedAdditionTest() {
        assertEquals(3, calculator.add(1, 2));
    }

    /** Overridden in the subclass: only the subclass version runs. */
    @Test
    void overriddenTest() {
        assertEquals(0, calculator.subtract(1, 1));
    }
}
