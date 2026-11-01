package com.example.module_05.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.onboarding.Onboarding;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Module 5.3 lab – random order exposes a hidden dependency between tests.
 * <p>
 * The second test secretly relies on the first having run before it, through
 * a {@code static} field. Without an orderer this may pass for months, because
 * the default order happens to suit it. With {@code Random} it fails for some
 * seeds and passes for others:
 * <pre>
 * mvn test -Dtest=HiddenCouplingDemoTest -DexcludedGroups=none -Djunit.jupiter.execution.order.random.seed=1
 * mvn test -Dtest=HiddenCouplingDemoTest -DexcludedGroups=none -Djunit.jupiter.execution.order.random.seed=2
 * </pre>
 * Try a few seeds until you see both outcomes. The fix is to make each test
 * create its own {@link Onboarding}, never to add {@code @Order}.
 * <p>
 * Tagged {@code expected-failure-tests} because it can fail; excluded from the normal build.
 */
@Tag("expected-failure-tests")
@TestMethodOrder(MethodOrderer.Random.class)
class HiddenCouplingDemoTest {

    private static Onboarding shared;

    @Test
    void createsTheOnboarding() {
        shared = new Onboarding(() -> "654321");
        shared.register("coupled@example.com");
        assertNotNull(shared);
    }

    @Test
    void sendsTheCode() {
        // NullPointerException whenever this runs first.
        assertEquals("654321", shared.sendVerificationCode());
    }
}
