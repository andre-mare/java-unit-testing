package com.example.iban;

import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;

/**
 * Generates valid Dutch test IBANs: a bank code plus ten random digits, with the
 * correct check digits. Pass a seeded {@link RandomGenerator} for repeatable output.
 */
public final class IbanGenerator {

    private static final List<String> DUTCH_BANK_CODES = List.of("ABNA", "INGB", "RABO", "TRIO", "BUNQ");

    private final RandomGenerator random;

    public IbanGenerator(RandomGenerator random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    public Iban nextDutchIban() {
        String bankCode = DUTCH_BANK_CODES.get(random.nextInt(DUTCH_BANK_CODES.size()));
        String accountNumber = "%010d".formatted(random.nextLong(10_000_000_000L));
        String bban = bankCode + accountNumber;
        return Iban.parse("NL" + IbanValidator.checkDigits("NL", bban) + bban);
    }
}
