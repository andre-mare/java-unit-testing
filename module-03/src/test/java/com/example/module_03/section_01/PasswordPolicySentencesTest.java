package com.example.module_03.section_01;

import static com.example.module_03.support.SamplePasswords.LETTERS_ONLY;
import static com.example.module_03.support.SamplePasswords.VALID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;

import com.example.password.Violation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.IndicativeSentencesGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.IndicativeSentences.SentenceFragment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Module 3.1 – {@code @SentenceFragment}: indicative sentences with normal Java names.
 * <p>
 * Instead of underscore names, each class and method declares its own piece of
 * the sentence. The default separator is {@code ", "}:
 * <pre>
 * The standard policy
 * ├─ The standard policy, accepts a mixed passphrase
 * └─ The standard policy, reports every broken rule at once
 * </pre>
 * Compare with {@link A_valid_password}: same output style, conventional names.
 */
@SentenceFragment("The standard policy")
@IndicativeSentencesGeneration
class PasswordPolicySentencesTest extends DisplayNameTestSupport {

    @BeforeAll
    static void className(TestInfo classInfo) {
        assertEquals("The standard policy", classInfo.getDisplayName());
    }

    @Test
    @SentenceFragment("accepts a mixed passphrase")
    void acceptsPassphrase() {
        assertDisplayName("The standard policy, accepts a mixed passphrase");
        assertTrue(policy.validate(VALID).isValid());
    }

    @Test
    @SentenceFragment("reports every broken rule at once")
    void reportsAllViolations() {
        assertDisplayName("The standard policy, reports every broken rule at once");
        assertEquals(
            EnumSet.of(Violation.TOO_SHORT, Violation.MISSING_UPPERCASE, Violation.MISSING_DIGIT,
                Violation.MISSING_SYMBOL),
            policy.validate(LETTERS_ONLY).violations());
    }
}
