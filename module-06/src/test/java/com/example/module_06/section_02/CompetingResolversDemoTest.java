package com.example.module_06.section_02;

import java.time.Clock;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

/**
 * Module 6.2 – FAILS on purpose: two resolvers for the same parameter.
 * <p>
 * JUnit does not pick one. It fails with {@code ParameterResolutionException:
 * Discovered multiple competing ParameterResolvers for parameter [java.time.Clock clock] ...}.
 * Make {@code supportsParameter} specific enough, for example by also requiring
 * an annotation, so that exactly one resolver claims each parameter.
 * <p>
 * Tagged {@code expected-failure-tests} and excluded from the normal build. Run it with:
 * <pre>mvn test -Dtest=CompetingResolversDemoTest -DexcludedGroups=none</pre>
 */
@Tag("expected-failure-tests")
@ExtendWith({ FixedClockResolver.class, CompetingResolversDemoTest.SystemClockResolver.class })
class CompetingResolversDemoTest {

    @Test
    void whichClock(Clock clock) {
        // never reached
    }

    static class SystemClockResolver extends TypeBasedParameterResolver<Clock> {

        @Override
        public Clock resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
            return Clock.systemUTC();
        }
    }
}
