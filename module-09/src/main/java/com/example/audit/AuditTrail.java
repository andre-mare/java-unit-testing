package com.example.audit;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A process-wide, in-memory trail of security events, Module 9 production code.
 * <p>
 * Global state on purpose: it is thread-safe itself, but two tests that each
 * {@link #clear()} it and then count entries will disturb each other when they
 * run in parallel. Module 9.5 fixes that with {@code @ResourceLock(AuditTrail.LOCK)}.
 */
public final class AuditTrail {

    /** Resource-lock key for tests that use the trail. */
    public static final String LOCK = "com.example.audit.AuditTrail";

    private static final List<String> ENTRIES = new CopyOnWriteArrayList<>();

    private AuditTrail() {
    }

    public static void record(String entry) {
        ENTRIES.add(entry);
    }

    public static List<String> entries() {
        return List.copyOf(ENTRIES);
    }

    public static void clear() {
        ENTRIES.clear();
    }
}
