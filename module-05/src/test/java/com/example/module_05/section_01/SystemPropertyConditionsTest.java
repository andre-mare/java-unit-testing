package com.example.module_05.section_01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import com.example.config.AppConfig;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/**
 * Module 5.1 – JVM system property conditions.
 * <p>
 * {@code matches} is a regular expression that must match the WHOLE value.
 * Try the class three ways:
 * <pre>
 * mvn test -Dtest=SystemPropertyConditionsTest
 * mvn test -Dtest=SystemPropertyConditionsTest -Dapp.region=eu-west-1
 * mvn test -Dtest=SystemPropertyConditionsTest -Dapp.region=eu-west-1 -Dapp.env=staging -Dapp.offline=true
 * </pre>
 * The properties reach the test JVM because Surefire passes Maven's {@code -D}
 * user properties on as system properties.
 */
class SystemPropertyConditionsTest {

    private final AppConfig config = AppConfig.fromSystem();

    @Test
    @EnabledIfSystemProperty(named = "os.arch", matches = ".*64.*")
    void on64BitArchitectures() {
        assertTrue(System.getProperty("os.arch").contains("64"));
    }

    @Test
    @EnabledIfSystemProperty(named = "app.region", matches = "eu-.*",
        disabledReason = "run with -Dapp.region=eu-west-1")
    void inEuRegions() {
        assertTrue(config.get("region").orElseThrow().startsWith("eu-"));
    }

    /** Repeatable: every condition must allow the test. */
    @Test
    @EnabledIfSystemProperty(named = "app.region", matches = "eu-.*")
    @EnabledIfSystemProperty(named = "app.env", matches = "staging|integration")
    void inEuStagingOrIntegration() {
        assertTrue(config.get("region").orElseThrow().startsWith("eu-"));
        assertTrue(Set.of("staging", "integration").contains(config.environmentName()));
    }

    @Test
    @DisabledIfSystemProperty(named = "app.offline", matches = "true",
        disabledReason = "needs network; skipped when -Dapp.offline=true")
    void notWhenOffline() {
        assertEquals("false", System.getProperty("app.offline", "false"));
    }
}
