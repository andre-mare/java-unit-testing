package com.example.module_09.section_01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.example.audit.AuditLogReader;
import com.example.audit.AuditLogWriter;
import com.example.module_09.support.SampleAuditEvents;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Module 9.1 – {@code @TempDir}: a fresh, empty directory that JUnit deletes afterwards.
 * <p>
 * Inject it into a field, a lifecycle or test method parameter, or a constructor
 * parameter (see {@link TempDirConstructorTest}), as a {@link Path} or a {@link File}.
 * Every declaration gets its <em>own</em> directory, so the field and the parameters
 * below are three different directories. Fields must not be {@code private} or {@code final}.
 */
class TempDirInjectionTest {

    @TempDir
    Path logs;

    @Test
    void fieldInjection() throws IOException {
        assertTrue(Files.isDirectory(logs));
        try (Stream<Path> nothing = Files.list(logs)) {
            assertEquals(0, nothing.count(), "a new temporary directory is empty");
        }
    }

    @Test
    void parameterInjectionGivesAnotherDirectory(@TempDir Path other) {
        assertNotEquals(logs, other);
    }

    @Test
    void severalDirectoriesAtOnce(@TempDir Path first, @TempDir File second) {
        assertNotEquals(first, second.toPath());
        assertTrue(second.isDirectory());
    }

    @Test
    void writeAndReadBack() throws IOException {
        Path file = logs.resolve("audit.log");
        try (AuditLogWriter writer = new AuditLogWriter(file)) {
            SampleAuditEvents.ALL.forEach(writer::write);
        }
        assertEquals(SampleAuditEvents.ALL, AuditLogReader.events(file));
    }
}
