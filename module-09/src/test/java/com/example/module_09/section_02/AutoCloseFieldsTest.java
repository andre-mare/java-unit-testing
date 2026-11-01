package com.example.module_09.section_02;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.audit.AuditEvent;
import com.example.audit.AuditLogWriter;
import com.example.audit.AuditShipper;
import com.example.module_09.support.SampleAuditEvents;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * Module 9.2 – {@code @AutoClose}: JUnit closes the field for you.
 * <ul>
 *   <li>An <em>instance</em> field is closed after each test (default
 *       {@code PER_METHOD} lifecycle: one instance per test), after {@code @AfterEach}.</li>
 *   <li>A {@code static} field is closed once, after {@code @AfterAll}.</li>
 *   <li>By default the method {@code close()} is called; name another with
 *       {@code @AutoClose("shutdown")} (see {@link AutoCloseCustomMethodTest}).</li>
 * </ul>
 * The field must be initialized by the time JUnit closes it, here by its initializer.
 * {@code @AfterAll} proves the timing: every instance writer is already closed, the
 * static shipper is not yet.
 */
@Execution(ExecutionMode.SAME_THREAD)
class AutoCloseFieldsTest {

    @TempDir
    static Path logs;

    static final List<AuditEvent> shipped = new CopyOnWriteArrayList<>();

    @AutoClose("shutdown")
    static final AuditShipper shipper = new AuditShipper(shipped::add);

    private static final List<AuditLogWriter> writersSeen = new CopyOnWriteArrayList<>();
    private static int nextFile;

    @AutoClose
    final AuditLogWriter writer = new AuditLogWriter(logs.resolve("audit-" + (++nextFile) + ".log"));

    @Test
    void firstTestGetsItsOwnWriter() {
        writersSeen.add(writer);
        writer.write(SampleAuditEvents.LOGIN);
        shipper.submit(SampleAuditEvents.LOGIN);
        assertEquals(1, writer.written());
    }

    @Test
    void secondTestGetsAnotherWriter() {
        writersSeen.add(writer);
        writer.write(SampleAuditEvents.REVOKE);
        shipper.submit(SampleAuditEvents.REVOKE);
        assertEquals(1, writer.written());
    }

    @AfterAll
    static void instanceFieldsAreClosedStaticFieldsNotYet() {
        assertEquals(2, writersSeen.size());
        assertTrue(writersSeen.stream().allMatch(AuditLogWriter::isClosed), "closed after each test");
        assertFalse(shipper.isShutdown(), "a static @AutoClose field is closed after @AfterAll");
    }
}
