package com.example.auth;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Issues access tokens for the OAuth 2.0 client credentials grant, Module 6 production code.
 * <p>
 * Every collaborator arrives through the constructor, so tests can hand in
 * real implementations, hand-written fakes (Module 6.2) or Mockito mocks (Module 6.3).
 * <p>
 * Checks, in this order:
 * <ol>
 *   <li>the client exists and the secret matches, else {@code invalid_client}
 *       (one error for both, so callers cannot probe for client ids);</li>
 *   <li>the client is enabled, else {@code unauthorized_client};</li>
 *   <li>the requested scopes are allowed, else {@code invalid_scope}.
 *       No requested scopes means all allowed scopes.</li>
 * </ol>
 * Every outcome is written to the audit log.
 */
public final class ClientCredentialsService {

    public static final Duration TOKEN_LIFETIME = Duration.ofMinutes(5);

    private final ClientRepository clients;
    private final SecretVerifier secretVerifier;
    private final TokenGenerator tokenGenerator;
    private final AuditLog auditLog;
    private final Clock clock;

    public ClientCredentialsService(ClientRepository clients, SecretVerifier secretVerifier,
            TokenGenerator tokenGenerator, AuditLog auditLog, Clock clock) {
        this.clients = Objects.requireNonNull(clients, "clients");
        this.secretVerifier = Objects.requireNonNull(secretVerifier, "secretVerifier");
        this.tokenGenerator = Objects.requireNonNull(tokenGenerator, "tokenGenerator");
        this.auditLog = Objects.requireNonNull(auditLog, "auditLog");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AccessToken issue(String clientId, String clientSecret, Set<String> requestedScopes) {
        Objects.requireNonNull(clientId, "clientId");
        Objects.requireNonNull(clientSecret, "clientSecret");
        Objects.requireNonNull(requestedScopes, "requestedScopes");

        Client client = clients.findById(clientId).orElse(null);
        if (client == null || !secretVerifier.matches(clientSecret, client.secretHash())) {
            throw deny(clientId, TokenRequestException.Error.INVALID_CLIENT);
        }
        if (!client.enabled()) {
            throw deny(clientId, TokenRequestException.Error.UNAUTHORIZED_CLIENT);
        }
        Set<String> scopes = requestedScopes.isEmpty() ? client.allowedScopes() : Set.copyOf(requestedScopes);
        if (!client.allowedScopes().containsAll(scopes)) {
            throw deny(clientId, TokenRequestException.Error.INVALID_SCOPE);
        }

        Instant now = clock.instant();
        AccessToken token = new AccessToken(tokenGenerator.newTokenValue(), clientId, scopes, now.plus(TOKEN_LIFETIME));
        auditLog.append(new AuditEvent(AuditEvent.Type.TOKEN_ISSUED, clientId, now, String.join(" ", new TreeSet<>(scopes))));
        return token;
    }

    private TokenRequestException deny(String clientId, TokenRequestException.Error error) {
        auditLog.append(new AuditEvent(AuditEvent.Type.TOKEN_DENIED, clientId, clock.instant(), error.code()));
        return new TokenRequestException(error);
    }
}
