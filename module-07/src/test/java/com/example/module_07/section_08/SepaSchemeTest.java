package com.example.module_07.section_08;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.EnumSource.Mode.MATCH_ALL;

import java.time.LocalDate;

import com.example.iban.Iban;
import com.example.module_07.support.SampleIbans;
import com.example.payments.Amount;
import com.example.payments.PaymentScheme;
import com.example.payments.Transfer;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Module 7.8 – {@code @ParameterizedClass} with field injection.
 * <p>
 * The whole class runs once per argument: three SEPA schemes, so every test
 * below runs three times. Each invocation has the normal class lifecycle (new
 * instance, {@code @BeforeEach}, ...). A {@code @Parameter} field receives the
 * argument; with several arguments, give each field its index:
 * {@code @Parameter(0)}, {@code @Parameter(1)}. {@code @Nested} classes run inside
 * every invocation and can use the field too.
 */
@ParameterizedClass(name = "[{index}] {0}")
@EnumSource(mode = MATCH_ALL, names = "^SEPA_.*")
class SepaSchemeTest {

    @Parameter
    PaymentScheme scheme;

    @Test
    void isSepa() {
        assertTrue(scheme.isSepa());
    }

    @Test
    void movesEuros() {
        assertTrue(scheme.allows(Amount.euro("250.00")));
    }

    @Test
    void refusesOtherCurrencies() {
        assertFalse(scheme.allows(Amount.parse("CHF 250.00")));
    }

    @Nested
    class Transfers {

        private final Iban debtor = Iban.parse(SampleIbans.NL);
        private final Iban creditor = Iban.parse(SampleIbans.DE);

        @Test
        void acceptsAEuroTransfer() {
            Transfer transfer = new Transfer(debtor, creditor, Amount.euro("99.95"), LocalDate.of(2026, 3, 2), scheme, "Order 7");
            assertTrue(transfer.isCrossBorder());
        }

        @Test
        void rejectsAPoundTransfer() {
            assertThrows(IllegalArgumentException.class, () -> new Transfer(debtor, creditor,
                Amount.parse("GBP 99.95"), LocalDate.of(2026, 3, 2), scheme, "Order 7"));
        }
    }
}
