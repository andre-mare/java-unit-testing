package com.example.module_05.section_01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import com.example.config.AppConfig;
import com.example.module_05.support.Integration;
import com.example.module_05.support.UnixOnly;

import org.junit.jupiter.api.Test;

/**
 * Module 5.1 lab – the composed {@code @Integration} and {@code @UnixOnly} annotations.
 * <p>
 * The plain {@code @Test} methods always run: they test {@code AppConfig} with
 * fixed values instead of the real environment, which needs no conditions at all.
 * The {@code @Integration} tests only run with {@code APP_ENV=integration}:
 * <pre>
 * APP_ENV=integration APP_REGION=eu-west-1 mvn test -Dtest=AppConfigIntegrationTest
 * </pre>
 * Rule of thumb: inject the environment when you can (always runs, fully
 * controlled); use conditions when you must test the real thing.
 */
class AppConfigIntegrationTest {

    @Test
    void systemPropertyWinsOverEnvironmentVariable() {
        AppConfig config = new AppConfig(
            Map.of("app.region", "eu-west-1")::get,
            Map.of("APP_REGION", "us-east-1")::get);
        assertEquals("eu-west-1", config.get("region").orElseThrow());
    }

    @Test
    void fallsBackToTheEnvironmentVariable() {
        AppConfig config = new AppConfig(key -> null, Map.of("APP_DB_URL", "jdbc:h2:mem:test")::get);
        assertEquals("jdbc:h2:mem:test", config.get("db.url").orElseThrow());
    }

    @Test
    void defaultsToLocal() {
        assertEquals("local", new AppConfig(key -> null, key -> null).environmentName());
    }

    @Integration
    void readsTheRealIntegrationEnvironment() {
        assertTrue(AppConfig.fromSystem().isIntegration());
    }

    @Integration
    void hasARegionInIntegration() {
        assertTrue(AppConfig.fromSystem().get("region").isPresent(), "set APP_REGION as well");
    }

    @UnixOnly
    void readsHomeFromTheRealEnvironment() {
        assertTrue(System.getenv("HOME").startsWith("/"));
    }
}
