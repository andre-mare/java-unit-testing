package com.example.module_03.section_02;

import static com.example.module_03.support.SamplePasswords.WITH_SPACE;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.password.PasswordPolicy;
import com.example.password.Violation;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Module 3.2 – {@code @Disabled} is NOT inherited.
 * <p>
 * This concrete class is disabled, so it shows as skipped. Its subclasses:
 * <ul>
 *   <li>{@link EnabledSubclassTest} does not repeat {@code @Disabled}: the
 *       inherited test runs there.</li>
 *   <li>{@link StillDisabledSubclassTest} redeclares {@code @Disabled}: skipped too.</li>
 * </ul>
 * Expected result: this class skipped, EnabledSubclassTest passed with 2 tests,
 * StillDisabledSubclassTest skipped.
 */
@Disabled("Base suite: disabled here on purpose, to show @Disabled is not inherited")
class DisabledBaseTest {

    protected final PasswordPolicy policy = PasswordPolicy.standard();

    @Test
    void rejectsPasswordWithWhitespace() {
        assertTrue(policy.validate(WITH_SPACE).has(Violation.CONTAINS_WHITESPACE));
    }
}
