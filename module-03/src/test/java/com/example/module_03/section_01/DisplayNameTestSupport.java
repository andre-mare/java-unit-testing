package com.example.module_03.section_01;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.password.PasswordPolicy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

/**
 * Module 3.1 helper – lets a test check its own display name.
 * <p>
 * {@link TestInfo} is injected into {@code @BeforeEach} (not into the test
 * method), so the test methods keep an empty parameter list. That matters:
 * a parameter would change the generated name, for example from
 * {@code acceptsValidPassword()} to {@code acceptsValidPassword(TestInfo)}.
 * TestInfo itself is covered in Module 6.
 * <p>
 * Not named {@code *Test}, so Surefire does not try to run it.
 */
abstract class DisplayNameTestSupport {

    protected final PasswordPolicy policy = PasswordPolicy.standard();

    private TestInfo testInfo;

    @BeforeEach
    void captureTestInfo(TestInfo testInfo) {
        this.testInfo = testInfo;
    }

    /** The display name JUnit gave the currently running test. */
    protected String displayName() {
        return testInfo.getDisplayName();
    }

    /** Asserts the display name JUnit gave the currently running test. */
    protected void assertDisplayName(String expected) {
        assertEquals(expected, displayName(), "display name of the running test");
    }
}
