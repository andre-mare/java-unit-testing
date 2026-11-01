package com.example.module_09.section_05;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.TimeZone;

import com.example.audit.AuditTrail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Module 9.5 – {@code @Isolated}: nothing else runs while this class runs.
 * <p>
 * For code that changes several global things at once and cannot be locked
 * precisely: here a "bootstrap" that redirects {@code System.out} and replaces the
 * default locale, time zone and audit trail. The test puts everything back in
 * {@code finally}. Use it sparingly: an isolated class stops all parallelism
 * while it runs.
 */
@Isolated
class IsolatedBootstrapTest {

    @Test
    void bootstrapChangesEverythingGlobal() {
        PrintStream originalOut = System.out;
        Locale originalLocale = Locale.getDefault();
        TimeZone originalZone = TimeZone.getDefault();
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
            Locale.setDefault(Locale.forLanguageTag("nl-NL"));
            TimeZone.setDefault(TimeZone.getTimeZone("Europe/Amsterdam"));
            AuditTrail.clear();
            AuditTrail.record("bootstrap");
            System.out.println("Audit trail ready: " + AuditTrail.entries());

            assertThat(captured.toString(StandardCharsets.UTF_8)).contains("Audit trail ready: [bootstrap]");
        } finally {
            System.setOut(originalOut);
            Locale.setDefault(originalLocale);
            TimeZone.setDefault(originalZone);
            AuditTrail.clear();
        }
    }
}
