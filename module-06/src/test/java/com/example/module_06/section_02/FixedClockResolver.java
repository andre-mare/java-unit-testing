package com.example.module_06.section_02;

import java.time.Clock;

import com.example.module_06.support.AuthFixtures;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

/**
 * Module 6.2 – the simplest custom resolver: one type, one value.
 * <p>
 * {@link TypeBasedParameterResolver} implements {@code supportsParameter} for you:
 * it supports exactly the type argument, here {@link Clock}. Every {@code Clock}
 * parameter gets the fixed Module 6 clock.
 */
public class FixedClockResolver extends TypeBasedParameterResolver<Clock> {

    @Override
    public Clock resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return AuthFixtures.FIXED_CLOCK;
    }
}
