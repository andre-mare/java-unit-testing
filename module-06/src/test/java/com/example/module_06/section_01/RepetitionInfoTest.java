package com.example.module_06.section_01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import com.example.auth.RandomTokenGenerator;
import com.example.auth.TokenGenerator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.TestInfo;

/**
 * Module 6.1 – {@link RepetitionInfo}: where a repeated test is.
 * <p>
 * Available in {@code @RepeatedTest} methods and in {@code @BeforeEach} /
 * {@code @AfterEach} methods, but only while a repeated test runs. That is why
 * this class contains repeated tests only: one plain {@code @Test} would make
 * the {@code @BeforeEach} below fail to resolve its parameter.
 * {@code @RepeatedTest} itself is covered in Module 7.1.
 */
class RepetitionInfoTest {

    private static final Set<String> issued = new HashSet<>();

    private final TokenGenerator generator = new RandomTokenGenerator();

    @BeforeAll
    static void reset() {
        issued.clear();
    }

    @BeforeEach
    void log(TestInfo testInfo, RepetitionInfo repetitionInfo) {
        System.out.printf("%s: repetition %d of %d%n", testInfo.getTestMethod().orElseThrow().getName(),
            repetitionInfo.getCurrentRepetition(), repetitionInfo.getTotalRepetitions());
    }

    @RepeatedTest(5)
    void everyTokenIsUnique(RepetitionInfo info) {
        issued.add(generator.newTokenValue());

        assertEquals(info.getCurrentRepetition(), issued.size(), "no token value was ever repeated");
        assertEquals(5, info.getTotalRepetitions());
        assertEquals(0, info.getFailureCount(), "no earlier repetition failed");
        assertEquals(Integer.MAX_VALUE, info.getFailureThreshold(), "no threshold configured");
    }

    @RepeatedTest(3)
    void tokenIsBase64UrlWith43Characters() {
        String value = generator.newTokenValue();
        assertEquals(43, value.length());
        assertTrue(value.matches("[A-Za-z0-9_-]+"));
    }
}
