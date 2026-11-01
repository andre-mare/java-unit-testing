package com.example.module_07.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.iban.Iban;
import com.example.iban.IbanValidator;
import com.example.payments.Amount;
import com.example.payments.PaymentScheme;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Module 7.3 – {@code @CsvSource}: a small table, written inline.
 * <p>
 * Each string (or each line of a {@code textBlock}) is one invocation. The rules:
 * <ul>
 *   <li>columns are split on {@code ,} (change with {@code delimiter} or {@code delimiterString});</li>
 *   <li>{@code '...'} quotes a value containing the delimiter (change with {@code quoteCharacter});</li>
 *   <li>an unquoted empty value is {@code null}; {@code ''} is an empty string
 *       (or {@code emptyValue}); {@code nullValues} names extra null markers;</li>
 *   <li>leading and trailing spaces are trimmed unless
 *       {@code ignoreLeadingAndTrailingWhitespace = false};</li>
 *   <li>in a text block, {@code #} starts a comment line and the first line can be a header.</li>
 * </ul>
 */
class CsvSourceTest {

    @ParameterizedTest
    @CsvSource({
        "NL91ABNA0417164300,     NL, 18",
        "DE89370400440532013000, DE, 22",
        "'NL91 ABNA 0417 1643 00', NL, 18" // quoted: contains spaces (no commas, but quotes are also fine)
    })
    void inlineRows(String iban, String country, int length) {
        Iban parsed = Iban.parse(iban);
        assertEquals(country, parsed.countryCode());
        assertEquals(length, parsed.toString().length());
    }

    @ParameterizedTest(name = "[{index}] {arguments}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
        SCHEME,               AMOUNT,          ALLOWED
        # SEPA moves euros only
        SEPA_CREDIT_TRANSFER, EUR 5000.00,     true
        SEPA_CREDIT_TRANSFER, GBP 5000.00,     false
        # the instant maximum in this example
        SEPA_INSTANT,         EUR 100000.00,   true
        SEPA_INSTANT,         EUR 100000.01,   false
        SWIFT_WIRE,           USD 2500000.00,  true
        """)
    void textBlockWithHeadersAndComments(PaymentScheme scheme, String amount, boolean allowed) {
        assertEquals(allowed, scheme.allows(Amount.parse(amount)));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', quoteCharacter = '"', textBlock = """
        NL91ABNA0417164300     | "Rent, March"
        DE89370400440532013000 | Invoice 42
        """)
    void otherDelimiterAndQuote(String iban, String reference) {
        assertEquals(true, IbanValidator.isValid(iban));
        assertEquals(true, reference.contains(" "));
    }

    @ParameterizedTest
    @CsvSource(value = { "NL91ABNA0417164300, NIL", "DE89370400440532013000, " }, nullValues = "NIL")
    void nullValues(String iban, String reference) {
        assertNull(reference, "NIL and an unquoted empty value both become null");
    }

    @ParameterizedTest
    @CsvSource(value = { "NL91ABNA0417164300, ''" }, emptyValue = "(none)")
    void emptyValue(String iban, String reference) {
        assertEquals("(none)", reference, "a quoted empty value becomes emptyValue");
    }

    @ParameterizedTest
    @CsvSource(value = { "'  padded  ',  padded  " }, ignoreLeadingAndTrailingWhitespace = false)
    void keepWhitespace(String quoted, String unquoted) {
        assertEquals("  padded  ", quoted);
        assertEquals("  padded  ", unquoted);
    }
}
