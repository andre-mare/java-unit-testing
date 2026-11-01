package com.example.module_06.section_01;

import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Module 6.1 – both tests FAIL on purpose: parameters nobody can resolve.
 * <p>
 * Every parameter needs a {@code ParameterResolver}. When none supports it, the test
 * fails before it starts with a {@code ParameterResolutionException} such as
 * {@code No ParameterResolver registered for parameter [java.lang.String clientId] ...}.
 * <p>
 * Tagged {@code expected-failure-tests} and excluded from the normal build. Run it with:
 * <pre>mvn test -Dtest=ResolutionFailuresDemoTest -DexcludedGroups=none</pre>
 */
@Tag("expected-failure-tests")
class ResolutionFailuresDemoTest {

    @Test
    void repetitionInfoOutsideARepeatedTest(RepetitionInfo info) {
        // RepetitionInfo is only resolvable inside a @RepeatedTest.
    }

    @Test
    void noResolverForAString(String clientId) {
        // No built-in resolver for String; Module 6.2 shows how to write your own.
    }
}
