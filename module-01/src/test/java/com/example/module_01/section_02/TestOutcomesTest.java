package com.example.module_01.section_02;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.example.calculator.Calculator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Module 1.2 – the four possible test outcomes, side by side.
 * <ul>
 *   <li><b>Succeeded</b> – no exception thrown.</li>
 *   <li><b>Failed</b> – an assertion failed or an unexpected exception was thrown.</li>
 *   <li><b>Skipped</b> – the test never ran ({@code @Disabled}, or a condition in Module 5).</li>
 *   <li><b>Aborted</b> – an assumption was not met; the test started but stopped early.</li>
 * </ul>
 * This class FAILS on purpose, so it is tagged {@code expected-failure-tests} and excluded
 * from the normal build (see the Surefire {@code excludedGroups} in the pom).
 * Run it on its own with:
 * <pre>mvn test -Dtest=TestOutcomesTest -DexcludedGroups=none</pre>
 * or from the IDE, and compare the four icons in the result tree.
 */
@Tag("expected-failure-tests")
class TestOutcomesTest {

    private final Calculator calculator = new Calculator();

    @Test
    void succeedingTest() {
        assertEquals(10, calculator.add(4, 6));
    }

    @Test
    void failingTest() {
        // Wrong expectation on purpose: read the "expected: <5> but was: <4>" message.
        assertEquals(5, calculator.add(2, 2));
    }

    @Test
    void failingWithFail() {
        fail("a failing test – fail() always fails");
    }

    @Test
    void failingWithUnexpectedException() {
        // No assertion at all: the uncaught ArithmeticException alone fails the test.
        calculator.divide(1, 0);
    }

    @Test
    @Disabled("for demonstration purposes – always give a reason, ideally a ticket number")
    void skippedTest() {
        fail("never executed");
    }

    @Test
    void abortedTest() {
        assumeTrue("abc".contains("Z"), "assumption not met, so the test is aborted");
        fail("test should have been aborted before reaching this line");
    }
}
