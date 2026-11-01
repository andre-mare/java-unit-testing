package com.example.display;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Formats money for people, Module 9 production code.
 * <p>
 * The methods without a {@link Locale} parameter use the JVM default <em>format</em> locale,
 * which is what makes them hard to test: the result depends on the machine. Module
 * 9.3 pins it with {@code @DefaultLocale}. The overloads that take a {@link Locale}
 * are the testable design, for comparison.
 */
public final class AmountFormatter {

    private AmountFormatter() {
    }

    /** In the default locale: "€ 1.234,56" in nl-NL, "€1,234.56" in en-US. */
    public static String format(BigDecimal amount, Currency currency) {
        return format(amount, currency, Locale.getDefault(Locale.Category.FORMAT));
    }

    public static String format(BigDecimal amount, Currency currency, Locale locale) {
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        format.setCurrency(currency);
        format.setMinimumFractionDigits(currency.getDefaultFractionDigits());
        format.setMaximumFractionDigits(currency.getDefaultFractionDigits());
        return format.format(amount);
    }

    /** Just the number, grouped and with the locale's decimal separator. */
    public static String formatNumber(BigDecimal amount) {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault(Locale.Category.FORMAT));
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        return format.format(amount);
    }
}
