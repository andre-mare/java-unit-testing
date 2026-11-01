package com.example.module_07.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.iban.IbanValidator;

import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

/**
 * Module 7.3 lab – {@code @CsvFileSource}: a table in a file.
 * <p>
 * The data lives in {@code src/test/resources/m07/ibans.csv}, where people who
 * do not read Java can maintain it. Read it from the classpath ({@code resources},
 * starting with {@code /}) or from the file system ({@code files}, relative to the
 * working directory, which is the module directory under Maven).
 * <p>
 * Differences from {@code @CsvSource}: the quote character is {@code "}, and the
 * header line is skipped with {@code numLinesToSkip} or used for display names
 * with {@code useHeadersInDisplayName}. Lines starting with {@code #} are comments.
 */
class CsvFileSourceTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/ibans.csv", numLinesToSkip = 1)
    void fromTheClasspath(String country, String iban, boolean valid) {
        assertEquals(valid, IbanValidator.isValid(iban));
        if (valid) {
            assertTrue(iban.startsWith(country));
        }
    }

    @ParameterizedTest
    @CsvFileSource(files = "src/test/resources/ibans.csv", useHeadersInDisplayName = true)
    void fromTheFileSystemWithHeaderNames(String country, String iban, boolean valid, TestInfo info) {
        assertEquals(valid, IbanValidator.isValid(iban));
        assertTrue(info.getDisplayName().contains("COUNTRY = "), info.getDisplayName());
    }
}
