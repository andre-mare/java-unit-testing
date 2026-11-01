package com.example.consent;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * An account holder's permission for a third-party provider (TPP) to access their
 * accounts, Module 8 production code. Immutable: state changes return a new copy.
 *
 * @param id          unique id
 * @param clientId    the TPP's OAuth client id
 * @param psuId       the account holder (payment service user)
 * @param permissions what the TPP may do, such as {@code ReadBalances}
 * @param expiresAt   end of validity
 * @param status      current status
 */
public record Consent(String id, String clientId, String psuId, Set<String> permissions,
        Instant expiresAt, ConsentStatus status) {

    public Consent {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(clientId, "clientId");
        Objects.requireNonNull(psuId, "psuId");
        Objects.requireNonNull(expiresAt, "expiresAt");
        Objects.requireNonNull(status, "status");
        permissions = Set.copyOf(permissions);
        if (permissions.isEmpty()) {
            throw new IllegalArgumentException("A consent needs at least one permission");
        }
    }

    public Consent withStatus(ConsentStatus newStatus) {
        return new Consent(id, clientId, psuId, permissions, expiresAt, newStatus);
    }

    /** Authorised and not yet expired at {@code now}. */
    public boolean isActiveAt(Instant now) {
        return status == ConsentStatus.AUTHORISED && now.isBefore(expiresAt);
    }
}
