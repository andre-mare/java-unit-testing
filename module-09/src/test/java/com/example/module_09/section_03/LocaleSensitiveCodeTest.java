package com.example.module_09.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Locale;

import com.example.display.AmountFormatter;
import com.example.session.SessionExpiry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.util.ReadsDefaultLocale;
import org.junit.jupiter.api.util.ReadsDefaultTimeZone;

/**
 * Module 9.3 – tests that <em>read</em> the defaults without setting them.
 * <p>
 * These run with whatever locale and zone the machine has, so they can only check
 * that the code follows the default. {@code @ReadsDefaultLocale} and
 * {@code @ReadsDefaultTimeZone} tell the parallel scheduler (9.5) not to run them
 * while a test with {@code @DefaultLocale} / {@code @DefaultTimeZone} (which count
 * as writers, like {@code @WritesDefaultLocale}) is changing the default.
 */
class LocaleSensitiveCodeTest {

    @Test
    @ReadsDefaultLocale
    void formatterFollowsTheDefaultLocale() {
        BigDecimal amount = new BigDecimal("1234.56");
        Currency eur = Currency.getInstance("EUR");
        assertEquals(AmountFormatter.format(amount, eur, Locale.getDefault(Locale.Category.FORMAT)),
            AmountFormatter.format(amount, eur));
    }

    @Test
    @ReadsDefaultTimeZone
    void sessionsFollowTheDefaultZone() {
        Instant login = Instant.parse("2026-11-02T09:00:00Z");
        Instant expected = login.atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1)
            .atStartOfDay(ZoneId.systemDefault()).toInstant();
        assertEquals(expected, SessionExpiry.endOfLocalDay(login));
    }
}
