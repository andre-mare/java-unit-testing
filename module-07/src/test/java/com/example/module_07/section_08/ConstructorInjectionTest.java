package com.example.module_07.section_08;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import com.example.iban.Iban;
import com.example.payments.Amount;
import com.example.payments.PaymentScheme;
import com.example.payments.Transfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Module 7.8 – {@code @ParameterizedClass} with constructor injection.
 * <p>
 * No {@code @Parameter} fields, so the arguments go to the constructor. Conversion
 * works as for methods: the text becomes {@code Iban} through fallback conversion
 * (7.5). Other constructor parameters, like {@code TestInfo}, come after the
 * indexed ones and are resolved as usual.
 */
@ParameterizedClass
@CsvSource({
    "NL91ABNA0417164300, NL20INGB0001234567, false",
    "NL91ABNA0417164300, BE68539007547034,   true",
    "GB29NWBK60161331926819, DE89370400440532013000, true"
})
class ConstructorInjectionTest {

    private final Iban debtor;
    private final Iban creditor;
    private final boolean crossBorder;
    private Transfer transfer;

    ConstructorInjectionTest(Iban debtor, Iban creditor, boolean crossBorder, TestInfo info) {
        this.debtor = debtor;
        this.creditor = creditor;
        this.crossBorder = crossBorder;
        assertEquals("ConstructorInjectionTest", info.getTestClass().orElseThrow().getSimpleName());
    }

    @BeforeEach
    void createTransfer() {
        transfer = new Transfer(debtor, creditor, Amount.parse("EUR 42.00"), LocalDate.of(2026, 3, 2),
            PaymentScheme.SWIFT_WIRE, "Test");
    }

    @Test
    void crossBorderFollowsTheCountries() {
        assertEquals(crossBorder, transfer.isCrossBorder());
    }

    @Test
    void cannotPayYourself() {
        assertThrows(IllegalArgumentException.class, () -> new Transfer(debtor, debtor, transfer.amount(),
            transfer.executionDate(), transfer.scheme(), transfer.reference()));
    }
}
