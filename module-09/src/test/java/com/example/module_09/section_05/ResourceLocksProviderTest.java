package com.example.module_09.section_05;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.example.audit.AuditTrail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.ResourceLock;

/**
 * Module 9.5 – locks from a provider: writers are exclusive, readers share.
 * <p>
 * {@link AuditTrailLocks} gives the {@code reads...} methods a {@code READ} lock and
 * the others {@code READ_WRITE}. Two readers may run together; a writer runs alone.
 * Static locks ({@code value}) and provider locks can be combined on one annotation.
 */
@Execution(ExecutionMode.CONCURRENT)
@ResourceLock(providers = AuditTrailLocks.class)
class ResourceLocksProviderTest {

    @Test
    void replacesTheTrail() {
        AuditTrail.clear();
        AuditTrail.record("consent.revoke");
        assertEquals(1, AuditTrail.entries().size());
    }

    @Test
    void appendsTwice() {
        int before = AuditTrail.entries().size();
        AuditTrail.record("login");
        AuditTrail.record("logout");
        assertEquals(before + 2, AuditTrail.entries().size());
    }

    @Test
    void readsACopyThatDoesNotChange() {
        List<String> snapshot = AuditTrail.entries();
        assertEquals(snapshot, AuditTrail.entries(), "no writer runs while a reader holds the READ lock");
    }

    @Test
    void readsTheSameAgain() {
        assertEquals(AuditTrail.entries(), AuditTrail.entries());
    }
}
