package com.example.module_07.section_06;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.params.aggregator.AggregateWith;

/** Module 7.6 – composed annotation for {@code @AggregateWith(TransferAggregator.class)}. */
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@AggregateWith(TransferAggregator.class)
public @interface CsvToTransfer {
}
