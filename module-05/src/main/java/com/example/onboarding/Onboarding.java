package com.example.onboarding;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A user sign-up flow whose steps must happen in order, Module 5 production code.
 * <pre>
 * STARTED --register--> REGISTERED --sendVerificationCode--> CODE_SENT --verify--> VERIFIED --activate--> ACTIVE
 * </pre>
 * Calling a step out of order throws {@link IllegalStateException}. That makes it
 * a natural subject for an ordered scenario test (Module 5.3), where each test
 * method performs one step on a shared instance.
 */
public final class Onboarding {

    public enum Step { STARTED, REGISTERED, CODE_SENT, VERIFIED, ACTIVE }

    private static final SecureRandom RANDOM = new SecureRandom();

    private final Supplier<String> codeGenerator;
    private Step step = Step.STARTED;
    private String email;
    private String expectedCode;

    /** Uses random six-digit verification codes. */
    public Onboarding() {
        this(() -> "%06d".formatted(RANDOM.nextInt(1_000_000)));
    }

    /** Uses the given code generator; tests can make codes predictable. */
    public Onboarding(Supplier<String> codeGenerator) {
        this.codeGenerator = Objects.requireNonNull(codeGenerator, "codeGenerator");
    }

    public Step step() {
        return step;
    }

    public String email() {
        return email;
    }

    public void register(String email) {
        require(Step.STARTED);
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Not a valid e-mail address: " + email);
        }
        this.email = email;
        step = Step.REGISTERED;
    }

    /** Generates and "sends" a code; returns it so tests can use it. */
    public String sendVerificationCode() {
        require(Step.REGISTERED);
        expectedCode = codeGenerator.get();
        step = Step.CODE_SENT;
        return expectedCode;
    }

    public void verify(String code) {
        require(Step.CODE_SENT);
        if (!expectedCode.equals(code)) {
            throw new IllegalArgumentException("Wrong verification code");
        }
        step = Step.VERIFIED;
    }

    public void activate() {
        require(Step.VERIFIED);
        step = Step.ACTIVE;
    }

    private void require(Step expected) {
        if (step != expected) {
            throw new IllegalStateException("Expected step " + expected + " but was " + step);
        }
    }
}
