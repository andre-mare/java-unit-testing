package com.example.consent;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@link ConsentRepository} that keeps one line per consent in a text file:
 * {@code id|clientId|psuId|perm1,perm2|expiresAt|status}.
 * <p>
 * Every call reads the file, so a second repository on the same file sees the same
 * data (a "restart"). Values must not contain {@code |}, {@code ,} or line breaks.
 * Not safe for concurrent writers; good enough for the course.
 */
public final class FileConsentRepository implements ConsentRepository {

    private final Path file;

    public FileConsentRepository(Path file) {
        this.file = file;
    }

    public Path file() {
        return file;
    }

    @Override
    public synchronized void save(Consent consent) {
        Map<String, Consent> all = readAll();
        all.put(consent.id(), consent);
        writeAll(all);
    }

    @Override
    public Optional<Consent> findById(String id) {
        return Optional.ofNullable(readAll().get(id));
    }

    @Override
    public List<Consent> findByPsu(String psuId) {
        return readAll().values().stream().filter(c -> c.psuId().equals(psuId)).toList();
    }

    @Override
    public int count() {
        return readAll().size();
    }

    private Map<String, Consent> readAll() {
        Map<String, Consent> all = new LinkedHashMap<>();
        if (!Files.exists(file)) {
            return all;
        }
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    Consent consent = parse(line);
                    all.put(consent.id(), consent);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return all;
    }

    private void writeAll(Map<String, Consent> all) {
        List<String> lines = all.values().stream().map(FileConsentRepository::format).toList();
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static String format(Consent c) {
        for (String value : List.of(c.id(), c.clientId(), c.psuId())) {
            check(value);
        }
        c.permissions().forEach(FileConsentRepository::check);
        return String.join("|", c.id(), c.clientId(), c.psuId(),
            c.permissions().stream().sorted().collect(Collectors.joining(",")),
            c.expiresAt().toString(), c.status().name());
    }

    static Consent parse(String line) {
        String[] f = line.split("\\|", -1);
        if (f.length != 6) {
            throw new IllegalStateException("Corrupt consent line: " + line);
        }
        return new Consent(f[0], f[1], f[2], Set.copyOf(Arrays.asList(f[3].split(","))),
            Instant.parse(f[4]), ConsentStatus.valueOf(f[5]));
    }

    private static void check(String value) {
        if (value.contains("|") || value.contains(",") || value.contains("\n") || value.contains("\r")) {
            throw new IllegalArgumentException("Value cannot be stored in a consent file: " + value);
        }
    }
}
