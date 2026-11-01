package com.example.module_01.section_07.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Module 1.7 lab deliverable – the course-wide marker for a plain unit test.
 * Combines {@code @Test} with {@code @Tag("unit")}. Use it from Module 2 onwards
 * so that {@code mvn test -Dgroups=unit} runs only unit tests.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Tag("unit")
@Test
public @interface UnitTest {
}
