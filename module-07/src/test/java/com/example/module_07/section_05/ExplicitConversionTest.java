package com.example.module_07.section_05;

import static com.example.module_07.support.TokenScenarios.ISSUER;
import static com.example.module_07.support.TokenScenarios.AUDIENCE;
import static com.example.module_07.support.TokenScenarios.CLOCK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.JavaTimeConversionPattern;
import org.junit.jupiter.params.provider.CsvSource;

import com.example.module_07.support.TokenScenarios;
import com.example.payments.Amount;
import com.example.payments.PaymentScheme;
import com.example.tokens.TokenValidator;

/**
 * Module 7.5 lab – explicit conversion with {@code @ConvertWith}.
 * <p>
 * An explicit converter is attached to one parameter and wins over implicit and
 * fallback conversion. Three forms:
 * <ul>
 *   <li>{@code @ConvertWith(SomeConverter.class)} on the parameter;</li>
 *   <li>a composed annotation such as {@link HumanDuration};</li>
 *   <li>a built-in one: {@code @JavaTimeConversionPattern} for dates in another format.</li>
 * </ul>
 */
class ExplicitConversionTest {

    @ParameterizedTest
    @CsvSource({
        "90s, -60s, false",  // expired 60 s ago, 90 s skew: still accepted
        "30s, -60s, true",
        "0s,  -1s,  true",
        "5m,  -4m,  false"
    })
    void skewFromHumanText(@HumanDuration Duration skew, @ConvertWith(HumanDurationConverter.class) Duration expiresIn,
            boolean expired) {
        TokenValidator validator = new TokenValidator(ISSUER, AUDIENCE, CLOCK, skew);
        assertEquals(!expired, validator.isValid(TokenScenarios.claimsExpiringIn(expiresIn)));
    }

    @ParameterizedTest
    @CsvSource({ "EUR 100000.00, true", "EUR 100000.01, false", "USD 5.00, false" })
    void amountWithAConverter(@ConvertWith(AmountConverter.class) Amount amount, boolean allowed) {
        assertEquals(allowed, PaymentScheme.SEPA_INSTANT.allows(amount));
    }

    @ParameterizedTest
    @CsvSource({ "01-03-2026, 3", "31-12-2026, 12" })
    void dutchDateFormat(@JavaTimeConversionPattern("dd-MM-yyyy") LocalDate date, int month) {
        assertEquals(Month.of(month), date.getMonth());
    }
}
