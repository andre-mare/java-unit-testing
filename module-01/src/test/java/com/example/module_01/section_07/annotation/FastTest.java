package com.example.module_01.section_07.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Module 1.7 – a composed annotation that is both {@code @Test} and {@code @Tag("fast")}.
 * A method annotated with {@code @FastTest} needs no separate {@code @Test}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Tag("fast")
@Test
public @interface FastTest {
}
