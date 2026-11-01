package com.example.module_07.section_05;

import java.time.Duration;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.TypedArgumentConverter;

/**
 * Module 7.5 – a typed converter: {@code "90s"}, {@code "5m"}, {@code "2h"} to a {@link Duration}.
 * <p>
 * {@link TypedArgumentConverter} checks the source and target types for you;
 * you only write {@code convert}. A converter must have a no-argument constructor.
 * Throw {@link ArgumentConversionException} for bad input: JUnit reports it with
 * the invocation that failed.
 */
public class HumanDurationConverter extends TypedArgumentConverter<String, Duration> {

    public HumanDurationConverter() {
        super(String.class, Duration.class);
    }

    @Override
    protected Duration convert(String text) {
        if (text == null || text.length() < 2) {
            throw new ArgumentConversionException("Not a duration: " + text);
        }
        long number = Long.parseLong(text.substring(0, text.length() - 1));
        return switch (text.charAt(text.length() - 1)) {
            case 's' -> Duration.ofSeconds(number);
            case 'm' -> Duration.ofMinutes(number);
            case 'h' -> Duration.ofHours(number);
            default -> throw new ArgumentConversionException("Unknown unit in: " + text);
        };
    }
}
