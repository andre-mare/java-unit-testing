package com.example.module_04.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.money.Money;

import org.junit.jupiter.api.Test;

/**
 * Module 4.3 lab – {@link Money} implements two contracts at once.
 * <p>
 * Expect 12 tests: 5 from {@link EqualsContract}, 3 from {@link ComparableContract},
 * and 4 of its own. Note the choice of values:
 * <ul>
 *   <li>equal value {@code "10"} vs {@code "10.00"} – same money, different input scale</li>
 *   <li>not-equal value {@code USD 10.00} – same number, different currency</li>
 * </ul>
 */
class MoneyTest implements EqualsContract<Money>, ComparableContract<Money> {

    @Override
    public Money createValue() {
        return Money.of("10.00", "EUR");
    }

    @Override
    public Money createEqualValue() {
        return Money.of("10", "EUR");
    }

    @Override
    public Money createNotEqualValue() {
        return Money.of("10.00", "USD");
    }

    @Override
    public Money createSmallerValue() {
        return Money.of("9.99", "EUR");
    }

    @Test
    void addsAmountsInTheSameCurrency() {
        assertEquals(Money.of("12.50", "EUR"), Money.of("10", "EUR").plus(Money.of("2.5", "EUR")));
    }

    @Test
    void refusesToMixCurrencies() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
            () -> Money.of("10", "EUR").compareTo(Money.of("10", "USD")));
        assertEquals("Currency mismatch: EUR 10.00 vs USD 10.00", e.getMessage());
    }

    @Test
    void rejectsMoreDecimalsThanTheCurrencyAllows() {
        assertThrows(IllegalArgumentException.class, () -> Money.of("100.5", "JPY"));
    }

    @Test
    void printsCurrencyAndAmount() {
        assertEquals("EUR 10.00", Money.of("10", "EUR").toString());
    }
}
