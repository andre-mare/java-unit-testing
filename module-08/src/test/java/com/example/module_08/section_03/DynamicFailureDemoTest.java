package com.example.module_08.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.List;
import java.util.stream.Stream;

import com.example.primes.Primes;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

/**
 * Module 8.3 – failure demo: dynamic tests fail, abort and pass independently.
 * <p>
 * Excluded from normal builds by the {@code expected-failure-tests} tag. Run with
 * {@code mvn test -DexcludedGroups=none -Dtest=DynamicFailureDemoTest}. Expected:
 * three dynamic tests from one factory; "passes" passes, "aborts" is skipped by an
 * assumption, and "fails" fails because 1 has no prime factors. The factory itself
 * is reported as a container, not as a test.
 */
@Tag("expected-failure-tests")
class DynamicFailureDemoTest {

    @TestFactory
    Stream<DynamicTest> mixedResults() {
        return Stream.of(
            dynamicTest("passes", () -> assertEquals(List.of(2, 5), Primes.factorize(10))),
            dynamicTest("aborts", () -> assumeTrue(false, "skipped on purpose")),
            dynamicTest("fails", () -> Primes.factorize(1)));
    }
}
