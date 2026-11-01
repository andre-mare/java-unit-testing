package com.example.password;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * A configurable password policy, the production class under test in Module 3.
 * <p>
 * Each rule reads naturally as a sentence ("a password is rejected when it has
 * no digit"), which makes it a good subject for display names. The breach check
 * is deliberately not implemented yet, which gives Module 3.2 a realistic
 * reason to disable a test.
 * <p>
 * Length is counted in Unicode code points, so an emoji counts as one character.
 */
public final class PasswordPolicy {

    private final int minLength;
    private final int maxLength;
    private final boolean requireUppercase;
    private final boolean requireLowercase;
    private final boolean requireDigit;
    private final boolean requireSymbol;
    private final boolean allowWhitespace;

    private PasswordPolicy(Builder builder) {
        this.minLength = builder.minLength;
        this.maxLength = builder.maxLength;
        this.requireUppercase = builder.requireUppercase;
        this.requireLowercase = builder.requireLowercase;
        this.requireDigit = builder.requireDigit;
        this.requireSymbol = builder.requireSymbol;
        this.allowWhitespace = builder.allowWhitespace;
    }

    /**
     * The standard policy: 12 to 128 characters, at least one uppercase letter,
     * lowercase letter, digit and symbol, and no whitespace.
     */
    public static PasswordPolicy standard() {
        return builder()
            .minLength(12)
            .maxLength(128)
            .requireUppercase()
            .requireLowercase()
            .requireDigit()
            .requireSymbol()
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public int minLength() {
        return minLength;
    }

    public int maxLength() {
        return maxLength;
    }

    /** Checks every rule and returns all violations, not just the first one. */
    public ValidationResult validate(CharSequence password) {
        Objects.requireNonNull(password, "password");
        Set<Violation> violations = EnumSet.noneOf(Violation.class);

        long length = password.codePoints().count();
        if (length < minLength) {
            violations.add(Violation.TOO_SHORT);
        }
        if (length > maxLength) {
            violations.add(Violation.TOO_LONG);
        }
        if (requireUppercase && password.codePoints().noneMatch(Character::isUpperCase)) {
            violations.add(Violation.MISSING_UPPERCASE);
        }
        if (requireLowercase && password.codePoints().noneMatch(Character::isLowerCase)) {
            violations.add(Violation.MISSING_LOWERCASE);
        }
        if (requireDigit && password.codePoints().noneMatch(Character::isDigit)) {
            violations.add(Violation.MISSING_DIGIT);
        }
        if (requireSymbol && password.codePoints().noneMatch(PasswordPolicy::isSymbol)) {
            violations.add(Violation.MISSING_SYMBOL);
        }
        if (!allowWhitespace && password.codePoints().anyMatch(Character::isWhitespace)) {
            violations.add(Violation.CONTAINS_WHITESPACE);
        }
        return new ValidationResult(violations);
    }

    /**
     * Checks the password against a list of known breached passwords.
     *
     * @throws UnsupportedOperationException always: not implemented yet (ticket SEC-142)
     */
    public boolean isKnownBreached(CharSequence password) {
        throw new UnsupportedOperationException("Breach check is not implemented yet (SEC-142)");
    }

    private static boolean isSymbol(int codePoint) {
        return !Character.isLetterOrDigit(codePoint) && !Character.isWhitespace(codePoint);
    }

    /** Builds a policy; every rule is off until you switch it on. */
    public static final class Builder {

        private int minLength = 1;
        private int maxLength = Integer.MAX_VALUE;
        private boolean requireUppercase;
        private boolean requireLowercase;
        private boolean requireDigit;
        private boolean requireSymbol;
        private boolean allowWhitespace;

        private Builder() {
        }

        public Builder minLength(int minLength) {
            this.minLength = minLength;
            return this;
        }

        public Builder maxLength(int maxLength) {
            this.maxLength = maxLength;
            return this;
        }

        public Builder requireUppercase() {
            this.requireUppercase = true;
            return this;
        }

        public Builder requireLowercase() {
            this.requireLowercase = true;
            return this;
        }

        public Builder requireDigit() {
            this.requireDigit = true;
            return this;
        }

        public Builder requireSymbol() {
            this.requireSymbol = true;
            return this;
        }

        public Builder allowWhitespace() {
            this.allowWhitespace = true;
            return this;
        }

        public PasswordPolicy build() {
            if (minLength < 1) {
                throw new IllegalArgumentException("minLength must be at least 1: " + minLength);
            }
            if (maxLength < minLength) {
                throw new IllegalArgumentException(
                    "maxLength (" + maxLength + ") must not be smaller than minLength (" + minLength + ")");
            }
            return new PasswordPolicy(this);
        }
    }
}
