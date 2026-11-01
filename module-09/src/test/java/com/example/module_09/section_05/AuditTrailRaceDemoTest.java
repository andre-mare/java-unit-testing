package com.example.module_09.section_05;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.IntStream;

import com.example.audit.AuditTrail;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * Module 9.5 – failure demo: shared global state without a lock.
 * <p>
 * Excluded from normal builds by the {@code failure-demo} tag. With parallel
 * execution enabled, run
 * {@code mvn test -DexcludedGroups=none -Dtest=AuditTrailRaceDemoTest}.
 * Expected: some repetitions fail with a size other than 5, a different number
 * each run, because another repetition cleared or filled the trail in between.
 * That is what a flaky test looks like. With parallel execution off, all pass,
 * which is exactly why such bugs hide. The fix: {@link AuditTrailLockTest}.
 */
@Tag("failure-demo")
@Execution(ExecutionMode.CONCURRENT)
class AuditTrailRaceDemoTest {

    @RepeatedTest(20)
    void eachRepetitionSeesOnlyItsOwnEntries() throws InterruptedException {
        AuditTrail.clear();
        IntStream.range(0, 5).forEach(i -> AuditTrail.record("event " + i));
        Thread.sleep(2);
        assertEquals(5, AuditTrail.entries().size());
    }
}
