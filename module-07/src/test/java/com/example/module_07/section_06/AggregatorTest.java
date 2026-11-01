package com.example.module_07.section_06;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.example.payments.PaymentScheme;
import com.example.payments.Transfer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvFileSource;

/**
 * Module 7.6 lab – {@code @AggregateWith} and a composed annotation.
 * <p>
 * Rules for parameter order: indexed parameters first, then aggregators
 * ({@code @AggregateWith} or {@code ArgumentsAccessor}), then parameters filled by
 * other resolvers such as {@code TestInfo}. An aggregator sees <em>all</em> columns,
 * so two aggregators can read the same row.
 */
class AggregatorTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/transfers.csv", numLinesToSkip = 1)
    void withAggregateWith(@AggregateWith(TransferAggregator.class) Transfer transfer) {
        assertFalse(transfer.debtor().equals(transfer.creditor()));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/transfers.csv", numLinesToSkip = 1)
    void withComposedAnnotation(@CsvToTransfer Transfer transfer, ArgumentsAccessor row) {
        assertEquals(row.getBoolean(6), transfer.isCrossBorder());
        if (transfer.scheme() == PaymentScheme.SWIFT_WIRE) {
            assertEquals("", transfer.reference(), "empty CSV value, null, then \"\" in Transfer");
        }
    }
}
