package com.example.module_09.section_01;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import org.junit.jupiter.api.extension.AnnotatedElementContext;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.io.TempDirFactory;

/**
 * Module 9.1 – a {@link TempDirFactory} that creates the directory in memory, with Jimfs.
 * <p>
 * Nothing touches the disk, so it is fast and leaves nothing behind. JUnit calls
 * {@link #close()} when the directory goes out of scope, which closes the whole
 * in-memory file system. Needs the Jimfs dependency from
 * {@code module9-pom-dependencies.xml}. A Jimfs {@code Path} has no {@code File}:
 * inject it as {@code Path} only.
 */
public class JimfsTempDirFactory implements TempDirFactory {

    private final FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

    @Override
    public Path createTempDirectory(AnnotatedElementContext elementContext, ExtensionContext extensionContext)
            throws IOException {
        return Files.createDirectories(fileSystem.getPath("/junit"));
    }

    @Override
    public void close() throws IOException {
        fileSystem.close();
    }
}
