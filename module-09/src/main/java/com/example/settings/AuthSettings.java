package com.example.settings;

import java.time.Duration;
import java.util.Objects;

/**
 * Which identity provider to trust, read from JVM system properties, Module 9 production code.
 * <ul>
 *   <li>{@value #ISSUER} (required): the expected {@code iss}, an {@code https://} URL;</li>
 *   <li>{@value #AUDIENCE} (default {@value #DEFAULT_AUDIENCE}): the expected {@code aud};</li>
 *   <li>{@value #CLOCK_SKEW} (default 30): allowed clock skew in seconds, 0 to 300.</li>
 * </ul>
 * Start the JVM with {@code -Dauth.issuer=https://idp.example.nl} to switch issuer
 * without a rebuild. Global JVM state, so tests use {@code @SetSystemProperty}.
 *
 * @param issuer    expected issuer
 * @param audience  expected audience
 * @param clockSkew allowed clock skew
 */
public record AuthSettings(String issuer, String audience, Duration clockSkew) {

    public static final String ISSUER = "auth.issuer";
    public static final String AUDIENCE = "auth.audience";
    public static final String CLOCK_SKEW = "auth.clock-skew-seconds";
    public static final String DEFAULT_AUDIENCE = "payments-api";

    public AuthSettings {
        Objects.requireNonNull(issuer, "issuer");
        Objects.requireNonNull(audience, "audience");
        Objects.requireNonNull(clockSkew, "clockSkew");
        if (!issuer.startsWith("https://")) {
            throw new IllegalArgumentException("Issuer must be an https URL: " + issuer);
        }
        if (clockSkew.isNegative() || clockSkew.compareTo(Duration.ofMinutes(5)) > 0) {
            throw new IllegalArgumentException("Clock skew must be 0 to 300 seconds: " + clockSkew.toSeconds());
        }
    }

    public static AuthSettings fromSystemProperties() {
        String issuer = System.getProperty(ISSUER);
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalStateException("System property " + ISSUER + " is not set");
        }
        String audience = System.getProperty(AUDIENCE, DEFAULT_AUDIENCE);
        String skew = System.getProperty(CLOCK_SKEW, "30");
        long seconds;
        try {
            seconds = Long.parseLong(skew.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(CLOCK_SKEW + " must be a number of seconds: " + skew, e);
        }
        return new AuthSettings(issuer.trim(), audience.trim(), Duration.ofSeconds(seconds));
    }

    /** Exact, case-sensitive match, as for the {@code iss} claim. */
    public boolean acceptsIssuer(String claimedIssuer) {
        return issuer.equals(claimedIssuer);
    }
}
