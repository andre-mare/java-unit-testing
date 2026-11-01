package com.example.audit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Reads an audit log written by {@link AuditLogWriter}, Module 9 production code. */
public final class AuditLogReader {

    private AuditLogReader() {
    }

    public static List<String> lines(Path file) throws IOException {
        return Files.readAllLines(file, StandardCharsets.UTF_8).stream().filter(l -> !l.isBlank()).toList();
    }

    public static List<AuditEvent> events(Path file) throws IOException {
        return lines(file).stream().map(AuditEvent::fromLine).toList();
    }
}
