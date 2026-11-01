package com.example.module_09.section_04;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import com.example.settings.AuthSettings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.util.ClearSystemProperty;
import org.junit.jupiter.api.util.SetSystemProperty;

/**
 * Module 9.4 lab – switch the trusted issuer with a system property.
 * <p>
 * {@code @SetSystemProperty(key, value)} sets a property for the class or one test;
 * {@code @ClearSystemProperty(key)} removes it. Both are repeatable, work on class
 * and method level (method wins), and restore the original value afterwards, so
 * no test leaks a property into the next. The keys come from {@link AuthSettings}.
 */
@SetSystemProperty(key = AuthSettings.ISSUER, value = "https://idp.example.com")
class AuthSettingsTest {

    @Test
    @ClearSystemProperty(key = AuthSettings.AUDIENCE)
    @ClearSystemProperty(key = AuthSettings.CLOCK_SKEW)
    void classLevelIssuerAndDefaults() {
        AuthSettings settings = AuthSettings.fromSystemProperties();
        assertEquals("https://idp.example.com", settings.issuer());
        assertEquals(AuthSettings.DEFAULT_AUDIENCE, settings.audience());
        assertEquals(Duration.ofSeconds(30), settings.clockSkew());
    }

    @Test
    @SetSystemProperty(key = AuthSettings.ISSUER, value = "https://login.bank.example.nl")
    void methodLevelSwitchesTheIssuer() {
        AuthSettings settings = AuthSettings.fromSystemProperties();
        assertTrue(settings.acceptsIssuer("https://login.bank.example.nl"));
        assertFalse(settings.acceptsIssuer("https://idp.example.com"));
    }

    @Test
    @SetSystemProperty(key = AuthSettings.AUDIENCE, value = "accounts-api")
    @SetSystemProperty(key = AuthSettings.CLOCK_SKEW, value = "120")
    void severalPropertiesAtOnce() {
        AuthSettings settings = AuthSettings.fromSystemProperties();
        assertEquals("accounts-api", settings.audience());
        assertEquals(Duration.ofMinutes(2), settings.clockSkew());
    }

    @Test
    @ClearSystemProperty(key = AuthSettings.ISSUER)
    void missingIssuerIsAnError() {
        IllegalStateException e = assertThrows(IllegalStateException.class, AuthSettings::fromSystemProperties);
        assertEquals("System property auth.issuer is not set", e.getMessage());
    }

    @Test
    @SetSystemProperty(key = AuthSettings.ISSUER, value = "http://idp.example.com")
    void plainHttpIsRefused() {
        assertThrows(IllegalArgumentException.class, AuthSettings::fromSystemProperties);
    }

    @Test
    @SetSystemProperty(key = AuthSettings.CLOCK_SKEW, value = "five minutes")
    void skewMustBeANumber() {
        assertThrows(IllegalArgumentException.class, AuthSettings::fromSystemProperties);
    }

    @Test
    @SetSystemProperty(key = AuthSettings.CLOCK_SKEW, value = "301")
    void skewHasAMaximum() {
        assertThrows(IllegalArgumentException.class, AuthSettings::fromSystemProperties);
    }

    @Test
    void issuerMatchIsCaseSensitive() {
        assertFalse(AuthSettings.fromSystemProperties().acceptsIssuer("https://IDP.example.com"));
    }
}
