package com.example.module_07.section_08;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.example.payments.Amount;
import com.example.payments.PaymentScheme;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.AfterParameterizedClassInvocation;
import org.junit.jupiter.params.BeforeParameterizedClassInvocation;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Module 7.8 – the lifecycle of a parameterized class, recorded and checked.
 * <ul>
 *   <li>{@code @BeforeAll} / {@code @AfterAll}: once for the whole class, around all invocations;</li>
 *   <li>{@code @BeforeParameterizedClassInvocation} / {@code @AfterParameterizedClassInvocation}:
 *       once per invocation; {@code static} unless {@code PER_CLASS}. With
 *       {@code injectArguments = true} (the default) they can take the class arguments
 *       as parameters; with {@code false} they cannot;</li>
 *   <li>{@code @BeforeEach}: before every test of every invocation, but it cannot
 *       receive the arguments; use a field or the constructor.</li>
 * </ul>
 * {@code @AfterAll} compares the log with the expected order.
 */
@ParameterizedClass
@EnumSource(names = { "SEPA_INSTANT", "SWIFT_WIRE" })
@TestMethodOrder(MethodOrderer.MethodName.class)
@Execution(ExecutionMode.SAME_THREAD)
class ParameterizedClassLifecycleTest {

    private static final List<String> log = new ArrayList<>();

    private final PaymentScheme scheme;

    ParameterizedClassLifecycleTest(PaymentScheme scheme) {
        this.scheme = scheme;
    }

    @BeforeAll
    static void beforeAll() {
        log.add("beforeAll");
    }

    @BeforeParameterizedClassInvocation
    static void beforeInvocation(PaymentScheme scheme) {
        log.add("before " + scheme);
    }

    @BeforeEach
    void beforeEach() {
        log.add("beforeEach");
    }

    @Test
    void a_allowsSmallEuroAmounts() {
        log.add("test a");
        assertTrue(scheme.allows(Amount.euro("1.00")));
    }

    @Test
    void b_hasAName() {
        log.add("test b");
        assertTrue(scheme.name().length() > 0);
    }

    @AfterParameterizedClassInvocation(injectArguments = false)
    static void afterInvocation() {
        log.add("after");
    }

    @AfterAll
    static void checkTheOrder() {
        assertEquals(List.of(
            "beforeAll",
            "before SEPA_INSTANT", "beforeEach", "test a", "beforeEach", "test b", "after",
            "before SWIFT_WIRE", "beforeEach", "test a", "beforeEach", "test b", "after"), log);
    }
}
