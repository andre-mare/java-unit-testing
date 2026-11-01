package com.example.module_07.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.example.iban.Iban;
import com.example.iban.IbanValidator;
import com.example.payments.Amount;
import com.example.payments.PaymentScheme;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

/**
 * Module 7.3 – {@code @FieldSource}: arguments from a field.
 * <p>
 * Simpler than a factory method when the data is fixed. The field holds a
 * collection, an iterable, an array, or a {@link Supplier} of a stream. A plain
 * {@code Stream} or {@code Iterator} field is not allowed, because it can only be
 * read once; wrap it in a {@code Supplier}. Fields must be {@code static} (unless
 * {@code PER_CLASS}); fields in other classes are referenced as {@code Class#field}.
 */
class FieldSourceTest {

    static final String[] dutchIbans = { "NL91ABNA0417164300", "NL20INGB0001234567" };

    static final List<String> belgianIbans = List.of("BE68539007547034");

    static final Supplier<Stream<Arguments>> schemeLimits = () -> Stream.of(
        arguments(PaymentScheme.SEPA_INSTANT, "EUR 100000.00", true),
        arguments(PaymentScheme.SEPA_INSTANT, "EUR 100000.01", false),
        arguments(PaymentScheme.SEPA_CREDIT_TRANSFER, "EUR 999999.99", true));

    @ParameterizedTest
    @FieldSource // field with the same name as the test method
    void dutchIbans(String iban) {
        assertEquals("NL", Iban.parse(iban).countryCode());
    }

    @ParameterizedTest
    @FieldSource({ "dutchIbans", "belgianIbans" }) // several fields: 3 invocations
    void severalFields(String iban) {
        assertTrue(IbanValidator.isValid(iban));
    }

    @ParameterizedTest
    @FieldSource("schemeLimits")
    void supplierOfAStream(PaymentScheme scheme, String amount, boolean allowed) {
        assertEquals(allowed, scheme.allows(Amount.parse(amount)));
    }

    @ParameterizedTest
    @FieldSource("com.example.module_07.support.SampleIbans#VALID")
    void externalField(String iban) {
        assertTrue(IbanValidator.isValid(iban));
    }

    @ParameterizedTest
    @FieldSource("com.example.module_07.support.SampleIbans#INVALID")
    void externalFieldOfBrokenIbans(String iban) {
        assertFalse(IbanValidator.isValid(iban));
    }
}
