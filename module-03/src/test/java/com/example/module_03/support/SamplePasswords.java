package com.example.module_03.support;

/**
 * Module 3 test data: one example password per rule of the standard policy.
 * Shared by both Module 3 submodules.
 */
public final class SamplePasswords {

    /** 23 characters with upper, lower, digit and symbol: passes the standard policy. */
    public static final String VALID = "Correct-Horse-9-Battery";

    /** Exactly 12 characters, the standard minimum, with all four character types. */
    public static final String EXACTLY_12 = "Abcdefgh1!xy";

    /** Exactly 128 characters, the standard maximum. */
    public static final String EXACTLY_128 = "Aa1!".repeat(32);

    /** 129 characters, one over the standard maximum. */
    public static final String TOO_LONG = EXACTLY_128 + "x";

    public static final String TOO_SHORT = "Ab1!";
    public static final String NO_DIGIT = "Correct-Horse-Battery";
    public static final String NO_UPPERCASE = "correct-horse-9-battery";
    public static final String NO_LOWERCASE = "CORRECT-HORSE-9-BATTERY";
    public static final String NO_SYMBOL = "CorrectHorse9Battery";
    public static final String WITH_SPACE = "Correct Horse-9-Battery";

    /** Breaks four rules at once: too short, no uppercase, no digit, no symbol. */
    public static final String LETTERS_ONLY = "abc";

    private SamplePasswords() {
    }
}
