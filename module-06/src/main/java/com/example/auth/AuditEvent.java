package com.example.auth;

import java.time.Instant;
import java.util.Objects;

/**
 * Something security-relevant that happened.
 *
 * @param type     what happened
 * @param clientId the client involved
 * @param at       when it happened
 * @param detail   granted scopes for {@code TOKEN_ISSUED}, the error code for {@code TOKEN_DENIED}
 */
public record AuditEvent(Type type, String clientId, Instant at, String detail) {

    public enum Type { TOKEN_ISSUED, TOKEN_DENIED }

    public AuditEvent {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(at, "at");
    }
}
