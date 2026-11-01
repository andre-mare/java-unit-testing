package com.example.module_08.section_01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * Module 8.1 – a first test template, without any shortcut annotation.
 * <p>
 * {@code @TestTemplate} alone does nothing: at least one registered
 * {@code TestTemplateInvocationContextProvider} must supply invocations. Each
 * invocation is a full test with the normal lifecycle, so {@code @BeforeEach} runs
 * three times below. (Compare 8.3: dynamic tests do <em>not</em> get that.)
 * {@code @AfterAll} checks the invocations, their names and the lifecycle count.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@Execution(ExecutionMode.SAME_THREAD)
class FirstTestTemplateTest {

    private static final List<String> seen = new ArrayList<>();
    private static int beforeEachCalls;

    @BeforeEach
    void countBeforeEach() {
        beforeEachCalls++;
    }

    @TestTemplate
    @ExtendWith(CurrencyInvocationContextProvider.class)
    void a_oncePerDefaultCurrency(Currency currency, TestInfo info) {
        seen.add(currency.getCurrencyCode());
        assertEquals("[" + seen.size() + "] " + currency.getCurrencyCode(), info.getDisplayName());
    }

    @ForEachCurrency(value = {}, allowZero = true)
    void b_zeroInvocationsAllowed(Currency currency) {
        fail("never invoked: no currencies");
    }

    @AfterAll
    static void threeInvocationsWithTheirOwnLifecycle() {
        assertEquals(List.of("EUR", "USD", "GBP"), seen);
        assertEquals(3, beforeEachCalls, "@BeforeEach runs once per template invocation");
    }
}
