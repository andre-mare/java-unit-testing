package com.example.module_05.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Module 5.2 / 5.4 – a slow test: {@code @Test} + {@code @Tag("slow")} + {@code @Timeout(5 s)}.
 * <p>
 * Tagging makes slow tests easy to exclude ({@code -DexcludedGroups=slow}); the
 * timeout stops a "slow" test from silently becoming a hanging one.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Test
@Tag("slow")
@Timeout(value = 5, unit = TimeUnit.SECONDS)
public @interface LongRunning {
}
