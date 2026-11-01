package com.example.module_09.section_01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import com.example.audit.AuditLogWriter;
import com.example.module_09.support.SampleAuditEvents;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Module 9.1 – constructor injection: the object under test is built once per
 * test from a fresh directory, and can then be {@code final}.
 */
class TempDirConstructorTest {

    private final AuditLogWriter writer;

    TempDirConstructorTest(@TempDir Path directory) {
        this.writer = new AuditLogWriter(directory.resolve("audit.log"));
    }

    @AfterEach
    void close() {
        writer.close();
    }

    @Test
    void theLogFileIsCreatedOnOpen() {
        assertTrue(Files.exists(writer.file()));
        assertEquals(0, writer.written());
    }

    @Test
    void eachTestStartsWithAnEmptyFile() throws Exception {
        writer.write(SampleAuditEvents.LOGIN);
        assertEquals(1, Files.readAllLines(writer.file()).size());
    }
}
