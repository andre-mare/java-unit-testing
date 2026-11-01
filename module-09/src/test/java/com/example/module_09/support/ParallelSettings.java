package com.example.module_09.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Module 9 test helper: is parallel execution switched on for this run?
 * <p>
 * Reads {@code junit.jupiter.execution.parallel.enabled} the way the platform
 * does for the two common places: a JVM system property (for example
 * {@code mvn test -Djunit.jupiter.execution.parallel.enabled=true}) wins over
 * {@code junit-platform.properties} on the test classpath. Not a test class.
 */
public final class ParallelSettings {

    public static final String ENABLED = "junit.jupiter.execution.parallel.enabled";

    private ParallelSettings() {
    }

    public static boolean isEnabled() {
        String fromSystem = System.getProperty(ENABLED);
        if (fromSystem != null) {
            return Boolean.parseBoolean(fromSystem.trim());
        }
        Properties properties = new Properties();
        try (InputStream in = ParallelSettings.class.getResourceAsStream("/junit-platform.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return Boolean.parseBoolean(properties.getProperty(ENABLED, "false").trim());
    }
}
