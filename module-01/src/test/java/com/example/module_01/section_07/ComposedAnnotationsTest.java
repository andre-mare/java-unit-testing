package com.example.module_01.section_07;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.calculator.Calculator;
import com.example.module_01.section_07.annotation.Fast;
import com.example.module_01.section_07.annotation.FastTest;
import com.example.module_01.section_07.annotation.UnitTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Module 1.7 – meta-annotations and composed annotations.
 * <p>
 * The tests read their own tags via {@link TestInfo} (Module 6 covers
 * injection properly) to prove the composed annotations really apply the tags.
 * <p>
 * Try filtering:
 * <pre>
 * mvn test -Dgroups=fast   // runs only the two "fast" tests here
 * mvn test -Dgroups=unit   // runs only the @UnitTest test
 * </pre>
 */
class ComposedAnnotationsTest {

    private final Calculator calculator = new Calculator();

    @Fast
    @Test
    void fastTagPlusTest(TestInfo info) {
        assertTrue(info.getTags().contains("fast"));
        assertEquals(4, calculator.add(2, 2));
    }

    @FastTest // no @Test needed: @FastTest already carries it
    void composedFastTest(TestInfo info) {
        assertTrue(info.getTags().contains("fast"));
        assertEquals(1, calculator.divide(3, 3));
    }

    @UnitTest
    void courseWideUnitTest(TestInfo info) {
        assertTrue(info.getTags().contains("unit"));
        assertFalse(calculator.isPrime(1));
    }
}
