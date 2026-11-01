package com.example.auth;

import java.util.Objects;
import java.util.Set;

/**
 * A registered OAuth client, Module 6 production code.
 *
 * @param clientId      public identifier
 * @param secretHash    hash of the client secret; the raw secret is never stored
 * @param allowedScopes scopes this client may request
 * @param enabled       a disabled client cannot get tokens
 */
public record Client(String clientId, String secretHash, Set<String> allowedScopes, boolean enabled) {

    public Client {
        Objects.requireNonNull(clientId, "clientId");
        Objects.requireNonNull(secretHash, "secretHash");
        allowedScopes = Set.copyOf(allowedScopes);
    }

    /** A copy of this client with {@code enabled = false}. */
    public Client disabled() {
        return new Client(clientId, secretHash, allowedScopes, false);
    }
}
