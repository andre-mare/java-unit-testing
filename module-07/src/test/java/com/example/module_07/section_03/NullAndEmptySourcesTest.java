package com.example.module_07.section_03;

import static com.example.module_07.support.TokenScenarios.ISSUER;
import static com.example.module_07.support.TokenScenarios.NOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Set;

import com.example.iban.Iban;
import com.example.iban.IbanValidator;
import com.example.module_07.support.TokenScenarios;
import com.example.tokens.ClaimProblem;
import com.example.tokens.Claims;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Module 7.3 – {@code @NullSource}, {@code @EmptySource} and {@code @NullAndEmptySource}.
 * <p>
 * Bad input is where bugs live. These sources add the corner cases:
 * <ul>
 *   <li>{@code @NullSource}: one {@code null} (not for primitive parameters);</li>
 *   <li>{@code @EmptySource}: one empty value: {@code ""}, an empty collection, map or array;</li>
 *   <li>{@code @NullAndEmptySource}: both.</li>
 * </ul>
 * Combine them with {@code @ValueSource} for blank strings: six invocations below.
 */
class NullAndEmptySourcesTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   ", "\t", "\n" })
    void blankInputIsNeverValid(String input) {
        assertFalse(IbanValidator.isValid(input));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Iban.parse(input));
        assertEquals("IBAN must not be blank", e.getMessage());
    }

    @ParameterizedTest
    @NullSource
    void nullSubjectIsReported(String subject) {
        Claims claims = new Claims(subject, ISSUER, Set.of(TokenScenarios.AUDIENCE), null, NOW.plus(Duration.ofHours(1)));
        assertEquals(Set.of(ClaimProblem.MISSING_SUBJECT), TokenScenarios.validator().validate(claims));
    }

    @ParameterizedTest
    @EmptySource
    void emptyAudienceIsReported(Set<String> audience) {
        assertTrue(audience.isEmpty());
        Claims claims = new Claims("alice", ISSUER, audience, null, NOW.plus(Duration.ofHours(1)));
        assertEquals(Set.of(ClaimProblem.WRONG_AUDIENCE), TokenScenarios.validator().validate(claims));
    }
}
