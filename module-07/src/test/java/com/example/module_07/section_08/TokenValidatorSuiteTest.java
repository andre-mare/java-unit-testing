package com.example.module_07.section_08;

import static com.example.module_07.support.TokenScenarios.CLOCK;
import static com.example.module_07.support.TokenScenarios.NOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import com.example.tokens.ClaimProblem;
import com.example.tokens.Claims;
import com.example.tokens.TokenValidator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Module 7.8 lab – one test suite, several configurations, written as a record.
 * <p>
 * A record's components are its constructor parameters, so a {@code record} test
 * class gets constructor injection without writing a constructor. Every
 * {@code @Test} runs once per CSV row: the same rules must hold for every
 * identity provider the payments platform trusts.
 * <p>
 * Constructor injection needs the default {@code PER_METHOD} lifecycle.
 */
@ParameterizedClass(name = "[{index}] {0} for {1}, skew {2} s")
@CsvSource({
    "https://idp.example.com,       payments-api, 30",
    "https://login.partner.example, payments-api, 0",
    "https://idp.example.com,       reporting,    120"
})
record TokenValidatorSuiteTest(String issuer, String audience, long skewSeconds) {

    private TokenValidator validator() {
        return new TokenValidator(issuer, audience, CLOCK, Duration.ofSeconds(skewSeconds));
    }

    private Claims claims(String claimIssuer, String claimAudience, Instant expiresAt) {
        return new Claims("alice", claimIssuer, Set.of(claimAudience), null, expiresAt);
    }

    @Test
    void acceptsMatchingClaims() {
        assertTrue(validator().isValid(claims(issuer, audience, NOW.plus(Duration.ofHours(1)))));
    }

    @Test
    void rejectsAnotherIssuer() {
        Set<ClaimProblem> problems = validator().validate(claims("https://evil.example", audience, NOW.plusSeconds(60)));
        assertEquals(Set.of(ClaimProblem.WRONG_ISSUER), problems);
    }

    @Test
    void rejectsAnotherAudience() {
        Set<ClaimProblem> problems = validator().validate(claims(issuer, "admin-api", NOW.plusSeconds(60)));
        assertEquals(Set.of(ClaimProblem.WRONG_AUDIENCE), problems);
    }

    @Test
    void expiryHonoursTheConfiguredSkew() {
        Instant justInsideSkew = NOW.minusSeconds(skewSeconds).plusSeconds(1);
        Instant atSkew = NOW.minusSeconds(skewSeconds);
        assertTrue(validator().isValid(claims(issuer, audience, justInsideSkew)));
        assertEquals(Set.of(ClaimProblem.EXPIRED), validator().validate(claims(issuer, audience, atSkew)));
    }
}
