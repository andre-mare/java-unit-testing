package com.example.module_04.section_03;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Module 4.3 helper – a minimal extension that prints how long each test took.
 * <p>
 * Used only to show that {@code @ExtendWith} on an interface is inherited.
 * Writing extensions is covered in Module 6; read this one as a black box for now.
 * <p>
 * The start time is kept in a {@link ThreadLocal}: JUnit runs these callbacks
 * and the test method on the same thread.
 */
public class ExecutionTimeExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final ThreadLocal<Long> START = new ThreadLocal<>();

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        START.set(System.nanoTime());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        long micros = (System.nanoTime() - START.get()) / 1_000;
        START.remove();
        System.out.printf("[timing] %s took %d µs%n", context.getDisplayName(), micros);
    }

    /** True while a test that uses this extension is running on the current thread. */
    public static boolean isTimingCurrentTest() {
        return START.get() != null;
    }
}
