package com.example.module_07.section_06;

import java.time.LocalDate;

import com.example.iban.Iban;
import com.example.payments.Amount;
import com.example.payments.PaymentScheme;
import com.example.payments.Transfer;

import org.junit.jupiter.api.extension.AnnotatedElementContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.SimpleArgumentsAggregator;

/**
 * Module 7.6 – a reusable aggregator: the first six columns become a {@link Transfer}.
 * <p>
 * Write the mapping once, use it in every test that reads transfer rows.
 * {@link SimpleArgumentsAggregator} is the convenient base class; the JUnit 6
 * signature passes the {@link AnnotatedElementContext} of the parameter (or field,
 * for parameterized classes) and its index. Must be top-level or static nested,
 * with a no-argument constructor.
 */
public class TransferAggregator extends SimpleArgumentsAggregator {

    @Override
    protected Transfer aggregateArguments(ArgumentsAccessor row, Class<?> targetType,
            AnnotatedElementContext context, int parameterIndex) {
        if (targetType != Transfer.class) {
            throw new ArgumentsAggregationException("Can only create a Transfer, not " + targetType.getName());
        }
        return new Transfer(
            row.get(0, Iban.class),
            row.get(1, Iban.class),
            Amount.parse(row.getString(2)),
            row.get(3, LocalDate.class),
            row.get(4, PaymentScheme.class),
            row.getString(5));
    }
}
