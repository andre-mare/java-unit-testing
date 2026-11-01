package com.example.audit;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Appends audit events to a file, one line each, Module 9 production code.
 * <p>
 * Opens the file on construction (creating it if needed) and keeps it open until
 * {@link #close()}, so it must be closed: a classic {@link AutoCloseable}. Every
 * write is flushed, so a reader sees it at once. Closing twice is allowed;
 * writing after close is not.
 */
public final class AuditLogWriter implements AutoCloseable {

    private final Path file;
    private final BufferedWriter out;
    private int written;
    private boolean closed;

    public AuditLogWriter(Path file) {
        this.file = file;
        try {
            this.out = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot open audit log " + file, e);
        }
    }

    public synchronized void write(AuditEvent event) {
        if (closed) {
            throw new IllegalStateException("Audit log is closed: " + file);
        }
        try {
            out.write(event.toLine());
            out.newLine();
            out.flush();
            written++;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Path file() {
        return file;
    }

    /** Events written by this writer (not lines already in the file). */
    public synchronized int written() {
        return written;
    }

    public synchronized boolean isClosed() {
        return closed;
    }

    @Override
    public synchronized void close() {
        if (closed) {
            return;
        }
        closed = true;
        try {
            out.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
