package com.example.module_08.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;
import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

import com.example.primes.Primes;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;

/**
 * Module 8.3 – execution mode per dynamic node (new in JUnit 6.1, experimental).
 * <p>
 * Instead of positional arguments, {@code dynamicTest(config -> ...)} and
 * {@code dynamicContainer(config -> ...)} take a configuration callback:
 * {@code displayName}, {@code testSourceUri}, {@code executionMode}, and for a
 * container {@code childExecutionMode} and {@code children}; for a test,
 * {@code executable}.
 * <p>
 * The modes only matter when parallel execution is enabled (Module 5). Then:
 * the factory's own mode comes from {@code @Execution}; a node's
 * {@code executionMode} sets its own mode; a container's {@code childExecutionMode}
 * sets the mode of children that do not set their own.
 */
@Execution(CONCURRENT)
class DynamicExecutionModeTest {

    private static final List<Integer> RANGES = List.of(1_000, 10_000, 100_000);

    @TestFactory
    Stream<DynamicContainer> primeCountsPerRange() {
        return RANGES.stream().map(limit -> dynamicContainer(container -> container
            .displayName("primes up to " + limit)
            .childExecutionMode(SAME_THREAD)                      // children run one after another...
            .children(
                dynamicTest(test -> test
                    .displayName("sieve and trial division agree")
                    .executable(() -> assertTrue(Primes.upTo(limit).stream().allMatch(Primes::isPrime)))),
                dynamicTest(test -> test
                    .displayName("last prime has no smaller factors")
                    .executionMode(CONCURRENT)                    // ...except this one, which sets its own
                    .testSourceUri(URI.create("method:com.example.primes.Primes#factorize(int)"))
                    .executable(() -> {
                        List<Integer> primes = Primes.upTo(limit);
                        int last = primes.get(primes.size() - 1);
                        assertEquals(List.of(last), Primes.factorize(last));
                    })))));
    }
}
