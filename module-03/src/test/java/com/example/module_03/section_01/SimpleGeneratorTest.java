package com.example.module_03.section_01;

import static com.example.module_03.support.SamplePasswords.NO_DIGIT;
import static com.example.module_03.support.SamplePasswords.VALID;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.password.Violation;

import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

/**
 * Module 3.1 – generator 2 of 4: {@code Simple}.
 * <p>
 * The same as {@code Standard}, but the empty {@code ()} is dropped for methods
 * without parameters. Compare the names with {@link StandardGeneratorTest}.
 */
@DisplayNameGeneration(DisplayNameGenerator.Simple.class)
class SimpleGeneratorTest extends DisplayNameTestSupport {

    @Test
    void acceptsValidPassword() {
        assertDisplayName("acceptsValidPassword");
        assertTrue(policy.validate(VALID).isValid());
    }

    @Test
    void rejectsPasswordWithoutDigit() {
        assertDisplayName("rejectsPasswordWithoutDigit");
        assertTrue(policy.validate(NO_DIGIT).has(Violation.MISSING_DIGIT));
    }
}
