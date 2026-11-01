package com.example.module_09.section_05;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.example.module_09.support.ParallelSettings;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * Module 9.5 – proof that tests really run at the same time.
 * <p>
 * Both tests wait until the other one has started. That only works if they run
 * on two threads at once, so it needs parallel execution enabled (see
 * {@code module9-parallel.properties}) and at least two worker threads. With
 * parallel execution off, both tests are skipped by the assumption.
 * <p>
 * {@code @Execution(CONCURRENT)} makes the methods concurrent even when
 * {@code mode.default} is {@code same_thread}; it has no effect while parallel
 * execution is disabled.
 */
@Execution(ExecutionMode.CONCURRENT)
class ParallelismProbeTest {

    private static final CountDownLatch bothStarted = new CountDownLatch(2);

    @BeforeAll
    static void onlyWhenParallel() {
        assumeTrue(ParallelSettings.isEnabled(), "parallel execution is disabled");
    }

    @Test
    void first() throws InterruptedException {
        meetTheOther();
    }

    @Test
    void second() throws InterruptedException {
        meetTheOther();
    }

    private static void meetTheOther() throws InterruptedException {
        bothStarted.countDown();
        assertTrue(bothStarted.await(10, TimeUnit.SECONDS),
            "the other test did not start: are the tests running in parallel, with two or more threads?");
    }
}
