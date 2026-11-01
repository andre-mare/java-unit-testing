package com.example.module_05.section_01;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.GraphicsEnvironment;

import com.example.module_05.support.Environments;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Module 5.1 – custom conditions with {@code @EnabledIf} and {@code @DisabledIf}.
 * <p>
 * A condition method returns {@code boolean} and takes no parameters or a single
 * {@code ExtensionContext}. It can be:
 * <ul>
 *   <li>a method of the test class, referenced by name (instance or static);</li>
 *   <li>a {@code static} method elsewhere, referenced as {@code fully.qualified.Class#method}.</li>
 * </ul>
 * Condition methods must be {@code static} when used at class level (see
 * {@link SandboxOnlyTest}), on a {@code @ParameterizedTest} or {@code @TestTemplate},
 * or in another class.
 */
class CustomConditionsTest {

    boolean hasAtLeastTwoProcessors() {
        return Runtime.getRuntime().availableProcessors() >= 2;
    }

    boolean isTaggedSlow(ExtensionContext context) {
        return context.getTags().contains("slow");
    }

    @Test
    @EnabledIf("hasAtLeastTwoProcessors")
    void onMultiCoreMachines() {
        assertTrue(Runtime.getRuntime().availableProcessors() >= 2);
    }

    @Test
    @DisabledIf("isTaggedSlow")
    void conditionCanInspectTheExtensionContext() {
        assertTrue(true, "runs because this test has no 'slow' tag");
    }

    @Test
    @EnabledIf("com.example.module_05.support.Environments#integrationEnvironment")
    void externalConditionMethod() {
        assertTrue(Environments.integrationEnvironment());
    }

    @Test
    @DisabledIf(value = "java.awt.GraphicsEnvironment#isHeadless", disabledReason = "headless environment")
    void needsADisplay() {
        // An existing static JDK method works as a condition.
        assertFalse(GraphicsEnvironment.isHeadless());
    }
}
