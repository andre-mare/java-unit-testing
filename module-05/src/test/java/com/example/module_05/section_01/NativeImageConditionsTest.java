package com.example.module_05.section_01;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.junit.jupiter.api.condition.EnabledInNativeImage;

/**
 * Module 5.1 – GraalVM native image conditions.
 * <p>
 * Relevant only when tests run inside a native image (GraalVM Native Build Tools).
 * On a normal JVM, the first test runs and the second is skipped. GraalVM sets
 * the system property {@code org.graalvm.nativeimage.imagecode} in native images.
 */
class NativeImageConditionsTest {

    @Test
    @DisabledInNativeImage
    void onRegularJvm() {
        assertNull(System.getProperty("org.graalvm.nativeimage.imagecode"));
    }

    @Test
    @EnabledInNativeImage
    void onlyInNativeImage() {
        assertNotNull(System.getProperty("org.graalvm.nativeimage.imagecode"));
    }
}
