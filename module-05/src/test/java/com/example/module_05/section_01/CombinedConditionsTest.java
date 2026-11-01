package com.example.module_05.section_01;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.condition.OS.LINUX;
import static org.junit.jupiter.api.condition.OS.MAC;
import static org.junit.jupiter.api.condition.OS.OTHER;
import static org.junit.jupiter.api.condition.OS.WINDOWS;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;

/**
 * Module 5.1 – combining different conditions: the first "disabled" wins.
 * <p>
 * Conditions of different kinds can be stacked on one test. JUnit evaluates
 * them all, and a single "disabled" result skips the test.
 */
class CombinedConditionsTest {

    @Test
    @EnabledOnOs({ LINUX, MAC, WINDOWS, OTHER })
    @EnabledForJreRange(min = JRE.JAVA_21)
    void everyConditionAllowsIt() {
        assertTrue(Runtime.version().feature() >= 21);
    }


    @Test
    @EnabledOnOs({ LINUX, MAC, WINDOWS, OTHER })                  // allows
    @DisabledIfSystemProperty(named = "java.version", matches = ".+",
        disabledReason = "always set, so this test never runs")    // disables
    void oneDisabledConditionIsEnough() {
        fail("never runs: one condition said no");
    }
}
