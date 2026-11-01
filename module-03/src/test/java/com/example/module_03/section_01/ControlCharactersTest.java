package com.example.module_03.section_01;

import static com.example.module_03.support.SamplePasswords.WITH_SPACE;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.password.Violation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Module 3.1 – control characters in display names are replaced.
 * <p>
 * Since JUnit 6, invisible control characters are made visible so they cannot
 * break a report line:
 * <ul>
 *   <li>{@code \r} becomes {@code <CR>}</li>
 *   <li>{@code \n} becomes {@code <LF>}</li>
 *   <li>any other control character (such as a tab) becomes U+FFFD, shown as {@code �}</li>
 * </ul>
 * Parameterized tests quote and escape their text arguments separately (Module 7.7).
 */
class ControlCharactersTest extends DisplayNameTestSupport {

    @Test
    @DisplayName("line feed:\nis replaced")
    void lineFeed() {
        assertDisplayName("line feed:<LF>is replaced");
        assertTrue(policy.validate("Correct\nHorse-9-Battery").has(Violation.CONTAINS_WHITESPACE));
    }

    @Test
    @DisplayName("carriage return:\ris replaced")
    void carriageReturn() {
        assertDisplayName("carriage return:<CR>is replaced");
        assertTrue(policy.validate("Correct\rHorse-9-Battery").has(Violation.CONTAINS_WHITESPACE));
    }

    @Test
    @DisplayName("tab:\tis replaced")
    void tab() {
        assertDisplayName("tab:�is replaced");
        assertTrue(policy.validate(WITH_SPACE.replace(' ', '\t')).has(Violation.CONTAINS_WHITESPACE));
    }
}
