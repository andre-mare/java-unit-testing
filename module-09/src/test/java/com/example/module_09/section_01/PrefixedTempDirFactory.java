package com.example.module_09.section_01;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.extension.AnnotatedElementContext;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.io.TempDirFactory;

/**
 * Module 9.1 – a custom {@link TempDirFactory}: directories named after the test class,
 * so a kept directory ({@code cleanup = NEVER}) is easy to find in {@code /tmp}.
 * A factory needs a no-argument constructor; JUnit creates one per directory and
 * calls {@code close()} on it afterwards.
 */
public class PrefixedTempDirFactory implements TempDirFactory {

    @Override
    public Path createTempDirectory(AnnotatedElementContext elementContext, ExtensionContext extensionContext)
            throws Exception {
        return Files.createTempDirectory("junit-" + extensionContext.getRequiredTestClass().getSimpleName() + "-");
    }
}
