package com.example.module_09.section_05;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.IntStream;

import com.example.audit.AuditTrail;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.ResourceLock;

/**
 * Module 9.5 lab – the fix for {@link AuditTrailRaceDemoTest}: a resource lock.
 * <p>
 * Every test that uses the global {@link AuditTrail} declares
 * {@code @ResourceLock(AuditTrail.LOCK)}. Tests holding the same lock never run at
 * the same time; everything else still runs in parallel. The default mode is
 * {@code READ_WRITE} (exclusive); {@code mode = READ} lets readers share.
 * Here the lock is on the class, so it covers all 20 repetitions.
 */
@Execution(ExecutionMode.CONCURRENT)
@ResourceLock(AuditTrail.LOCK)
class AuditTrailLockTest {

    @RepeatedTest(20)
    void eachRepetitionSeesOnlyItsOwnEntries() throws InterruptedException {
        AuditTrail.clear();
        IntStream.range(0, 5).forEach(i -> AuditTrail.record("event " + i));
        Thread.sleep(2); // widen the window a concurrent test would need to interfere
        assertEquals(5, AuditTrail.entries().size());
    }
}
