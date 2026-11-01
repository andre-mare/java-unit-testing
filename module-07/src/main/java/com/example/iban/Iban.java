package com.example.iban;

/**
 * A valid IBAN, stored without spaces in upper case.
 * <p>
 * The only way to create one is {@link #parse(String)}. That single public
 * static factory method taking a {@code String} also lets JUnit convert CSV text
 * into an {@code Iban} automatically (Module 7.5, fallback conversion).
 */
public final class Iban {

    private final String value;

    private Iban(String value) {
        this.value = value;
    }

    /**
     * Parses and validates an IBAN, with or without spaces.
     *
     * @throws IllegalArgumentException when the text is blank or not a valid IBAN
     */
    public static Iban parse(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("IBAN must not be blank");
        }
        if (!IbanValidator.isValid(text)) {
            throw new IllegalArgumentException("Invalid IBAN: " + text);
        }
        return new Iban(IbanValidator.normalize(text));
    }

    public String countryCode() {
        return value.substring(0, 2);
    }

    public String checkDigits() {
        return value.substring(2, 4);
    }

    /** The basic bank account number: everything after country and check digits. */
    public String bban() {
        return value.substring(4);
    }

    /** Printed in groups of four: "NL91 ABNA 0417 1643 00". */
    public String formatted() {
        return value.replaceAll("(.{4})(?!$)", "$1 ");
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Iban iban && value.equals(iban.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /** The electronic form, without spaces. */
    @Override
    public String toString() {
        return value;
    }
}
