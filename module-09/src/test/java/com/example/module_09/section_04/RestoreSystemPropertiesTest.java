package com.example.module_09.section_04;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.settings.AuthSettings;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.util.ReadsSystemProperty;
import org.junit.jupiter.api.util.RestoreSystemProperties;

/**
 * Module 9.4 – {@code @RestoreSystemProperties}: change anything, get it all back.
 * <p>
 * For code that sets properties itself (here: plain {@code System.setProperty}),
 * the annotation snapshots <em>all</em> system properties before the test and
 * restores them after {@code @AfterEach}. The second test proves nothing leaked.
 * It reads properties without changing them, so it says so with
 * {@code @ReadsSystemProperty}; together with {@code @WritesSystemProperty} that
 * lets the parallel scheduler (9.5) keep readers and writers apart.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RestoreSystemPropertiesTest {

    private static final String DEMO_KEY = "m09.restore.demo";

    @Test
    @Order(1)
    @RestoreSystemProperties
    void changesPropertiesProgrammatically() {
        System.setProperty(DEMO_KEY, "changed");
        System.setProperty(AuthSettings.ISSUER, "https://idp.example.com");
        assertEquals("https://idp.example.com", AuthSettings.fromSystemProperties().issuer());
    }

    @Test
    @Order(2)
    @ReadsSystemProperty
    void nothingLeakedIntoTheNextTest() {
        assertNull(System.getProperty(DEMO_KEY));
        assertNull(System.getProperty(AuthSettings.ISSUER));
    }
}
