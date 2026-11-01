package com.example.tokens;

import java.time.Instant;
import java.util.Set;

/**
 * The registered claims of a JSON Web Token (RFC 7519) that the validator checks.
 * Any field may be missing ({@code null}); the validator reports what is wrong.
 *
 * @param subject   {@code sub}
 * @param issuer    {@code iss}
 * @param audience  {@code aud}; never null, possibly empty
 * @param notBefore {@code nbf}; optional
 * @param expiresAt {@code exp}; required
 */
public record Claims(String subject, String issuer, Set<String> audience, Instant notBefore, Instant expiresAt) {

    public Claims {
        audience = audience == null ? Set.of() : Set.copyOf(audience);
    }
}
