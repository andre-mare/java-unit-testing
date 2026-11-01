package com.example.module_05.section_01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.condition.OS.LINUX;
import static org.junit.jupiter.api.condition.OS.MAC;
import static org.junit.jupiter.api.condition.OS.WINDOWS;

import java.io.File;
import java.util.Locale;

import com.example.module_05.support.UnixOnly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

/**
 * Module 5.1 – operating system and architecture conditions.
 * <p>
 * Start here. Each enabled test checks the very thing its condition promises,
 * so a test can only pass on a machine where it should run. On your machine,
 * some of these run and the rest show as skipped with their reason.
 */
class OsConditionsTest {

    private static String osName() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT);
    }

    @Test
    void currentOsIsDetected() {
        System.out.println("OS.current() = " + OS.current() + ", os.arch = " + System.getProperty("os.arch"));
        assertNotEquals(null, OS.current());
    }

    @Test
    @EnabledOnOs(MAC)
    void onlyOnMacOs() {
        assertTrue(osName().contains("mac"));
    }

    @Test
    @EnabledOnOs(WINDOWS)
    void onlyOnWindows() {
        assertEquals('\\', File.separatorChar);
    }

    @Test
    @EnabledOnOs({ LINUX, MAC })
    void onLinuxOrMac() {
        assertEquals('/', File.separatorChar);
    }

    @Test
    @DisabledOnOs(value = WINDOWS, disabledReason = "Windows uses ';' as path separator")
    void notOnWindows() {
        assertEquals(':', File.pathSeparatorChar);
    }

    @UnixOnly // composed: @Test + @EnabledOnOs({LINUX, MAC})
    void composedUnixOnlyAnnotation() {
        assertEquals("/", File.separator);
    }

    @Test
    @EnabledOnOs(architectures = "aarch64")
    void onArm64() {
        assertEquals("aarch64", System.getProperty("os.arch"));
    }

    @Test
    @DisabledOnOs(architectures = "aarch64")
    void notOnArm64() {
        assertNotEquals("aarch64", System.getProperty("os.arch"));
    }

    @Test
    @EnabledOnOs(value = MAC, architectures = "aarch64")
    void onAppleSilicon() {
        assertTrue(osName().contains("mac"));
        assertEquals("aarch64", System.getProperty("os.arch"));
    }
}
