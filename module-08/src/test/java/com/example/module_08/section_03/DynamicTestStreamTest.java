package com.example.module_08.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import com.example.primes.Primes;
import com.example.scope.ScopeSet;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.NamedExecutable;
import org.junit.jupiter.api.TestFactory;

/**
 * Module 8.3 – {@code DynamicTest.stream(...)}: tests from input data.
 * <p>
 * Four forms, from most to least code:
 * <ol>
 *   <li>an {@code Iterator} of inputs, a display-name function and a test body;</li>
 *   <li>the same with a {@code Stream} of inputs;</li>
 *   <li>a stream of {@link Named} inputs, so the name travels with the value;</li>
 *   <li>a stream of {@link NamedExecutable}s: each input is its own test.</li>
 * </ol>
 */
class DynamicTestStreamTest {

    /** Generates numbers with exactly two prime factors: 4, 6, 9, 10, 14, ... */
    static final class SemiprimeGenerator implements Iterator<Integer> {

        private int next = 4;
        private int produced;

        @Override
        public boolean hasNext() {
            return produced < 10;
        }

        @Override
        public Integer next() {
            while (Primes.factorize(next).size() != 2) {
                next++;
            }
            produced++;
            return next++;
        }
    }

    @TestFactory
    Stream<DynamicTest> fromAnIterator() {
        return DynamicTest.stream(new SemiprimeGenerator(),
            n -> n + " has two prime factors",
            n -> assertEquals(2, Primes.factorize(n).size()));
    }

    @TestFactory
    Stream<DynamicTest> fromAStream() {
        return DynamicTest.stream(Stream.of(8, 27, 32, 81),
            n -> n + " is a prime power",
            n -> assertEquals(1, Set.copyOf(Primes.factorize(n)).size()));
    }

    @TestFactory
    Stream<DynamicTest> fromNamedInputs() {
        return DynamicTest.stream(Stream.of(
                named("OpenID Connect login", "openid profile"),
                named("account information (PSD2)", "accounts balances transactions"),
                named("duplicates collapse", "openid openid")),
            scope -> assertTrue(ScopeSet.parse(scope).size() >= 1));
    }

    /** A test case that is its own executable: name and body in one record. */
    record ScopeContains(String scope, String token) implements NamedExecutable {

        @Override
        public String getName() {
            return "'" + scope + "' contains " + token;
        }

        @Override
        public void execute() {
            assertTrue(ScopeSet.parse(scope).contains(token));
        }
    }

    @TestFactory
    Stream<DynamicTest> fromNamedExecutables() {
        return DynamicTest.stream(Stream.of(
            new ScopeContains("openid profile", "openid"),
            new ScopeContains("accounts payments:write", "payments:write"),
            new ScopeContains("urn:example:read", "urn:example:read")));
    }
}
