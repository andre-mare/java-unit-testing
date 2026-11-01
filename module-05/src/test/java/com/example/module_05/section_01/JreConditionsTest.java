package com.example.module_05.section_01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.condition.JRE.JAVA_17;
import static org.junit.jupiter.api.condition.JRE.JAVA_21;
import static org.junit.jupiter.api.condition.JRE.JAVA_25;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledForJreRange;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.EnabledOnJre;

/**
 * Module 5.1 – Java runtime conditions.
 * <p>
 * Two ways to name a version:
 * <ul>
 *   <li>{@code JRE} enum constants ({@code JAVA_17}, {@code JAVA_21}, ...) via {@code value},
 *       {@code min} and {@code max};</li>
 *   <li>plain numbers via {@code versions}, {@code minVersion} and {@code maxVersion}, for
 *       Java versions newer than the enum of your JUnit release.</li>
 * </ul>
 * Ranges are inclusive and open-ended when one bound is omitted.
 * Each body asserts the version the condition allowed.
 */
class JreConditionsTest {

    private static int javaVersion() {
        return Runtime.version().feature();
    }

    @Test
    void printsTheRunningVersion() {
        System.out.println("Running on Java " + javaVersion());
        assertTrue(javaVersion() >= 17, "JUnit 6 requires Java 17+");
    }

    @Test
    @EnabledOnJre(JAVA_17)
    void onlyOnJava17() {
        assertEquals(17, javaVersion());
    }

    @Test
    @EnabledOnJre({ JAVA_21, JAVA_25 })
    void onLtsReleases21And25() {
        assertTrue(Set.of(21, 25).contains(javaVersion()));
    }

    @Test
    @EnabledForJreRange(min = JAVA_21)
    void onJava21AndLater() {
        assertTrue(javaVersion() >= 21);
    }

    @Test
    @EnabledForJreRange(min = JAVA_17, max = JAVA_21)
    void fromJava17To21() {
        assertTrue(javaVersion() >= 17 && javaVersion() <= 21);
    }

    @Test
    @DisabledOnJre(JAVA_17)
    void notOnJava17() {
        assertNotEquals(17, javaVersion());
    }

    @Test
    @DisabledForJreRange(max = JAVA_21)
    void notUpToJava21() {
        assertTrue(javaVersion() > 21);
    }

    @Test
    @EnabledOnJre(versions = { 29, 30 })
    void onFutureVersionsByNumber() {
        assertTrue(Set.of(29, 30).contains(javaVersion()));
    }

    @Test
    @EnabledForJreRange(minVersion = 26)
    void onJava26AndLaterByNumber() {
        assertTrue(javaVersion() >= 26);
    }
}
