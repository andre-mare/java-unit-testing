package com.example.config;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Application settings from the environment, Module 5 production code.
 * <p>
 * A key such as {@code "region"} is looked up in this order:
 * <ol>
 *   <li>JVM system property {@code app.region} ({@code -Dapp.region=eu-west-1})</li>
 *   <li>environment variable {@code APP_REGION}</li>
 * </ol>
 * Because its behaviour depends on where it runs, it pairs naturally with
 * JUnit's conditional execution (Module 5.1). The two lookups are injectable,
 * so tests can also supply fixed values instead of the real environment.
 */
public final class AppConfig {

    private final UnaryOperator<String> systemProperties;
    private final UnaryOperator<String> environment;

    public AppConfig(UnaryOperator<String> systemProperties, UnaryOperator<String> environment) {
        this.systemProperties = Objects.requireNonNull(systemProperties, "systemProperties");
        this.environment = Objects.requireNonNull(environment, "environment");
    }

    /** Reads the real JVM system properties and environment variables. */
    public static AppConfig fromSystem() {
        return new AppConfig(System::getProperty, System::getenv);
    }

    /** The value for {@code key}, or empty if neither source sets a non-blank value. */
    public Optional<String> get(String key) {
        Objects.requireNonNull(key, "key");
        String value = systemProperties.apply(propertyName(key));
        if (value == null || value.isBlank()) {
            value = environment.apply(environmentVariableName(key));
        }
        return Optional.ofNullable(value).filter(v -> !v.isBlank());
    }

    public String getOrDefault(String key, String defaultValue) {
        return get(key).orElse(defaultValue);
    }

    /** Deployment environment from key {@code env}; {@code "local"} when unset. */
    public String environmentName() {
        return getOrDefault("env", "local");
    }

    public boolean isIntegration() {
        return "integration".equals(environmentName());
    }

    /** {@code "region"} becomes {@code "app.region"}. */
    public static String propertyName(String key) {
        return "app." + key;
    }

    /** {@code "db.url"} becomes {@code "APP_DB_URL"}. */
    public static String environmentVariableName(String key) {
        return "APP_" + key.toUpperCase(Locale.ROOT).replace('.', '_').replace('-', '_');
    }
}
