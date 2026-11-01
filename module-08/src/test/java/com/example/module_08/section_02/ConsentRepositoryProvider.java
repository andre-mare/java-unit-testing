package com.example.module_08.section_02;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.example.consent.FileConsentRepository;
import com.example.consent.InMemoryConsentRepository;

import org.junit.jupiter.api.extension.ClassTemplateInvocationContext;
import org.junit.jupiter.api.extension.ClassTemplateInvocationContextProvider;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

/**
 * Module 8.2 – runs a whole test class once per {@code ConsentRepository} implementation.
 * <p>
 * The class-level twin of a test template provider:
 * {@code supportsClassTemplate} picks the classes it serves (here: those that
 * implement {@link RepositoryAware}); {@code provideClassTemplateInvocationContexts}
 * returns one context per run of the class, with a display name and extensions for
 * that run only. The extension used here is a {@link TestInstancePostProcessor}: it
 * runs for every new test instance, so each test gets a fresh, empty repository.
 * <p>
 * The file-backed repository writes to a new temporary directory per test. The
 * directory is put in the extension context's store as an {@code AutoCloseable};
 * JUnit closes (here: deletes) it when that context ends.
 */
public class ConsentRepositoryProvider implements ClassTemplateInvocationContextProvider {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ConsentRepositoryProvider.class);

    @Override
    public boolean supportsClassTemplate(ExtensionContext context) {
        return RepositoryAware.class.isAssignableFrom(context.getRequiredTestClass());
    }

    @Override
    public Stream<ClassTemplateInvocationContext> provideClassTemplateInvocationContexts(ExtensionContext context) {
        return Stream.of(
            invocation("in-memory", ctx -> {
                InMemoryConsentRepository repository = new InMemoryConsentRepository();
                return new RepositoryFixture("in-memory", repository, () -> repository);
            }),
            invocation("file-backed", ctx -> {
                Path file = temporaryDirectory(ctx).resolve("consents.txt");
                return new RepositoryFixture("file-backed", new FileConsentRepository(file), () -> new FileConsentRepository(file));
            }));
    }

    private static ClassTemplateInvocationContext invocation(String name,
            Function<ExtensionContext, RepositoryFixture> factory) {
        return new ClassTemplateInvocationContext() {

            @Override
            public String getDisplayName(int invocationIndex) {
                return "[" + invocationIndex + "] " + name;
            }

            @Override
            public List<Extension> getAdditionalExtensions() {
                return List.of((TestInstancePostProcessor) (testInstance, ctx) -> {
                    // Called for @Nested instances too; only the outer class wants a repository.
                    if (testInstance instanceof RepositoryAware aware) {
                        aware.use(factory.apply(ctx));
                    }
                });
            }
        };
    }

    private static Path temporaryDirectory(ExtensionContext context) {
        try {
            Path directory = Files.createTempDirectory("m08-consents-");
            context.getStore(NAMESPACE).put(directory.toString(), (AutoCloseable) () -> deleteTree(directory));
            return directory;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void deleteTree(Path directory) throws IOException {
        try (Stream<Path> paths = Files.walk(directory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }
}
