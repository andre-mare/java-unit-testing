package com.example.module_05.section_01;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.config.AppConfig;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

/**
 * Module 5.1 – a condition on the whole class.
 * <p>
 * Unlike an assumption in {@code @BeforeAll}, a class-level condition is checked
 * before anything in the class runs: when it disables the class, {@code @BeforeAll}
 * is never called. Run it with {@code -Dapp.sandbox.url=https://sandbox.example.com}.
 */
@EnabledIf(value = "com.example.module_05.support.Environments#sandboxConfigured",
    disabledReason = "set -Dapp.sandbox.url=... to run the sandbox tests")
class SandboxOnlyTest {

    private static String sandboxUrl;

    @BeforeAll
    static void connect() {
        sandboxUrl = AppConfig.fromSystem().get("sandbox.url").orElseThrow(); // never throws: the condition passed
    }

    @Test
    void usesHttps() {
        assertTrue(sandboxUrl.startsWith("https://"), "sandbox URL must use HTTPS: " + sandboxUrl);
    }
}
