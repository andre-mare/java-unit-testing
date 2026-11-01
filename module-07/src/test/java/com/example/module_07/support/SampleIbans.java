package com.example.module_07.support;

import java.util.List;

/**
 * Module 7 test data: well-known example IBANs, valid and broken.
 * <p>
 * Public static fields can be referenced from another class with
 * {@code @FieldSource("com.example.m07_data_driven_tests.support.SampleIbans#VALID")}.
 */
public final class SampleIbans {

    public static final String NL = "NL91ABNA0417164300";
    public static final String DE = "DE89370400440532013000";
    public static final String BE = "BE68539007547034";
    public static final String FR = "FR1420041010050500013M02606";
    public static final String GB = "GB29NWBK60161331926819";

    /** One valid example IBAN per supported country. */
    public static final List<String> VALID = List.of(
        NL, DE, BE, FR, GB,
        "ES9121000418450200051332", "IT60X0542811101000000123456", "LU280019400644750000",
        "AT611904300234573201", "CH9300762011623852957");

    /** Broken IBANs: wrong checksum, wrong length, unknown country, bad character. */
    public static final List<String> INVALID = List.of(
        "NL91ABNA0417164301", "NL91ABNA041716430", "XX91ABNA0417164300", "NL9!ABNA0417164300");

    private SampleIbans() {
    }
}
