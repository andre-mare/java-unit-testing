package com.example.module_08.section_03;

import static com.example.module_08.support.ConsentFixtures.CLIENT;
import static com.example.module_08.support.ConsentFixtures.PSU;
import static com.example.module_08.support.ConsentFixtures.READ_ACCOUNTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.time.Duration;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.example.consent.ConsentService;
import com.example.consent.InMemoryConsentRepository;
import com.example.module_08.support.ConsentFixtures;
import com.example.module_08.support.MutableClock;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * Module 8.3 – the lifecycle caveat, proved.
 * <p>
 * There are no lifecycle callbacks for individual dynamic tests: {@code @BeforeEach}
 * and {@code @AfterEach} run once around the whole factory. So the repository below
 * is <em>shared</em> by all five dynamic tests, and each sees the consents of the
 * ones before it. That is fine when you want it (as here, on purpose) and a nasty
 * surprise when you don't: create fresh state inside each test body instead.
 */
@Execution(ExecutionMode.SAME_THREAD)
class DynamicLifecycleTest {

    private static int beforeEachCalls;

    private InMemoryConsentRepository repository;
    private ConsentService service;

    @BeforeEach
    void freshService() {
        beforeEachCalls++;
        repository = new InMemoryConsentRepository();
        service = new ConsentService(repository, new MutableClock(ConsentFixtures.START), ConsentFixtures.sequentialIds());
    }

    @TestFactory
    Stream<DynamicTest> fiveTestsShareOneSetUp() {
        return IntStream.rangeClosed(1, 5).mapToObj(n -> dynamicTest("request " + n, () -> {
            service.request(CLIENT, PSU, Set.of(READ_ACCOUNTS), Duration.ofDays(30));
            assertEquals(n, repository.count(), "the repository was not reset between dynamic tests");
        }));
    }

    @AfterAll
    static void beforeEachRanOnce() {
        assertEquals(1, beforeEachCalls, "once for the factory, not five times");
    }
}
