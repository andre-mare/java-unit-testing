package com.example.module_05.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.onboarding.Onboarding;
import com.example.onboarding.Onboarding.Step;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Module 5.3 lab – an ordered scenario: one test per step of a user's sign-up.
 * <p>
 * The steps depend on each other on purpose: they share one {@link Onboarding}
 * (thanks to {@code PER_CLASS}), and {@code @Order} fixes the sequence. This
 * style suits integration and functional tests that walk through a workflow;
 * the report shows exactly which step broke.
 * <p>
 * The cost: the steps cannot run alone. Run step 3 by itself and it fails,
 * because steps 1 and 2 never happened. Pure unit tests should never depend on
 * order; see {@link HiddenCouplingDemoTest}.
 */
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Onboarding a new user")
class OnboardingScenarioTest {

    private final Onboarding onboarding = new Onboarding(() -> "123456");
    private String code;

    @Test
    @Order(1)
    @DisplayName("1. registers with an e-mail address")
    void register() {
        onboarding.register("jane@example.com");
        assertEquals(Step.REGISTERED, onboarding.step());
    }

    @Test
    @Order(2)
    @DisplayName("2. receives a verification code")
    void sendCode() {
        code = onboarding.sendVerificationCode();
        assertEquals("123456", code);
        assertEquals(Step.CODE_SENT, onboarding.step());
    }

    @Test
    @Order(3)
    @DisplayName("3. rejects a wrong code")
    void rejectWrongCode() {
        assertThrows(IllegalArgumentException.class, () -> onboarding.verify("000000"));
        assertEquals(Step.CODE_SENT, onboarding.step(), "a wrong code changes nothing");
    }

    @Test
    @Order(4)
    @DisplayName("4. verifies with the right code")
    void verify() {
        onboarding.verify(code);
        assertEquals(Step.VERIFIED, onboarding.step());
    }

    @Test
    @Order(5)
    @DisplayName("5. activates the account")
    void activate() {
        onboarding.activate();
        assertEquals(Step.ACTIVE, onboarding.step());
    }

    @Test
    @Order(6)
    @DisplayName("6. cannot be activated twice")
    void cannotActivateTwice() {
        IllegalStateException e = assertThrows(IllegalStateException.class, onboarding::activate);
        assertEquals("Expected step VERIFIED but was ACTIVE", e.getMessage());
    }
}
