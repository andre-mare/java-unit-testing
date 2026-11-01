package com.example.module_03.section_02;

import static com.example.module_03.support.SamplePasswords.TOO_SHORT;
import static com.example.module_03.support.SamplePasswords.VALID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.password.PasswordPolicy;
import com.example.password.Violation;


/**
 * Module 3.2 lab – {@code @Disabled} on individual test methods, and what still runs.
 * <p>
 * Two tests run, two are disabled. The counters prove what JUnit does with the
 * disabled ones, and {@code @AfterAll} checks the counts:
 * <table>
 *   <caption>What happens for a disabled test method</caption>
 *   <tr><th>Step</th><th>Runs?</th></tr>
 *   <tr><td>{@code @BeforeAll} / {@code @AfterAll}</td><td>yes, once for the class</td></tr>
 *   <tr><td>test class constructor</td><td>yes, still once per test method, disabled ones included</td></tr>
 *   <tr><td>{@code @BeforeEach} / {@code @AfterEach}</td><td>no</td></tr>
 *   <tr><td>the test method</td><td>no</td></tr>
 * </table>
 * Run the whole class: filtering to a single method changes the counts.
 * <p>
 * In the report, compare the two skip reasons:
 * the ticketed one shows your text; the bare one shows a generated reason
 * such as {@code "void ...disabledWithoutReason() is @Disabled"}.
 */
class DisabledMethodTest {

    private static int instancesCreated;
    private static int beforeEachRuns;
    private static int testsRun;

    private final PasswordPolicy policy = PasswordPolicy.standard();

    DisabledMethodTest() {
        instancesCreated++;
    }

    @BeforeAll
    static void resetCounters() {
        instancesCreated = 0;
        beforeEachRuns = 0;
        testsRun = 0;
    }

    @BeforeEach
    void countBeforeEach() {
        beforeEachRuns++;
    }

    @Test
    void acceptsValidPassword() {
        testsRun++;
        assertTrue(policy.validate(VALID).isValid());
    }

    @Test
    void rejectsShortPassword() {
        testsRun++;
        assertTrue(policy.validate(TOO_SHORT).has(Violation.TOO_SHORT));
    }

    /** The recommended form: a reason with a ticket number, so the skip is traceable. */
    @Test
    @Disabled("SEC-142: breach check is not implemented yet")
    @DisplayName("rejects a password from a breach list")
    void rejectsBreachedPassword() {
        testsRun++;
        // Would throw UnsupportedOperationException today; enable once SEC-142 ships.
        assertTrue(policy.isKnownBreached("Password123!"));
    }

    /** Allowed, but discouraged: nobody can tell later why this was switched off. */
    @Test
    @Disabled
    void disabledWithoutReason() {
        testsRun++;
        fail("never executed");
    }

    @AfterAll
    static void verifyWhatRan() {
        assertEquals(2, testsRun, "only the enabled tests run");
        assertEquals(2, beforeEachRuns, "@BeforeEach is skipped for disabled tests");
        assertEquals(4, instancesCreated, "the class is still instantiated for each disabled test");
    }
}
