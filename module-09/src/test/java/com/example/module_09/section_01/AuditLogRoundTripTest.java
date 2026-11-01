package com.example.module_09.section_01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.example.audit.AuditArchiver;
import com.example.audit.AuditEvent;
import com.example.audit.AuditEvent.Outcome;
import com.example.audit.AuditLogReader;
import com.example.audit.AuditLogWriter;
import com.example.module_09.support.SampleAuditEvents;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Module 9.1 lab – write an audit log, check it with {@code assertLinesMatch}, archive it.
 * <p>
 * {@code assertLinesMatch(expected, actual)} compares line by line. Each expected
 * line is first compared with {@code equals}, then used as a regular expression,
 * and {@code ">> ... >>"} skips any number of lines (or {@code ">> 2 >>"}, exactly
 * two). Good for logs with timestamps you don't want to spell out.
 */
class AuditLogRoundTripTest {

    @TempDir
    Path logs;

    @Test
    void linesMatchExactlyAndByPattern() throws IOException {
        Path file = logs.resolve("audit.log");
        try (AuditLogWriter writer = new AuditLogWriter(file)) {
            SampleAuditEvents.ALL.forEach(writer::write);
            writer.write(new AuditEvent(Instant.now(), "batch-job", "report.export", Outcome.ERROR));
        }

        assertLinesMatch(List.of(
            "2026-11-02T09:00:00Z|alice|login|SUCCESS",               // exact
            "\\S+\\|tpp-budget-app\\|accounts\\.read\\|DENIED",        // regular expression
            ">> 1 >>",                                                 // skip exactly one line
            "\\d{4}-\\d{2}-\\d{2}T.*Z\\|batch-job\\|report\\.export\\|ERROR"), AuditLogReader.lines(file));
    }

    @Test
    void eventsSurviveTheRoundTrip() throws IOException {
        Path file = logs.resolve("audit.log");
        try (AuditLogWriter writer = new AuditLogWriter(file)) {
            SampleAuditEvents.ALL.forEach(writer::write);
        }
        try (AuditLogWriter again = new AuditLogWriter(file)) {
            again.write(SampleAuditEvents.LOGIN); // appends, never truncates
        }
        List<AuditEvent> events = AuditLogReader.events(file);
        assertEquals(4, events.size());
        assertEquals(SampleAuditEvents.ALL, events.subList(0, 3));
    }

    @Test
    void archivingMovesTheFileWithoutOverwriting(@TempDir Path archive) throws IOException {
        LocalDate day = LocalDate.of(2026, 11, 2);
        Path first = logs.resolve("first.log");
        Path second = logs.resolve("second.log");
        Files.writeString(first, SampleAuditEvents.LOGIN.toLine());
        Files.writeString(second, SampleAuditEvents.REVOKE.toLine());

        Path archivedFirst = AuditArchiver.archive(first, archive, day);
        Path archivedSecond = AuditArchiver.archive(second, archive, day);

        assertEquals("audit-2026-11-02.log", archivedFirst.getFileName().toString());
        assertEquals("audit-2026-11-02-1.log", archivedSecond.getFileName().toString());
        assertFalse(Files.exists(first));
        assertTrue(Files.exists(archivedSecond));
    }

    @Test
    void archivingAMissingFileFails(@TempDir Path archive) {
        assertThrows(NoSuchFileException.class,
            () -> AuditArchiver.archive(logs.resolve("missing.log"), archive, LocalDate.of(2026, 11, 2)));
    }

    @Test
    void writingAfterCloseFails() {
        AuditLogWriter writer = new AuditLogWriter(logs.resolve("audit.log"));
        writer.close();
        writer.close(); // twice is fine
        assertThrows(IllegalStateException.class, () -> writer.write(SampleAuditEvents.LOGIN));
    }
}
