package com.example.module_09.section_05;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Locale;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.ResourceLockTarget;
import org.junit.jupiter.api.parallel.Resources;

/**
 * Module 9.5 – the predefined {@link Resources} keys and {@code target = CHILDREN}.
 * <p>
 * JUnit names the common global resources: {@code SYSTEM_PROPERTIES},
 * {@code SYSTEM_OUT}, {@code SYSTEM_ERR}, {@code LOCALE} and {@code TIME_ZONE}.
 * The built-in extensions of 9.3 and 9.4 take these locks for you; hand-written
 * code that touches them declares them itself.
 * <p>
 * A lock on the class normally covers the whole class as one unit, so its methods
 * cannot run side by side. {@code target = CHILDREN} puts the lock on each method
 * instead: with {@code mode = READ}, the readers below can run in parallel with
 * each other while no test changes the locale or time zone.
 */
@Execution(ExecutionMode.CONCURRENT)
@ResourceLock(value = Resources.LOCALE, mode = ResourceAccessMode.READ, target = ResourceLockTarget.CHILDREN)
@ResourceLock(value = Resources.TIME_ZONE, mode = ResourceAccessMode.READ, target = ResourceLockTarget.CHILDREN)
class SharedReadLockTest {

    @Test
    void localeIsStableWhileReading() {
        Locale first = Locale.getDefault();
        assertEquals(first, Locale.getDefault());
    }

    @Test
    void timeZoneIsStableWhileReading() {
        TimeZone first = TimeZone.getDefault();
        assertEquals(first.getID(), TimeZone.getDefault().getID());
    }

    @Test
    void bothAreSet() {
        assertNotNull(Locale.getDefault());
        assertNotNull(TimeZone.getDefault());
    }
}
