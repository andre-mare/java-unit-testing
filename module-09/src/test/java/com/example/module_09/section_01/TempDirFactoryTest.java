package com.example.module_09.section_01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import com.example.audit.AuditArchiver;
import com.example.audit.AuditLogReader;
import com.example.audit.AuditLogWriter;
import com.example.module_09.support.SampleAuditEvents;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.io.TempDirDeletionStrategy;

/**
 * Module 9.1 – where the directory comes from, and how it is deleted.
 * <ul>
 *   <li>{@code factory}: a {@code TempDirFactory}; for the whole run set
 *       {@code junit.jupiter.tempdir.factory.default} to its class name;</li>
 *   <li>{@code deletionStrategy} (new in 6.1, experimental): {@code Standard}
 *       fails the test when a file cannot be deleted; {@code IgnoreFailures} only
 *       logs a warning (handy on Windows, where open files cannot be deleted); for
 *       the whole run set {@code junit.jupiter.tempdir.deletion.strategy.default}.</li>
 * </ul>
 * The production code only uses {@code Path}, so it runs unchanged on an
 * in-memory file system.
 */
class TempDirFactoryTest {

    @Test
    void customFactory(@TempDir(factory = PrefixedTempDirFactory.class) Path directory) {
        assertTrue(directory.getFileName().toString().startsWith("junit-TempDirFactoryTest-"));
    }

    @Test
    void ignoreDeletionFailures(
            @TempDir(deletionStrategy = TempDirDeletionStrategy.IgnoreFailures.class) Path directory) {
        assertTrue(Files.isDirectory(directory));
    }

    @Test
    void inMemoryWithJimfs(@JimfsTempDir Path logs, @JimfsTempDir Path archive) throws IOException {
        assertNotSame(FileSystems.getDefault(), logs.getFileSystem(), "not on the disk");

        Path file = logs.resolve("audit.log");
        try (AuditLogWriter writer = new AuditLogWriter(file)) {
            SampleAuditEvents.ALL.forEach(writer::write);
        }
        assertEquals(SampleAuditEvents.ALL, AuditLogReader.events(file));

        Path archived = AuditArchiver.archive(file, logs.resolve("archive"), LocalDate.of(2026, 11, 2));
        assertEquals("/junit/archive/audit-2026-11-02.log", archived.toString());
        assertNotSame(logs.getFileSystem(), archive.getFileSystem(), "each declaration gets its own in-memory file system");
    }
}
