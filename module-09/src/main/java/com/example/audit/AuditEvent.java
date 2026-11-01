package com.example.audit;

import java.time.Instant;
import java.util.Objects;

/**
 * One line in a security audit log, Module 9 production code.
 * <p>
 * Stored as {@code at|actor|action|OUTCOME}, for example
 * {@code 2026-11-02T09:00:00Z|alice|login|SUCCESS}. Actor and action must not
 * contain {@code |} or line breaks.
 *
 * @param at      when it happened (UTC)
 * @param actor   who did it: a user or client id
 * @param action  what was attempted, such as {@code login} or {@code consent.revoke}
 * @param outcome how it ended
 */
public record AuditEvent(Instant at, String actor, String action, Outcome outcome) {

    public enum Outcome { SUCCESS, DENIED, ERROR }

    public AuditEvent {
        Objects.requireNonNull(at, "at");
        Objects.requireNonNull(outcome, "outcome");
        requireStorable(actor, "actor");
        requireStorable(action, "action");
    }

    public String toLine() {
        return at + "|" + actor + "|" + action + "|" + outcome;
    }

    public static AuditEvent fromLine(String line) {
        String[] f = line.split("\\|", -1);
        if (f.length != 4) {
            throw new IllegalArgumentException("Not an audit line: " + line);
        }
        return new AuditEvent(Instant.parse(f[0]), f[1], f[2], Outcome.valueOf(f[3]));
    }

    private static void requireStorable(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank() || value.contains("|") || value.contains("\n") || value.contains("\r")) {
            throw new IllegalArgumentException(name + " must be non-blank, without '|' or line breaks: " + value);
        }
    }
}
