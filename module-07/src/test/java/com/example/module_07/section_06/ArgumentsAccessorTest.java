package com.example.module_07.section_06;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import com.example.iban.Iban;
import com.example.payments.Amount;
import com.example.payments.PaymentScheme;
import com.example.payments.Transfer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Module 7.6 – {@link ArgumentsAccessor}: all arguments in one parameter.
 * <p>
 * Seven columns would mean seven parameters. Instead, take an
 * {@code ArgumentsAccessor} and read the columns by index. {@code get(i, Type.class)}
 * applies the same implicit and fallback conversion as for parameters (so
 * {@code Iban} works, see 7.5); {@code getString}, {@code getInteger},
 * {@code getBoolean} and friends are shortcuts.
 */
class ArgumentsAccessorTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/transfers.csv", numLinesToSkip = 1)
    void eachRowIsAValidTransfer(ArgumentsAccessor row) {
        Transfer transfer = toTransfer(row);
        assertEquals(row.getBoolean(6), transfer.isCrossBorder());
        assertTrue(transfer.reference().length() <= Transfer.MAX_REFERENCE_LENGTH);
        assertEquals(7, row.size());
    }

    @ParameterizedTest(name = "[{index}] rejected: {6}")
    @CsvSource({
        "NL91ABNA0417164300, NL91ABNA0417164300, EUR 10.00,     2026-03-01, SEPA_INSTANT,         x, same account",
        "NL91ABNA0417164300, BE68539007547034,   EUR 0.00,      2026-03-01, SEPA_INSTANT,         x, zero amount",
        "NL91ABNA0417164300, BE68539007547034,   GBP 10.00,     2026-03-01, SEPA_CREDIT_TRANSFER, x, SEPA in pounds",
        "NL91ABNA0417164300, BE68539007547034,   EUR 100000.01, 2026-03-01, SEPA_INSTANT,         x, over instant maximum"
    })
    void invalidRowsAreRejected(ArgumentsAccessor row) {
        assertThrows(IllegalArgumentException.class, () -> toTransfer(row));
    }

    static Transfer toTransfer(ArgumentsAccessor row) {
        return new Transfer(
            row.get(0, Iban.class),
            row.get(1, Iban.class),
            Amount.parse(row.getString(2)),   // two String factories: no fallback, parse by hand
            row.get(3, LocalDate.class),
            row.get(4, PaymentScheme.class),
            row.getString(5));                // an empty CSV value arrives as null
    }
}
