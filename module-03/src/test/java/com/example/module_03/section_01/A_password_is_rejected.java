package com.example.module_03.section_01;

import static com.example.module_03.support.SamplePasswords.NO_DIGIT;
import static com.example.module_03.support.SamplePasswords.NO_UPPERCASE;
import static com.example.module_03.support.SamplePasswords.TOO_SHORT;
import static com.example.module_03.support.SamplePasswords.WITH_SPACE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.password.Violation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Module 3.1 – generator 3 of 4: {@code ReplaceUnderscores}.
 * <p>
 * Underscores in class and method names become spaces, and the empty
 * {@code ()} is dropped, so the report reads as a specification:
 * <pre>
 * A password is rejected
 * ├─ when it is shorter than 12 characters
 * ├─ when it has no digit
 * ├─ when it has no uppercase letter
 * └─ when it contains whitespace
 * </pre>
 * The class name breaks Java naming conventions on purpose, and it does not
 * end in {@code Test}. Surefire only runs {@code *Test} classes by default, so
 * the pom adds an include for {@code **}{@code /*_*.java}.
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class A_password_is_rejected extends DisplayNameTestSupport {

    @BeforeAll
    static void classNameReadsAsASentence(TestInfo classInfo) {
        assertEquals("A password is rejected", classInfo.getDisplayName());
    }

    @Test
    void when_it_is_shorter_than_12_characters() {
        assertDisplayName("when it is shorter than 12 characters");
        assertTrue(policy.validate(TOO_SHORT).has(Violation.TOO_SHORT));
    }

    @Test
    void when_it_has_no_digit() {
        assertDisplayName("when it has no digit");
        assertTrue(policy.validate(NO_DIGIT).has(Violation.MISSING_DIGIT));
    }

    @Test
    void when_it_has_no_uppercase_letter() {
        assertDisplayName("when it has no uppercase letter");
        assertTrue(policy.validate(NO_UPPERCASE).has(Violation.MISSING_UPPERCASE));
    }

    @Test
    void when_it_contains_whitespace() {
        assertDisplayName("when it contains whitespace");
        assertTrue(policy.validate(WITH_SPACE).has(Violation.CONTAINS_WHITESPACE));
    }
}
