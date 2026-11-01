package com.example.module_02.section_02;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


/**
 * Module 2.2 – an assumption in {@code @BeforeAll} aborts the whole class.
 * <p>
 * Typical use: an "integration" test class that needs a database or a sandbox
 * API. Here the switch is a system property:
 * <pre>
 * mvn test -Dtest=ClassLevelAssumptionTest                     // both tests aborted
 * mvn test -Dtest=ClassLevelAssumptionTest -Dbank.sandbox=on   // both tests run
 * </pre>
 * Module 5.1 shows the declarative alternative, {@code @EnabledIfSystemProperty},
 * which skips the class without even running {@code @BeforeAll}.
 */
class ClassLevelAssumptionTest {

    @BeforeAll
    static void requireSandbox() {
        assumeTrue("on".equals(System.getProperty("bank.sandbox")),
            "set -Dbank.sandbox=on to run the sandbox tests");
    }

    @Test
    void sandboxTestOne() {
        assertTrue(true);
    }

    @Test
    void sandboxTestTwo() {
        assertTrue(true);
    }
}
