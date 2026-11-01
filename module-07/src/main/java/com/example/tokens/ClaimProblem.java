package com.example.tokens;

/** A reason why a token's claims are not acceptable. */
public enum ClaimProblem {
    MISSING_SUBJECT,
    WRONG_ISSUER,
    WRONG_AUDIENCE,
    NOT_YET_VALID,
    EXPIRED
}
