package com.example.module_03.section_01;

import static com.example.module_03.support.SamplePasswords.NO_SYMBOL;
import static com.example.module_03.support.SamplePasswords.TOO_LONG;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.password.Violation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

/**
 * Module 3.1 – precedence: which name wins when several sources apply.
 * <ol>
 *   <li>{@code @DisplayName} on the element, if present</li>
 *   <li>the generator from {@code @DisplayNameGeneration} on the class (or an enclosing class)</li>
 *   <li>the default generator from {@code junit.jupiter.displayname.generator.default}</li>
 *   <li>{@code DisplayNameGenerator.Standard}</li>
 * </ol>
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DisplayNamePrecedenceTest extends DisplayNameTestSupport {

    @Test
    void uses_the_class_level_generator() {
        assertDisplayName("uses the class level generator"); // rule 2
        assertTrue(policy.validate(NO_SYMBOL).has(Violation.MISSING_SYMBOL));
    }

    @Test
    @DisplayName("@DisplayName always wins over a generator")
    void this_name_is_never_shown() {
        assertDisplayName("@DisplayName always wins over a generator"); // rule 1
        assertTrue(policy.validate(TOO_LONG).has(Violation.TOO_LONG));
    }
}
