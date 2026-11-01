package com.example.module_08.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.example.primes.Primes;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 * Module 8.3 – a {@code @TestFactory} builds tests while the suite runs.
 * <p>
 * The factory itself is not a test: it returns a {@link DynamicNode} (a
 * {@link DynamicTest} or a {@link DynamicContainer}), or a {@code Stream},
 * {@code Collection}, {@code Iterable}, {@code Iterator} or array of them.
 * JUnit runs what it returns. A factory must not be {@code private} or
 * {@code static}. Each form is shown once below; a stream is the usual choice.
 */
class FirstDynamicTestsTest {

    @TestFactory
    DynamicTest singleTest() {
        return dynamicTest("2 is the only even prime", () -> assertTrue(Primes.isPrime(2)));
    }

    @TestFactory
    List<DynamicTest> collection() {
        return List.of(
            dynamicTest("0 is not prime", () -> assertFalse(Primes.isPrime(0))),
            dynamicTest("1 is not prime", () -> assertFalse(Primes.isPrime(1))),
            dynamicTest("negative numbers are not prime", () -> assertFalse(Primes.isPrime(-7))));
    }

    @TestFactory
    Iterator<DynamicTest> iterator() {
        return List.of(
            dynamicTest("97 is prime", () -> assertTrue(Primes.isPrime(97))),
            dynamicTest("91 = 7 x 13", () -> assertEquals(List.of(7, 13), Primes.factorize(91)))).iterator();
    }

    @TestFactory
    DynamicTest[] array() {
        return new DynamicTest[] {
            dynamicTest("largest int is prime", () -> assertTrue(Primes.isPrime(Integer.MAX_VALUE))),
            dynamicTest("12 = 2 x 2 x 3", () -> assertEquals(List.of(2, 2, 3), Primes.factorize(12)))
        };
    }

    @TestFactory
    Stream<DynamicTest> stream() {
        return IntStream.of(2, 3, 5, 7, 11, 13)
            .mapToObj(p -> dynamicTest(p + " is prime", () -> assertTrue(Primes.isPrime(p))));
    }

    @TestFactory
    DynamicContainer singleContainer() {
        return dynamicContainer("trial division agrees with the sieve", Stream.of(10, 100, 1000)
            .map(limit -> dynamicTest("up to " + limit, () -> assertEquals(
                Primes.upTo(limit),
                IntStream.rangeClosed(0, limit).filter(Primes::isPrime).boxed().toList()))));
    }
}
