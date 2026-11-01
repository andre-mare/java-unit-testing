package com.example.module_07.support;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.stream.Stream;

import com.example.tokens.ClaimProblem;
import com.example.tokens.Claims;
import com.example.tokens.TokenValidator;

import org.junit.jupiter.params.provider.Arguments;

/**
 * Module 7 test data for {@link TokenValidator}: a fixed "now", the expected
 * issuer and audience, and an external argument factory.
 * <p>
 * {@link #expiryScenarios()} is used from other classes with
 * {@code @MethodSource("com.example.m07_data_driven_tests.support.TokenScenarios#expiryScenarios")}.
 * External factory methods must be {@code static}.
 */
public final class TokenScenarios {

    public static final Instant NOW = Instant.parse("2026-03-01T10:00:00Z");
    public static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    public static final String ISSUER = "https://idp.example.com";
    public static final String AUDIENCE = "payments-api";
    public static final Duration SKEW = Duration.ofSeconds(30);

    private TokenScenarios() {
    }

    public static TokenValidator validator() {
        return new TokenValidator(ISSUER, AUDIENCE, CLOCK, SKEW);
    }

    /** Valid claims that expire {@code expiresIn} after {@link #NOW}. */
    public static Claims claimsExpiringIn(Duration expiresIn) {
        return new Claims("alice", ISSUER, Set.of(AUDIENCE), null, NOW.plus(expiresIn));
    }

    /** Arguments: (expires in, expected to be expired?). Boundaries around the 30 s skew. */
    public static Stream<Arguments> expiryScenarios() {
        return Stream.of(
            arguments(Duration.ofMinutes(5), false),
            arguments(Duration.ZERO, false),              // expired now, but within the skew
            arguments(Duration.ofSeconds(-29), false),
            arguments(Duration.ofSeconds(-30), true),     // exactly at exp + skew
            arguments(Duration.ofHours(-1), true));
    }

    /** Claims that each break exactly one rule, with the problem expected. */
    public static Stream<Arguments> singleProblems() {
        Instant later = NOW.plus(Duration.ofHours(1));
        return Stream.of(
            arguments(new Claims(null, ISSUER, Set.of(AUDIENCE), null, later), ClaimProblem.MISSING_SUBJECT),
            arguments(new Claims("alice", "https://evil.example", Set.of(AUDIENCE), null, later), ClaimProblem.WRONG_ISSUER),
            arguments(new Claims("alice", ISSUER, Set.of("other-api"), null, later), ClaimProblem.WRONG_AUDIENCE),
            arguments(new Claims("alice", ISSUER, Set.of(AUDIENCE), NOW.plus(Duration.ofMinutes(5)), later), ClaimProblem.NOT_YET_VALID),
            arguments(new Claims("alice", ISSUER, Set.of(AUDIENCE), null, null), ClaimProblem.EXPIRED));
    }
}
