package com.example.module_03.section_01;

import static com.example.module_03.support.SamplePasswords.NO_DIGIT;
import static com.example.module_03.support.SamplePasswords.VALID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.password.Violation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Module 3.1 – generator 1 of 4: {@code Standard}, JUnit's default.
 * <p>
 * Class: the simple class name. Method: the method name plus its parameter
 * list, always with parentheses.
 * <p>
 * {@code @DisplayNameGeneration(Standard.class)} is normally unnecessary. It is
 * here so this class keeps showing Standard names even after you set a different
 * global default in {@link DefaultGeneratorTest}'s exercise.
 */
@DisplayNameGeneration(DisplayNameGenerator.Standard.class)
class StandardGeneratorTest extends DisplayNameTestSupport {

    @BeforeAll
    static void className(TestInfo classInfo) {
        assertEquals("StandardGeneratorTest", classInfo.getDisplayName());
    }

    @Test
    void acceptsValidPassword() {
        assertDisplayName("acceptsValidPassword()");
        assertTrue(policy.validate(VALID).isValid());
    }

    @Test
    void rejectsPasswordWithoutDigit() {
        assertDisplayName("rejectsPasswordWithoutDigit()");
        assertTrue(policy.validate(NO_DIGIT).has(Violation.MISSING_DIGIT));
    }
}
