package com.example.module_05.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.onboarding.Onboarding;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Module 5.3 – {@code MethodOrderer.Random}: a pseudo-random order.
 * <p>
 * Independent tests must pass in any order; random ordering is a cheap way to
 * prove it. Each test here uses its own {@link Onboarding}, so the class always passes.
 * <p>
 * The order is printed. To repeat a failing order, fix the seed:
 * <pre>
 * mvn test -Dtest=RandomOrderTest -Djunit.jupiter.execution.order.random.seed=42
 * </pre>
 * Run twice with the same seed: same order. Change the seed: different order.
 */
@TestMethodOrder(MethodOrderer.Random.class)
class RandomOrderTest {

    private static final List<String> executed = new ArrayList<>();

    @BeforeAll
    static void reset() {
        executed.clear();
    }

    @Test
    void registers() {
        executed.add("registers");
        Onboarding onboarding = new Onboarding();
        onboarding.register("a@example.com");
        assertEquals(Onboarding.Step.REGISTERED, onboarding.step());
    }

    @Test
    void sendsSixDigitCode() {
        executed.add("sendsSixDigitCode");
        Onboarding onboarding = new Onboarding();
        onboarding.register("b@example.com");
        assertTrue(onboarding.sendVerificationCode().matches("\\d{6}"));
    }

    @Test
    void rejectsInvalidEmail() {
        executed.add("rejectsInvalidEmail");
        assertThrows(IllegalArgumentException.class,
            () -> new Onboarding().register("not-an-email"));
    }

    @Test
    void cannotVerifyBeforeRegistering() {
        executed.add("cannotVerifyBeforeRegistering");
        assertThrows(IllegalStateException.class,
            () -> new Onboarding().verify("123456"));
    }

    @Test
    void startsAtStarted() {
        executed.add("startsAtStarted");
        assertEquals(Onboarding.Step.STARTED, new Onboarding().step());
    }

    @AfterAll
    static void allRanOnce() {
        System.out.println("Random order: " + executed);
        assertEquals(5, Set.copyOf(executed).size());
    }
}
