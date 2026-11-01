package com.example.password;

/** A rule of a {@link PasswordPolicy} that a password breaks. */
public enum Violation {

    TOO_SHORT("is shorter than the minimum length"),
    TOO_LONG("is longer than the maximum length"),
    MISSING_UPPERCASE("has no uppercase letter"),
    MISSING_LOWERCASE("has no lowercase letter"),
    MISSING_DIGIT("has no digit"),
    MISSING_SYMBOL("has no symbol"),
    CONTAINS_WHITESPACE("contains whitespace");

    private final String description;

    Violation(String description) {
        this.description = description;
    }

    /** Human-readable reason, for example "has no digit". */
    public String description() {
        return description;
    }
}
