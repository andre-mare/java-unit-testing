package com.example.module_03.section_01;

import static com.example.module_03.support.SamplePasswords.NO_DIGIT;
import static com.example.module_03.support.SamplePasswords.TOO_SHORT;
import static com.example.module_03.support.SamplePasswords.VALID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.password.Violation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Module 3.1 – {@code @DisplayName} on a class and on methods.
 * <p>
 * Start here. The display name replaces the class or method name in the IDE,
 * in Maven output and in test reports. It can contain spaces, punctuation,
 * other scripts and emoji. The method name no longer matters to the reader,
 * so you can keep it short.
 */
@DisplayName("Standard password policy")
class DisplayNameAnnotationTest extends DisplayNameTestSupport {

    @BeforeAll
    static void classHasItsOwnDisplayName(TestInfo classInfo) {
        // In @BeforeAll, TestInfo describes the test class.
        assertEquals("Standard password policy", classInfo.getDisplayName());
    }

    @Test
    @DisplayName("accepts a 23-character passphrase with all four character types")
    void acceptsPassphrase() {
        assertDisplayName("accepts a 23-character passphrase with all four character types");
        assertTrue(policy.validate(VALID).isValid());
    }

    @Test
    @DisplayName("rejects \"Ab1!\" – too short (minimum: 12) ✗")
    void rejectsShort() {
        assertDisplayName("rejects \"Ab1!\" – too short (minimum: 12) ✗");
        assertTrue(policy.validate(TOO_SHORT).has(Violation.TOO_SHORT));
    }

    @Test
    @DisplayName("wachtwoord zonder cijfer wordt geweigerd")
    void anyLanguage() {
        assertDisplayName("wachtwoord zonder cijfer wordt geweigerd");
        assertFalse(policy.validate(NO_DIGIT).isValid());
    }

    @Test
    @DisplayName("🔐")
    void emoji() {
        assertDisplayName("🔐");
        // The emoji is the only non-letter, non-digit character: it satisfies the symbol rule.
        assertTrue(policy.validate("🔐CorrectHorse9").isValid(), "an emoji counts as a symbol");
    }
}
