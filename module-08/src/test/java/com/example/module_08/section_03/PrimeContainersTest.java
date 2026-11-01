package com.example.module_08.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.example.primes.Primes;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

/**
 * Module 8.3 lab – one test per number from 1 to 100, in two containers.
 * <p>
 * The expected answer comes from a fixed list of the 25 primes below 100, not
 * from the code under test. The report shows a tree:
 * {@code primes (25)} with 25 tests and {@code non-primes (75)} with 75.
 */
class PrimeContainersTest {

    private static final Set<Integer> PRIMES_BELOW_100 = Set.of(
        2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47,
        53, 59, 61, 67, 71, 73, 79, 83, 89, 97);

    @TestFactory
    Stream<DynamicContainer> oneHundredNumbers() {
        Map<Boolean, List<Integer>> byPrimality = IntStream.rangeClosed(1, 100).boxed()
            .collect(Collectors.partitioningBy(PRIMES_BELOW_100::contains));
        return Stream.of(
            container("primes", byPrimality.get(true), true),
            container("non-primes", byPrimality.get(false), false));
    }

    private static DynamicContainer container(String name, List<Integer> numbers, boolean prime) {
        return dynamicContainer(name + " (" + numbers.size() + ")", numbers.stream()
            .map(n -> dynamicTest(String.valueOf(n), () -> assertEquals(prime, Primes.isPrime(n)))));
    }

    @Test
    void theSieveFindsTheSame25() {
        assertEquals(PRIMES_BELOW_100, Set.copyOf(Primes.upTo(100)));
        assertEquals(25, Primes.upTo(100).size());
    }
}
