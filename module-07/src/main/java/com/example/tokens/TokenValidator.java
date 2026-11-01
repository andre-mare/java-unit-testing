package com.example.tokens;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Checks token claims against an expected issuer and audience at the current time,
 * Module 7 production code.
 * <p>
 * Time comes from an injected {@link Clock}, so expiry can be tested without waiting.
 * {@code allowedClockSkew} tolerates small clock differences between issuer and
 * resource server: a token stays valid until {@code exp + skew}, and is accepted from
 * {@code nbf - skew}.
 */
public final class TokenValidator {

    private final String expectedIssuer;
    private final String expectedAudience;
    private final Clock clock;
    private final Duration allowedClockSkew;

    public TokenValidator(String expectedIssuer, String expectedAudience, Clock clock, Duration allowedClockSkew) {
        this.expectedIssuer = Objects.requireNonNull(expectedIssuer, "expectedIssuer");
        this.expectedAudience = Objects.requireNonNull(expectedAudience, "expectedAudience");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.allowedClockSkew = Objects.requireNonNull(allowedClockSkew, "allowedClockSkew");
        if (allowedClockSkew.isNegative()) {
            throw new IllegalArgumentException("allowedClockSkew must not be negative");
        }
    }

    /** Every problem with the claims; empty means valid. */
    public Set<ClaimProblem> validate(Claims claims) {
        Objects.requireNonNull(claims, "claims");
        Set<ClaimProblem> problems = EnumSet.noneOf(ClaimProblem.class);
        Instant now = clock.instant();

        if (claims.subject() == null || claims.subject().isBlank()) {
            problems.add(ClaimProblem.MISSING_SUBJECT);
        }
        if (!expectedIssuer.equals(claims.issuer())) {
            problems.add(ClaimProblem.WRONG_ISSUER);
        }
        if (!claims.audience().contains(expectedAudience)) {
            problems.add(ClaimProblem.WRONG_AUDIENCE);
        }
        if (claims.notBefore() != null && now.plus(allowedClockSkew).isBefore(claims.notBefore())) {
            problems.add(ClaimProblem.NOT_YET_VALID);
        }
        if (claims.expiresAt() == null || !now.minus(allowedClockSkew).isBefore(claims.expiresAt())) {
            problems.add(ClaimProblem.EXPIRED);
        }
        return problems;
    }

    public boolean isValid(Claims claims) {
        return validate(claims).isEmpty();
    }
}
