package com.example.module_09.section_02;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;

import com.example.audit.AuditLogWriter;
import com.example.module_09.support.SampleAuditEvents;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

/**
 * Module 9.2 – {@code @AutoClose} under {@code PER_CLASS}.
 * <p>
 * One instance serves all tests, so an instance field lives as long as the class:
 * it is closed once, after {@code @AfterAll}. The writer is created in a
 * non-static {@code @BeforeAll} (allowed with {@code PER_CLASS}), keeps counting
 * across tests, and is still open in {@code @AfterAll}.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AutoClosePerClassTest {

    @TempDir
    static Path logs;

    @AutoClose
    AuditLogWriter writer;

    @BeforeAll
    void openOnce() {
        writer = new AuditLogWriter(logs.resolve("audit.log"));
    }

    @Test
    @Order(1)
    void first() {
        writer.write(SampleAuditEvents.LOGIN);
        assertEquals(1, writer.written());
    }

    @Test
    @Order(2)
    void secondSeesTheSameWriter() {
        writer.write(SampleAuditEvents.REVOKE);
        assertEquals(2, writer.written());
    }

    @AfterAll
    void stillOpen() {
        assertFalse(writer.isClosed(), "closed after @AfterAll, together with the instance");
    }
}
