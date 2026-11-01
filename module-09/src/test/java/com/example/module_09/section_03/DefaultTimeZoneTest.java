package com.example.module_09.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.TimeZone;

import com.example.session.SessionExpiry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.util.DefaultTimeZone;
import org.junit.jupiter.api.util.TimeZoneProvider;

/**
 * Module 9.3 lab – {@code @DefaultTimeZone}: one session rule, two answers.
 * <p>
 * "A session ends at local midnight" depends on {@code TimeZone.getDefault()}.
 * The same login at 22:30 UTC on 28 March 2026 is 23:30 in Amsterdam, so the
 * session there ends 30 minutes later; on a UTC server it lasts until 00:00 UTC,
 * 1.5 hours. The next day, 29 March, the Netherlands moves to summer time: that
 * local day has only 23 hours. Use full zone ids such as {@code Europe/Amsterdam},
 * not abbreviations such as {@code CET}.
 */
class DefaultTimeZoneTest {

    private static final Instant LATE_LOGIN = Instant.parse("2026-03-28T22:30:00Z");

    @Test
    @DefaultTimeZone("Europe/Amsterdam")
    void inAmsterdam() {
        assertEquals("23:30", SessionExpiry.localTime(LATE_LOGIN));
        assertEquals(Instant.parse("2026-03-28T23:00:00Z"), SessionExpiry.endOfLocalDay(LATE_LOGIN));
        assertTrue(SessionExpiry.isExpired(LATE_LOGIN, Instant.parse("2026-03-28T23:00:00Z")));
    }

    @Test
    @DefaultTimeZone("UTC")
    void onAUtcServer() {
        assertEquals("22:30", SessionExpiry.localTime(LATE_LOGIN));
        assertEquals(Instant.parse("2026-03-29T00:00:00Z"), SessionExpiry.endOfLocalDay(LATE_LOGIN));
        assertFalse(SessionExpiry.isExpired(LATE_LOGIN, Instant.parse("2026-03-28T23:00:00Z")));
    }

    @Test
    @DefaultTimeZone("Europe/Amsterdam")
    void theDayTheClocksGoForwardHas23Hours() {
        Instant morning = Instant.parse("2026-03-29T08:00:00Z");
        assertEquals(Duration.ofHours(23), SessionExpiry.lengthOfLocalDay(morning));
        assertEquals(Instant.parse("2026-03-29T22:00:00Z"), SessionExpiry.endOfLocalDay(morning), "midnight CEST is 22:00 UTC");
    }

    @Test
    @DefaultTimeZone(timeZoneProvider = BankHeadOffice.class)
    void computedTimeZone() {
        assertEquals("Europe/Amsterdam", TimeZone.getDefault().getID());
        assertEquals(ZoneId.of("Europe/Amsterdam"), ZoneId.systemDefault());
    }

    /** A {@link TimeZoneProvider} is a {@code Supplier<TimeZone>} with a no-argument constructor. */
    public static class BankHeadOffice implements TimeZoneProvider {

        @Override
        public TimeZone get() {
            return TimeZone.getTimeZone("Europe/Amsterdam");
        }
    }
}
