package com.example.module_07.section_07;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.argumentSet;

import java.util.stream.Stream;

import com.example.iban.Iban;
import com.example.iban.IbanValidator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Module 7.7 lab – what each invocation is called in the report.
 * <p>
 * Every test below asserts its own display name, so this class documents the
 * placeholders and proves them. The default pattern is
 * {@code "[{index}] {argumentSetNameOrArgumentsWithNames}"}
 * ({@code ParameterizedInvocationConstants.DEFAULT_DISPLAY_NAME}).
 * <p>
 * Placeholders: {@code {index}}, {@code {0}}, {@code {1}}, ..., {@code {arguments}},
 * {@code {argumentsWithNames}} (needs the {@code -parameters} compiler flag),
 * {@code {argumentSetName}}, {@code {argumentSetNameOrArgumentsWithNames}} and
 * {@code {displayName}}. The pattern is a {@code MessageFormat}: write {@code ''}
 * for one apostrophe.
 * <p>
 * Since JUnit 6, text arguments are quoted: strings in {@code "..."}, characters in
 * {@code '...'}. Turn it off with {@code quoteTextArguments = false}.
 * Configuration parameters: {@code junit.jupiter.params.displayname.default} (the
 * default pattern) and {@code junit.jupiter.params.displayname.argument.maxlength}
 * (longer arguments are cut off, default 512).
 */
class InvocationDisplayNamesTest {

    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = { "NL91ABNA0417164300", "BE68539007547034" })
    void indexAndFirstArgument(String iban, ArgumentsAccessor accessor, TestInfo info) {
        assertEquals(accessor.getInvocationIndex() + ": \"" + iban + "\"", info.getDisplayName());
    }

    @ParameterizedTest(name = "{index}: {0}", quoteTextArguments = false)
    @ValueSource(strings = { "NL91ABNA0417164300" })
    void withoutQuotes(String iban, TestInfo info) {
        assertEquals("1: " + iban, info.getDisplayName());
    }

    @ParameterizedTest(name = "{0} has {1} characters")
    @CsvSource({ "NL91ABNA0417164300, 18" })
    void numbersAreNotQuoted(String iban, int length, TestInfo info) {
        assertEquals("\"" + iban + "\" has \"" + length + "\" characters", info.getDisplayName());
    }

    @ParameterizedTest(name = "{0} isn''t valid")
    @ValueSource(chars = { '!' })
    void apostropheAndCharQuotes(char bad, TestInfo info) {
        assertEquals("'!' isn't valid", info.getDisplayName());
        assertEquals(false, IbanValidator.isValid("NL91ABNA041716430" + bad));
    }

    @DisplayName("Country code")
    @ParameterizedTest(name = "{displayName} #{index}")
    @ValueSource(strings = { "DE89370400440532013000" })
    void methodDisplayName(String iban, TestInfo info) {
        assertEquals("Country code #1", info.getDisplayName());
        assertEquals("DE", Iban.parse(iban).countryCode());
    }

    @ParameterizedTest(name = "{argumentsWithNames}")
    @CsvSource({ "NL91ABNA0417164300, NL" })
    void argumentsWithNames(String iban, String country, TestInfo info) {
        assertEquals("iban = \"" + iban + "\", country = \"" + country + "\"", info.getDisplayName());
    }

    static Stream<Arguments> namedIbans() {
        return Stream.of(Arguments.of(Named.of("ABN AMRO example", "NL91ABNA0417164300")));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("namedIbans")
    void namedArgument(String iban, TestInfo info) {
        assertEquals("ABN AMRO example", info.getDisplayName(), "the name is shown, the payload is passed");
        assertEquals(18, iban.length());
    }

    static Stream<Arguments> argumentSets() {
        return Stream.of(
            argumentSet("Dutch, with spaces", "NL91 ABNA 0417 1643 00", "NL"),
            argumentSet("German, lower case", "de89370400440532013000", "DE"));
    }

    @ParameterizedTest // default pattern
    @MethodSource("argumentSets")
    void argumentSetNames(String iban, String country, ArgumentsAccessor accessor, TestInfo info) {
        String expectedName = accessor.getInvocationIndex() == 1 ? "Dutch, with spaces" : "German, lower case";
        assertEquals("[" + accessor.getInvocationIndex() + "] " + expectedName, info.getDisplayName());
        assertEquals(country, Iban.parse(iban).countryCode());
    }
}
