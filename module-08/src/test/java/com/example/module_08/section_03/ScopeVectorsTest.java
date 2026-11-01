package com.example.module_08.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.example.scope.ScopeSet;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 * Module 8.3 – tests from a data file, each pointing back to its line.
 * <p>
 * Every vector in {@code src/test/resources/scope-vectors.txt} becomes one
 * dynamic test. The optional {@link URI} is the test's <em>source</em>: IDEs and
 * reports use it to jump to the origin of a failure. JUnit maps these schemes:
 * <ul>
 *   <li>{@code classpath:/scope-vectors.txt?line=12}: a classpath resource, line 12;</li>
 *   <li>{@code method:com.example.scope.ScopeSet#parse(java.lang.String)}: a method;</li>
 *   <li>{@code class:com.example.scope.ScopeSet}: a class ({@code ?line=} allowed);</li>
 *   <li>{@code file:} URIs: a file or a directory; anything else: a plain URI source.</li>
 * </ul>
 * Adding a vector means editing the text file, not the Java code.
 */
class ScopeVectorsTest {

    private static final String RESOURCE = "/scope-vectors.txt";

    record Vector(int line, boolean valid, String scope, String expectation) {

        static Vector parse(int line, String text) {
            int open = text.indexOf('[');
            int close = text.lastIndexOf(']');
            return new Vector(line, text.startsWith("VALID "), text.substring(open + 1, close),
                text.substring(close + 1).trim());
        }

        URI source() {
            return URI.create("classpath:" + RESOURCE + "?line=" + line);
        }
    }

    @TestFactory
    Stream<DynamicContainer> scopeVectors() throws IOException {
        List<Vector> vectors = readVectors();
        return Stream.of(
            dynamicContainer("valid scopes", URI.create("method:com.example.scope.ScopeSet#parse(java.lang.String)"),
                vectors.stream().filter(Vector::valid).map(ScopeVectorsTest::validTest)),
            dynamicContainer("invalid scopes", URI.create("class:com.example.scope.ScopeSet"),
                vectors.stream().filter(v -> !v.valid()).map(ScopeVectorsTest::invalidTest)));
    }

    private static DynamicTest validTest(Vector v) {
        return dynamicTest("[" + v.scope() + "] has " + v.expectation() + " token(s)", v.source(),
            () -> assertEquals(Integer.parseInt(v.expectation()), ScopeSet.parse(v.scope()).size()));
    }

    private static DynamicTest invalidTest(Vector v) {
        return dynamicTest("[" + v.scope() + "] is rejected: " + v.expectation(), v.source(),
            () -> assertThrows(IllegalArgumentException.class, () -> ScopeSet.parse(v.scope())));
    }

    static List<Vector> readVectors() throws IOException {
        List<Vector> vectors = new ArrayList<>();
        try (InputStream in = Objects.requireNonNull(ScopeVectorsTest.class.getResourceAsStream(RESOURCE), RESOURCE);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String text;
            int line = 0;
            while ((text = reader.readLine()) != null) {
                line++;
                if (!text.isBlank() && !text.startsWith("#")) {
                    vectors.add(Vector.parse(line, text));
                }
            }
        }
        return vectors;
    }
}
