package com.example.module_05.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/**
 * Module 5.1 lab – a composed annotation for integration tests.
 * <p>
 * One annotation = three: it is a {@code @Test}, it is tagged {@code integration}
 * (Module 5.2), and it only runs when the environment variable
 * {@code APP_ENV} is {@code integration}. Run them with:
 * <pre>
 * APP_ENV=integration mvn test -Dgroups=integration     (Windows: set APP_ENV=integration)
 * </pre>
 * Named {@code @Integration}, not {@code @IntegrationTest}, so it does not match
 * Surefire's {@code *Test} pattern.
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Test
@Tag("integration")
@EnabledIfEnvironmentVariable(named = "APP_ENV", matches = "integration",
    disabledReason = "integration tests need APP_ENV=integration")
public @interface Integration {
}
