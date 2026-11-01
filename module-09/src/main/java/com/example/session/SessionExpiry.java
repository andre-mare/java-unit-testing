package com.example.session;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * "A banking session ends at midnight, local time", Module 9 production code.
 * <p>
 * Local time means the JVM default time zone ({@code ZoneId.systemDefault()}),
 * so the answer differs between a server in UTC and a laptop in Amsterdam, and
 * a day is 23 or 25 hours long when the clocks change. Module 9.3 pins the zone
 * with {@code @DefaultTimeZone}.
 */
public final class SessionExpiry {

    private static final DateTimeFormatter HOURS_MINUTES = DateTimeFormatter.ofPattern("HH:mm");

    private SessionExpiry() {
    }

    /** The next local midnight after {@code login}. */
    public static Instant endOfLocalDay(Instant login) {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate day = login.atZone(zone).toLocalDate();
        return day.plusDays(1).atStartOfDay(zone).toInstant();
    }

    public static boolean isExpired(Instant login, Instant now) {
        return !now.isBefore(endOfLocalDay(login));
    }

    /** Length of the local calendar day that contains {@code instant}. */
    public static Duration lengthOfLocalDay(Instant instant) {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate day = instant.atZone(zone).toLocalDate();
        return Duration.between(day.atStartOfDay(zone), day.plusDays(1).atStartOfDay(zone));
    }

    /** Wall-clock time in the default zone, such as {@code 23:30}. */
    public static String localTime(Instant instant) {
        return HOURS_MINUTES.format(instant.atZone(ZoneId.systemDefault()));
    }
}
