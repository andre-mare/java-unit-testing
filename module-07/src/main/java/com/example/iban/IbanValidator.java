package com.example.iban;

import java.util.Locale;
import java.util.Map;

/**
 * Validates International Bank Account Numbers (ISO 13616), Module 7 production code.
 * <p>
 * Two checks: the length must match the country, and the ISO 7064 mod-97
 * checksum must be 1. Spaces are ignored and letters may be lower case.
 * Only the countries in {@link #LENGTHS} are supported in this example.
 */
public final class IbanValidator {

    /** IBAN length per country for the supported countries. */
    public static final Map<String, Integer> LENGTHS = Map.of(
        "AT", 20, "BE", 16, "CH", 21, "DE", 22, "ES", 24,
        "FR", 27, "GB", 22, "IT", 27, "LU", 20, "NL", 18);

    private IbanValidator() {
    }

    public static boolean isValid(String candidate) {
        if (candidate == null) {
            return false;
        }
        String iban = normalize(candidate);
        if (!iban.matches("[A-Z]{2}[0-9]{2}[A-Z0-9]+")) {
            return false;
        }
        Integer expectedLength = LENGTHS.get(iban.substring(0, 2));
        if (expectedLength == null || iban.length() != expectedLength) {
            return false;
        }
        return mod97(iban.substring(4) + iban.substring(0, 4)) == 1;
    }

    /**
     * The two check digits for a country and basic bank account number (BBAN).
     * {@code checkDigits("NL", "ABNA0417164300")} returns {@code "91"}.
     */
    public static String checkDigits(String countryCode, String bban) {
        int remainder = mod97(normalize(bban) + normalize(countryCode) + "00");
        return "%02d".formatted(98 - remainder);
    }

    /** Removes spaces and upper-cases. */
    static String normalize(String text) {
        return text.replace(" ", "").toUpperCase(Locale.ROOT);
    }

    /** ISO 7064 mod-97 of a string of digits and letters, where A = 10 ... Z = 35. */
    private static int mod97(String digitsAndLetters) {
        int remainder = 0;
        for (char c : digitsAndLetters.toCharArray()) {
            int value = Character.isDigit(c) ? c - '0' : c - 'A' + 10;
            remainder = value < 10 ? (remainder * 10 + value) % 97 : (remainder * 100 + value) % 97;
        }
        return remainder;
    }
}
