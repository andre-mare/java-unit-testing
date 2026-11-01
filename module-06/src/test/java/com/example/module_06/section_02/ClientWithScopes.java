package com.example.module_06.section_02;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Module 6.2 – configures a {@code Client} parameter resolved by {@link ClientFixtureExtension}.
 * <pre>
 * void test(@ClientWithScopes({"payments:read", "payments:write"}) Client client)
 * void test(@ClientWithScopes(value = "read", enabled = false) Client client)
 * </pre>
 * Without this annotation the client gets the single scope {@code read}.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClientWithScopes {

    /** Scopes the client may request. */
    String[] value();

    /** {@code false} registers a disabled client. */
    boolean enabled() default true;
}
