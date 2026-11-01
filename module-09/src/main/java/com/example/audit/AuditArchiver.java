package com.example.audit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDate;

/**
 * Moves a finished audit log into an archive directory, Module 9 production code.
 * <p>
 * The archived name is {@code audit-<date>.log}; when that exists, {@code -1},
 * {@code -2}, ... is added, so nothing is ever overwritten. Works on any file
 * system ({@code Path} based), including an in-memory one.
 */
public final class AuditArchiver {

    private AuditArchiver() {
    }

    /** Moves {@code logFile} into {@code archiveDirectory} (created if missing); returns the new path. */
    public static Path archive(Path logFile, Path archiveDirectory, LocalDate day) throws IOException {
        if (!Files.exists(logFile)) {
            throw new NoSuchFileException(logFile.toString());
        }
        Files.createDirectories(archiveDirectory);
        Path target = archiveDirectory.resolve("audit-" + day + ".log");
        for (int n = 1; Files.exists(target); n++) {
            target = archiveDirectory.resolve("audit-" + day + "-" + n + ".log");
        }
        return Files.move(logFile, target);
    }
}
