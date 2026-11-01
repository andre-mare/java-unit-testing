package com.example.module_08.section_01;

import java.util.Currency;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestTemplate;

/**
 * Module 8.1 – failure demo: the two ways a test template fails before it runs.
 * <p>
 * Excluded from normal builds by the {@code expected-failure-tests} tag. Run with
 * {@code mvn test -DexcludedGroups=none -Dtest=TemplateFailureDemoTest}. Expected:
 * <ul>
 *   <li>{@code noProvider}: fails, no {@code TestTemplateInvocationContextProvider} is registered;</li>
 *   <li>{@code zeroInvocations}: fails, the provider supplied no invocations and
 *       did not allow zero.</li>
 * </ul>
 */
@Tag("expected-failure-tests")
class TemplateFailureDemoTest {

    @TestTemplate
    void noProvider() {
    }

    @ForEachCurrency({})
    void zeroInvocations(Currency currency) {
    }
}
