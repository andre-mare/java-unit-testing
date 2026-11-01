package com.example.module_06.support;

import java.util.ArrayList;
import java.util.List;

import com.example.auth.AuditEvent;
import com.example.auth.AuditLog;

/**
 * A hand-written fake {@link AuditLog} that remembers every event.
 * <p>
 * Tests check its contents afterwards (state-based testing). Compare with a
 * Mockito mock in Module 6.3, where you verify the call instead.
 */
public final class RecordingAuditLog implements AuditLog {

    private final List<AuditEvent> events = new ArrayList<>();

    @Override
    public void append(AuditEvent event) {
        events.add(event);
    }

    public List<AuditEvent> events() {
        return List.copyOf(events);
    }

    public AuditEvent last() {
        return events.get(events.size() - 1);
    }
}
