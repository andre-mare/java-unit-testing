package com.example.module_09.section_01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import com.example.audit.AuditLogReader;
import com.example.audit.AuditLogWriter;
import com.example.module_09.support.SampleAuditEvents;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

/**
 * Module 9.1 – a {@code static} {@code @TempDir} is shared by all tests of the class
 * and deleted after the last one.
 * <p>
 * Useful for expensive set-up, but the tests now depend on each other, so their
 * order must be fixed. Prefer a fresh directory per test when you can.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SharedTempDirTest {

    @TempDir
    static Path shared;

    @Test
    @Order(1)
    void firstTestWrites() {
        try (AuditLogWriter writer = new AuditLogWriter(shared.resolve("audit.log"))) {
            writer.write(SampleAuditEvents.LOGIN);
        }
    }

    @Test
    @Order(2)
    void secondTestAppendsToTheSameFile() throws Exception {
        assertTrue(Files.exists(shared.resolve("audit.log")), "the file from the first test is still there");
        try (AuditLogWriter writer = new AuditLogWriter(shared.resolve("audit.log"))) {
            writer.write(SampleAuditEvents.REVOKE);
        }
        assertEquals(2, AuditLogReader.lines(shared.resolve("audit.log")).size());
    }
}
