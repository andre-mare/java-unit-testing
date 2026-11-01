package com.example.module_07.section_05;

import com.example.payments.Amount;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

/**
 * Module 7.5 – an untyped converter: you check the types yourself.
 * <p>
 * Converts {@code "EUR 12.50"} with {@link Amount#parse(String)}. Needed because
 * {@code Amount} has two static {@code String} factories, so fallback conversion
 * does not apply.
 */
public class AmountConverter extends SimpleArgumentConverter {

    @Override
    protected Object convert(Object source, Class<?> targetType) {
        if (!(source instanceof String text) || targetType != Amount.class) {
            throw new ArgumentConversionException("Cannot convert " + source + " to " + targetType.getName());
        }
        try {
            return Amount.parse(text);
        } catch (IllegalArgumentException e) {
            throw new ArgumentConversionException("Not an amount: " + text, e);
        }
    }
}
