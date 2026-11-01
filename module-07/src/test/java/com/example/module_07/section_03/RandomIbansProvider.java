package com.example.module_07.section_03;

import java.util.Random;
import java.util.stream.Stream;

import com.example.iban.IbanGenerator;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

/**
 * Module 7.3 – a reusable {@link ArgumentsProvider}.
 * <p>
 * Ten generated Dutch IBANs from a fixed seed: the same ten on every run.
 * A provider must be a top-level or {@code static} nested class. In JUnit 6,
 * {@code provideArguments} also receives the {@link ParameterDeclarations} of the
 * method or class being parameterized.
 */
public class RandomIbansProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ParameterDeclarations parameters, ExtensionContext context) {
        IbanGenerator generator = new IbanGenerator(new Random(2026));
        return Stream.generate(generator::nextDutchIban).limit(10).map(Arguments::of);
    }
}
