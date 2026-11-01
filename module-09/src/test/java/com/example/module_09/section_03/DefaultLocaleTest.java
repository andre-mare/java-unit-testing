package com.example.module_09.section_03;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import com.example.display.AmountFormatter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.util.DefaultLocale;
import org.junit.jupiter.api.util.LocaleProvider;

/**
 * Module 9.3 lab – {@code @DefaultLocale} (new built-in in JUnit 6.1).
 * <p>
 * Sets {@code Locale.getDefault()} for the class or one test and restores it
 * afterwards. Three ways to name the locale, never combined:
 * <ul>
 *   <li>a language tag: {@code @DefaultLocale("nl-NL")};</li>
 *   <li>parts: {@code language}, {@code country}, {@code variant};</li>
 *   <li>a {@code localeProvider} class, for a locale computed in code.</li>
 * </ul>
 * A method-level annotation overrides the class-level one. Exact currency strings
 * differ between JDK versions (non-breaking spaces, symbol placement), so the tests
 * check the grouping and decimal separators, and that the default-locale method
 * agrees with the explicit-locale one.
 */
@DefaultLocale("en-GB")
class DefaultLocaleTest {

    private static final BigDecimal AMOUNT = new BigDecimal("1234.56");
    private static final Currency EUR = Currency.getInstance("EUR");

    @Test
    void classLevelLocaleApplies() {
        assertEquals(Locale.UK, Locale.getDefault());
        assertEquals("1,234.56", AmountFormatter.formatNumber(AMOUNT));
    }

    @Test
    @DefaultLocale("nl-NL")
    void dutchUsesDotsForThousandsAndACommaForDecimals() {
        assertThat(AmountFormatter.format(AMOUNT, EUR)).contains("1.234,56").contains("€");
        assertEquals(AmountFormatter.format(AMOUNT, EUR, Locale.forLanguageTag("nl-NL")),
            AmountFormatter.format(AMOUNT, EUR));
    }

    @Test
    @DefaultLocale(language = "en", country = "US")
    void americanUsesCommasForThousandsAndADotForDecimals() {
        assertThat(AmountFormatter.format(AMOUNT, EUR)).contains("1,234.56").contains("€");
        assertThat(AmountFormatter.format(AMOUNT, Currency.getInstance("USD"))).startsWith("$");
    }

    @Test
    @DefaultLocale(localeProvider = BelgianFrench.class)
    void computedLocale() {
        assertEquals("BE", Locale.getDefault().getCountry());
        assertThat(AmountFormatter.formatNumber(AMOUNT)).endsWith(",56");
    }

    /** A {@link LocaleProvider} is a {@code Supplier<Locale>} with a no-argument constructor. */
    public static class BelgianFrench implements LocaleProvider {

        @Override
        public Locale get() {
            return Locale.forLanguageTag("fr-BE");
        }
    }
}
