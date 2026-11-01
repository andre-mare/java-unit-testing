package com.example.module_03.section_01;

import static com.example.module_03.support.SamplePasswords.EXACTLY_12;
import static com.example.module_03.support.SamplePasswords.NO_DIGIT;
import static com.example.module_03.support.SamplePasswords.NO_LOWERCASE;
import static com.example.module_03.support.SamplePasswords.TOO_LONG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.password.Violation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Module 3.1 lab – using the custom {@link SentenceCaseDisplayNameGenerator}.
 * <pre>
 * Password policy rules
 * ├─ Rejects password without digit
 * ├─ Rejects password without lowercase
 * ├─ Accepts exactly 12 characters
 * └─ Rejects 129 characters
 * </pre>
 * Normal Java names in the code, sentences in the report, and no annotation per method.
 */
@DisplayNameGeneration(SentenceCaseDisplayNameGenerator.class)
class PasswordPolicyRulesTest extends DisplayNameTestSupport {

    @BeforeAll
    static void className(TestInfo classInfo) {
        assertEquals("Password policy rules", classInfo.getDisplayName());
    }

    @Test
    void rejectsPasswordWithoutDigit() {
        assertDisplayName("Rejects password without digit");
        assertTrue(policy.validate(NO_DIGIT).has(Violation.MISSING_DIGIT));
    }

    @Test
    void rejectsPasswordWithoutLowercase() {
        assertDisplayName("Rejects password without lowercase");
        assertTrue(policy.validate(NO_LOWERCASE).has(Violation.MISSING_LOWERCASE));
    }

    @Test
    void acceptsExactly12Characters() {
        assertDisplayName("Accepts exactly 12 characters");
        assertTrue(policy.validate(EXACTLY_12).isValid());
    }

    @Test
    void rejects129Characters() {
        assertDisplayName("Rejects 129 characters");
        assertTrue(policy.validate(TOO_LONG).has(Violation.TOO_LONG));
    }
}
