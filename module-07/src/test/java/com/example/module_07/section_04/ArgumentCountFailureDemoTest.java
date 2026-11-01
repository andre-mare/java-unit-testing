package com.example.module_07.section_04;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.example.iban.Iban;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ArgumentCountValidationMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.FieldSource;

/**
 * Module 7.4 – failure demo: the errors the previous class avoids.
 * <p>
 * Excluded from normal builds by the {@code expected-failure-tests} tag. Run with
 * {@code mvn test -DexcludedGroups=none -Dtest=ArgumentCountFailureDemoTest}.
 * Expected result:
 * <ul>
 *   <li>{@code strictWithAnExtraColumn}: fails, the row has 3 columns and the method takes 2;</li>
 *   <li>{@code tooFewArguments}: fails, nothing can supply {@code country};</li>
 *   <li>{@code zeroInvocations}: fails, the source supplied no arguments.</li>
 * </ul>
 */
@Tag("expected-failure-tests")
class ArgumentCountFailureDemoTest {

    static final List<String> noIbans = List.of();

    @ParameterizedTest(argumentCountValidation = ArgumentCountValidationMode.STRICT)
    @CsvSource({ "NL91ABNA0417164300, NL, 18" })
    void strictWithAnExtraColumn(String iban, String country) {
        assertEquals(country, Iban.parse(iban).countryCode());
    }

    @ParameterizedTest
    @CsvSource({ "NL91ABNA0417164300" })
    void tooFewArguments(String iban, String country) {
        assertEquals(country, Iban.parse(iban).countryCode());
    }

    @ParameterizedTest
    @FieldSource("noIbans")
    void zeroInvocations(String iban) {
        Iban.parse(iban);
    }
}
