package com.example.module_03.section_01;

import static com.example.module_03.support.SamplePasswords.EXACTLY_12;
import static com.example.module_03.support.SamplePasswords.EXACTLY_128;
import static com.example.module_03.support.SamplePasswords.VALID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.IndicativeSentencesGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Module 3.1 – generator 4 of 4: {@code IndicativeSentences}, via
 * {@code @IndicativeSentencesGeneration}.
 * <p>
 * Each test name becomes a full sentence: the class name, a separator, then the
 * method name. Here the separator is {@code " -> "} and the underlying generator
 * is {@code ReplaceUnderscores}, so:
 * <pre>
 * A valid password
 * ├─ A valid password -> has at least 12 characters
 * ├─ A valid password -> has at most 128 characters
 * └─ A valid password -> mixes all four character types
 * </pre>
 * The full sentence is useful in flat reports (CI e-mails, Surefire XML) where
 * the class name is not shown next to the test. With {@code @Nested} classes
 * (Module 4.2) the sentence grows with every nesting level.
 */
@IndicativeSentencesGeneration(separator = " -> ", generator = ReplaceUnderscores.class)
class A_valid_password extends DisplayNameTestSupport {

    @BeforeAll
    static void className(TestInfo classInfo) {
        assertEquals("A valid password", classInfo.getDisplayName());
    }

    @Test
    void has_at_least_12_characters() {
        assertDisplayName("A valid password -> has at least 12 characters");
        assertTrue(policy.validate(EXACTLY_12).isValid());
    }

    @Test
    void has_at_most_128_characters() {
        assertDisplayName("A valid password -> has at most 128 characters");
        assertTrue(policy.validate(EXACTLY_128).isValid());
    }

    @Test
    void mixes_all_four_character_types() {
        assertDisplayName("A valid password -> mixes all four character types");
        assertTrue(policy.validate(VALID).isValid());
    }
}
