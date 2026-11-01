package com.example.module_05.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

/**
 * Module 5.1 – a composed annotation: a {@code @Test} that only runs on Linux or macOS.
 * Any conditional annotation can be used as a meta-annotation like this.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Test
@EnabledOnOs(value = { OS.LINUX, OS.MAC }, disabledReason = "Unix-only behaviour")
public @interface UnixOnly {
}
