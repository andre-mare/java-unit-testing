package com.example.module_05.section_01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.config.AppConfig;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/**
 * Module 5.1 – environment variable conditions.
 * <p>
 * Environment variables come from the operating system, so set them in the
 * shell, not with {@code -D}:
 * <pre>
 * macOS/Linux:  APP_ENV=integration mvn test -Dtest=EnvironmentVariableConditionsTest
 * Windows cmd:  set APP_ENV=integration &amp;&amp; mvn test -Dtest=EnvironmentVariableConditionsTest
 * PowerShell:   $env:APP_ENV="integration"; mvn test -Dtest=EnvironmentVariableConditionsTest
 * </pre>
 * Most CI services set {@code CI=true}, so the last test is skipped on CI.
 */
class EnvironmentVariableConditionsTest {

    private final AppConfig config = AppConfig.fromSystem();

    @Test
    @EnabledIfEnvironmentVariable(named = "PATH", matches = ".+")
    void whenPathIsSet() {
        assertNotNull(System.getenv("PATH"));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "APP_ENV", matches = "integration",
        disabledReason = "set APP_ENV=integration")
    void onlyInIntegrationEnvironment() {
        assertEquals("integration", System.getenv("APP_ENV"));
        assertEquals("integration", AppConfig.fromSystem().environmentName()); // unless -Dapp.env overrides it
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "CI", matches = "true", disabledReason = "developer-only check")
    void notOnCiServers() {
        assertNotEquals("true", System.getenv("CI"));
        assertNotNull(config.environmentName());
    }
}
