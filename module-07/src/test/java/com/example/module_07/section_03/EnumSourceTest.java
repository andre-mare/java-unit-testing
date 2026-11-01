package com.example.module_07.section_03;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.junit.jupiter.params.provider.EnumSource.Mode.MATCH_ALL;

import com.example.payments.Amount;
import com.example.payments.PaymentScheme;
import com.example.tokens.ClaimProblem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Module 7.3 – {@code @EnumSource}: enum constants as arguments.
 * <ul>
 *   <li>{@code value} can be omitted when the parameter type is the enum itself;</li>
 *   <li>{@code names} picks constants; with {@code mode} you can exclude them
 *       ({@code EXCLUDE}) or match them by regular expression ({@code MATCH_ALL},
 *       {@code MATCH_ANY}, {@code MATCH_NONE});</li>
 *   <li>{@code from} / {@code to} select a range in declaration order.</li>
 * </ul>
 */
class EnumSourceTest {

    @ParameterizedTest
    @EnumSource // all constants of the parameter's enum type
    void everySchemeCarriesASmallEuroPayment(PaymentScheme scheme) {
        assertTrue(scheme.allows(Amount.euro("10.00")));
    }

    @ParameterizedTest
    @EnumSource(names = { "SEPA_CREDIT_TRANSFER", "SEPA_DIRECT_DEBIT" })
    void namedConstantsAreNotInstant(PaymentScheme scheme) {
        assertFalse(scheme.isInstant());
    }

    @ParameterizedTest
    @EnumSource(mode = EXCLUDE, names = "SWIFT_WIRE")
    void allButSwiftAreSepa(PaymentScheme scheme) {
        assertTrue(scheme.isSepa());
    }

    @ParameterizedTest
    @EnumSource(mode = MATCH_ALL, names = "^SEPA_.*")
    void sepaSchemesRefuseOtherCurrencies(PaymentScheme scheme) {
        assertFalse(scheme.allows(Amount.parse("GBP 10.00")));
    }

    @ParameterizedTest
    @EnumSource(value = ClaimProblem.class, from = "WRONG_ISSUER", to = "WRONG_AUDIENCE")
    void rangeOfConstants(ClaimProblem problem) {
        assertTrue(problem.name().startsWith("WRONG_"));
    }
}
