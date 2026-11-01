package com.example.module_05.support;

import com.example.config.AppConfig;

/**
 * Module 5.1 – condition methods kept outside the test classes.
 * <p>
 * Referenced by fully qualified name, for example
 * {@code @EnabledIf("com.example.module_05.support.Environments#sandboxConfigured")}.
 * Condition methods in another class must be {@code static} and return {@code boolean}.
 */
public final class Environments {

    private Environments() {
    }

    /** True when {@code -Dapp.sandbox.url=...} or {@code APP_SANDBOX_URL} is set. */
    public static boolean sandboxConfigured() {
        return AppConfig.fromSystem().get("sandbox.url").isPresent();
    }

    /** True when the app environment is {@code integration}. */
    public static boolean integrationEnvironment() {
        return AppConfig.fromSystem().isIntegration();
    }
}
