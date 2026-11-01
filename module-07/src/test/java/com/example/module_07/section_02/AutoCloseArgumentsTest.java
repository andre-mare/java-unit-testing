package com.example.module_07.section_02;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.example.iban.IbanValidator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Module 7.2 – arguments that are {@link AutoCloseable} are closed after their invocation.
 * <p>
 * Handy for resources such as files or connections created by the argument
 * source. When one argument object is reused across invocations, switch this off
 * with {@code autoCloseArguments = false}, or it would be closed after the first use.
 */
class AutoCloseArgumentsTest {

    /** A stand-in for an open resource, such as a file with IBANs. */
    static final class IbanBatch implements AutoCloseable {

        private final List<String> ibans;
        private boolean closed;

        IbanBatch(String... ibans) {
            this.ibans = List.of(ibans);
        }

        List<String> read() {
            if (closed) {
                throw new IllegalStateException("batch already closed");
            }
            return ibans;
        }

        @Override
        public void close() {
            closed = true;
        }

        @Override
        public String toString() {
            return "batch of " + ibans.size();
        }
    }

    private static final List<IbanBatch> created = new ArrayList<>();
    private static final IbanBatch SHARED = new IbanBatch("NL91ABNA0417164300");

    @BeforeAll
    static void reset() {
        created.clear();
    }

    static Stream<IbanBatch> freshBatches() {
        IbanBatch a = new IbanBatch("NL91ABNA0417164300", "BE68539007547034");
        IbanBatch b = new IbanBatch("DE89370400440532013000");
        created.add(a);
        created.add(b);
        return Stream.of(a, b);
    }

    static Stream<IbanBatch> sharedBatch() {
        return Stream.of(SHARED, SHARED); // the same object twice
    }

    @ParameterizedTest
    @MethodSource("freshBatches")
    void freshResourcesAreClosedForYou(IbanBatch batch) {
        assertTrue(batch.read().stream().allMatch(IbanValidator::isValid));
    }

    @ParameterizedTest(autoCloseArguments = false)
    @MethodSource("sharedBatch")
    void sharedResourceMustStayOpen(IbanBatch batch) {
        assertEquals(1, batch.read().size()); // would throw on the 2nd invocation if closed after the 1st
    }

    @AfterAll
    static void verifyClosing() {
        assertEquals(2, created.size());
        assertTrue(created.stream().allMatch(batch -> batch.closed), "closed after their invocation");
        assertFalse(SHARED.closed, "autoCloseArguments = false left it open");
    }
}
