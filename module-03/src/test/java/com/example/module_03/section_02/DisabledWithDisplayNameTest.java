package com.example.module_03.section_02;

import static com.example.module_03.support.SamplePasswords.VALID;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.password.PasswordPolicy;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

/**
 * Module 3.2 – disabled tests keep their display names.
 * <p>
 * Combines both submodules: a skipped test still appears in the report under
 * its readable name, with the reason next to it. Expected report:
 * <pre>
 * Breach checks
 * ├─ blocks passwords from known breaches   ↷ SEC-142: breach check is not implemented yet
 * └─ still validates the format first       ✔
 * </pre>
 */
@DisplayName("Breach checks")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DisabledWithDisplayNameTest {

    private final PasswordPolicy policy = PasswordPolicy.standard();

    @Test
    @Disabled("SEC-142: breach check is not implemented yet")
    void blocks_passwords_from_known_breaches() {
        assertTrue(policy.isKnownBreached("Summer2026!!"));
    }

    @Test
    void still_validates_the_format_first() {
        assertTrue(policy.validate(VALID).isValid());
    }
}
