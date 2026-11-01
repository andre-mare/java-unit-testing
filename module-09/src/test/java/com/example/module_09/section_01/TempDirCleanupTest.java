package com.example.module_09.section_01;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

/**
 * Module 9.1 – when JUnit deletes a temporary directory.
 * <ul>
 *   <li>{@code cleanup = ALWAYS} (the default): after the test, pass or fail;</li>
 *   <li>{@code ON_SUCCESS}: kept when the test fails, so you can look at the files;</li>
 *   <li>{@code NEVER}: kept; you clean up yourself.</li>
 * </ul>
 * Change the default for the whole run with
 * {@code junit.jupiter.tempdir.cleanup.mode.default = on_success}. The two tests
 * remember their directories; {@code @AfterAll} checks what is left, then removes
 * the one JUnit was told to keep.
 */
class TempDirCleanupTest {

    private static Path deletedAfterTest;
    private static Path keptAfterTest;

    @Test
    void alwaysCleanedUp(@TempDir Path directory) throws IOException {
        Files.writeString(directory.resolve("note.txt"), "gone soon");
        deletedAfterTest = directory;
    }

    @Test
    void neverCleanedUp(@TempDir(cleanup = CleanupMode.NEVER) Path directory) throws IOException {
        Files.writeString(directory.resolve("note.txt"), "still here");
        keptAfterTest = directory;
    }

    @AfterAll
    static void checkWhatIsLeft() throws IOException {
        assertFalse(Files.exists(deletedAfterTest), "ALWAYS: deleted after its test");
        assertTrue(Files.exists(keptAfterTest.resolve("note.txt")), "NEVER: still there");
        try (Stream<Path> paths = Files.walk(keptAfterTest)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }
}
