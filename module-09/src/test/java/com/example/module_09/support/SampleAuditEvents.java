package com.example.module_09.support;

import java.time.Instant;
import java.util.List;

import com.example.audit.AuditEvent;
import com.example.audit.AuditEvent.Outcome;

/** Module 9 shared test data: a few audit events from one morning. Not a test class. */
public final class SampleAuditEvents {

    public static final Instant MORNING = Instant.parse("2026-11-02T09:00:00Z");

    public static final AuditEvent LOGIN = new AuditEvent(MORNING, "alice", "login", Outcome.SUCCESS);
    public static final AuditEvent DENIED = new AuditEvent(MORNING.plusSeconds(5), "tpp-budget-app", "accounts.read", Outcome.DENIED);
    public static final AuditEvent REVOKE = new AuditEvent(MORNING.plusSeconds(60), "alice", "consent.revoke", Outcome.SUCCESS);

    public static final List<AuditEvent> ALL = List.of(LOGIN, DENIED, REVOKE);

    private SampleAuditEvents() {
    }
}
