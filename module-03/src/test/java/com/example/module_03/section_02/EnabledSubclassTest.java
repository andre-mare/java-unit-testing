package com.example.module_03.section_02;

import static com.example.module_03.support.SamplePasswords.NO_SYMBOL;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.password.Violation;

import org.junit.jupiter.api.Test;

/**
 * Module 3.2 – extends a {@code @Disabled} class without repeating the annotation.
 * <p>
 * Runs 2 tests: the inherited {@code rejectsPasswordWithWhitespace()} and its own one.
 */
class EnabledSubclassTest extends DisabledBaseTest {

    @Test
    void rejectsPasswordWithoutSymbol() {
        assertTrue(policy.validate(NO_SYMBOL).has(Violation.MISSING_SYMBOL));
    }
}
