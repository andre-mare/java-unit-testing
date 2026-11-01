package com.example.module_07.section_01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.example.iban.Iban;
import com.example.iban.IbanGenerator;
import com.example.iban.IbanValidator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.TestInfo;

/**
 * Module 7.1 – {@code @RepeatedTest}: run the same test many times.
 * <p>
 * Start here for Module 7. A repeated test is a container; each repetition is a
 * test with the full lifecycle ({@code @BeforeEach} runs before every one).
 * Typical uses: generated input, as here, and hunting flaky behaviour.
 * <p>
 * Repeatable randomness: each repetition seeds its generator with its own
 * repetition number, so a failing repetition N fails the same way on every run.
 * <p>
 * Display name placeholders: {@code {displayName}}, {@code {currentRepetition}},
 * {@code {totalRepetitions}}. Each test checks its own name.
 */
class RepeatedIbanGenerationTest {

    private static final Set<Iban> generated = new HashSet<>();

    private IbanGenerator generator;

    @BeforeAll
    static void reset() {
        generated.clear();
    }

    @BeforeEach
    void seedPerRepetition(RepetitionInfo repetition) {
        generator = new IbanGenerator(new Random(repetition.getCurrentRepetition()));
    }

    @RepeatedTest(20)
    void generatedIbansAreValid(TestInfo info, RepetitionInfo repetition) {
        Iban iban = generator.nextDutchIban();

        assertTrue(IbanValidator.isValid(iban.toString()), () -> "repetition " + repetition.getCurrentRepetition() + ": " + iban);
        assertEquals("repetition %d of 20".formatted(repetition.getCurrentRepetition()), info.getDisplayName(),
            "the default name pattern");
    }

    @RepeatedTest(value = 5, name = "IBAN {currentRepetition}/{totalRepetitions}")
    void customNamePattern(TestInfo info, RepetitionInfo repetition) {
        generated.add(generator.nextDutchIban());

        assertEquals("IBAN %d/5".formatted(repetition.getCurrentRepetition()), info.getDisplayName());
        assertEquals(repetition.getCurrentRepetition(), generated.size(), "different seeds, different IBANs");
    }

    @RepeatedTest(value = 3, name = RepeatedTest.LONG_DISPLAY_NAME)
    @DisplayName("Dutch IBAN")
    void longDisplayName(TestInfo info, RepetitionInfo repetition) {
        // LONG_DISPLAY_NAME = "{displayName} :: repetition {currentRepetition} of {totalRepetitions}"
        assertEquals("Dutch IBAN :: repetition %d of 3".formatted(repetition.getCurrentRepetition()),
            info.getDisplayName());
        assertEquals("NL", generator.nextDutchIban().countryCode());
    }
}
