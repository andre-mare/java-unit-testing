package com.example.module_03.section_02;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Module 3.2 – {@code @Disabled} on a whole class.
 * <p>
 * Every test in the class is skipped, and unlike a disabled method, nothing in
 * the class runs at all: no {@code @BeforeAll}, no constructor, no
 * {@code @AfterAll}. Each of them calls {@code fail()}, so if any ran, the build
 * would go red. The result shows this one class as skipped with its reason.
 */
@Disabled("SEC-150: passphrase policy suite parked until the new policy ships")
class DisabledClassTest {

    DisabledClassTest() {
        fail("a disabled class is never instantiated");
    }

    @BeforeAll
    static void beforeAll() {
        fail("@BeforeAll does not run for a disabled class");
    }

    @Test
    void acceptsLongPassphraseWithoutSymbols() {
        fail("never executed");
    }

    @Test
    void rejectsDictionaryWords() {
        fail("never executed");
    }

    @AfterAll
    static void afterAll() {
        fail("@AfterAll does not run for a disabled class");
    }
}
