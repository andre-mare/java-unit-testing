package com.example.module_07.section_02;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.iban.Iban;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Module 7.2 – the parameter-order rule, and the lifecycle of invocations.
 * <p>
 * A parameterized test declares its parameters in three groups, in this order:
 * <ol>
 *   <li>indexed parameters, filled from the source by position (0, 1, ...);</li>
 *   <li>aggregators: {@code ArgumentsAccessor} or {@code @AggregateWith} (Module 7.6);</li>
 *   <li>parameters for other resolvers, such as {@code TestInfo} and {@code TestReporter}.</li>
 * </ol>
 * Each invocation has the lifecycle of a normal test: {@code @BeforeEach} runs
 * before every one. Source arguments are NOT passed to lifecycle methods; only
 * normal resolvers such as {@code TestInfo} work there.
 */
class ParameterOrderTest {

    private static int beforeEachCalls;
    private static int invocations;

    @BeforeAll
    static void reset() {
        beforeEachCalls = 0;
        invocations = 0;
    }

    @BeforeEach
    void beforeEveryInvocation(TestInfo info) {
        beforeEachCalls++;
    }

    @ParameterizedTest
    @CsvSource({
        "NL91ABNA0417164300, NL, 91",
        "DE89370400440532013000, DE, 89"
    })
    void allThreeGroups(String iban, String country, ArgumentsAccessor all, TestInfo info, TestReporter reporter) {
        invocations++;
        Iban parsed = Iban.parse(iban);

        assertEquals(country, parsed.countryCode());                  // indexed parameter 1
        assertEquals(all.getString(2), parsed.checkDigits());          // the accessor sees every column, also index 2
        assertEquals(3, all.size());
        assertTrue(info.getDisplayName().startsWith("[" + all.getInvocationIndex() + "]"));
        reporter.publishEntry("checked", parsed.formatted());
    }

    @AfterAll
    static void beforeEachRanPerInvocation() {
        assertEquals(2, invocations);
        assertEquals(invocations, beforeEachCalls);
    }
}
