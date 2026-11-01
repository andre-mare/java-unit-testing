package com.example.module_03.section_01;

import static com.example.module_03.support.SamplePasswords.LETTERS_ONLY;
import static com.example.module_03.support.SamplePasswords.WITH_SPACE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.password.Violation;

/**
 * Module 3.1 lab – setting the global default generator.
 * <p>
 * This class has NO display-name annotation, so it uses the project default.
 * It only prints its names; look at them in the console or IDE.
 * <p>
 * <b>Run 1</b> – plain {@code mvn test -Dtest=DefaultGeneratorTest}:
 * <pre>
 * rejects_a_password_with_only_letters()
 * rejects_a_password_with_a_space()
 * </pre>
 * <b>Run 2</b> – set the default for one run:
 * <pre>
 * mvn test -Dtest=DefaultGeneratorTest \
 *   "-Djunit.jupiter.displayname.generator.default=org.junit.jupiter.api.DisplayNameGenerator$ReplaceUnderscores"
 * </pre>
 * Expected:
 * <pre>
 * rejects a password with only letters
 * rejects a password with a space
 * </pre>
 * <b>Run 3</b> – make it permanent: uncomment the line in
 * {@code src/test/resources/junit-platform.properties}. That changes the names
 * in every module, but no test in the course depends on it: classes that check
 * their names declare their generator or {@code @DisplayName} explicitly, and
 * those always win over the default.
 */
class DefaultGeneratorTest extends DisplayNameTestSupport {

    @Test
    void rejects_a_password_with_only_letters() {
        System.out.println("display name: " + displayName());
        assertFalse(policy.validate(LETTERS_ONLY).isValid());
    }

    @Test
    void rejects_a_password_with_a_space() {
        System.out.println("display name: " + displayName());
        assertTrue(policy.validate(WITH_SPACE).has(Violation.CONTAINS_WHITESPACE));
    }
}
