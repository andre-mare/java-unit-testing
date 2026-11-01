package com.example.module_08.section_01;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Module 8.1 – a home-made {@code @RepeatedTest}: run a test once per currency.
 * <p>
 * Meta-annotated with {@link TestTemplate} (so JUnit treats the method as a
 * template, not a test) and with the extension that supplies the invocations.
 * This is exactly how {@code @RepeatedTest} and {@code @ParameterizedTest} are built.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@TestTemplate
@ExtendWith(CurrencyInvocationContextProvider.class)
public @interface ForEachCurrency {

    /** ISO 4217 codes, one invocation each. */
    String[] value() default { "EUR", "USD", "GBP" };

    /** Accept an empty {@link #value()}. Without it, zero invocations is an error. */
    boolean allowZero() default false;
}
