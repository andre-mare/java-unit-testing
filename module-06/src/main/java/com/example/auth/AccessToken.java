package com.example.auth;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * An issued access token.
 *
 * @param value     the opaque token value; treat as a secret
 * @param clientId  the client it was issued to
 * @param scopes    the granted scopes
 * @param expiresAt the first instant at which the token is no longer valid
 */
public record AccessToken(String value, String clientId, Set<String> scopes, Instant expiresAt) {

    public AccessToken {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(clientId, "clientId");
        scopes = Set.copyOf(scopes);
        Objects.requireNonNull(expiresAt, "expiresAt");
    }

    public boolean isExpiredAt(Instant now) {
        return !now.isBefore(expiresAt);
    }

    /** Never prints the token value. */
    @Override
    public String toString() {
        return "AccessToken[clientId=" + clientId + ", scopes=" + scopes + ", expiresAt=" + expiresAt + "]";
    }
}
