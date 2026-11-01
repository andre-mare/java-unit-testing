package com.example.module_08.section_01;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.platform.commons.support.AnnotationSupport;

/**
 * Module 8.1 – the extension behind {@link ForEachCurrency}.
 * <p>
 * A {@link TestTemplateInvocationContextProvider} answers two questions:
 * <ol>
 *   <li>{@code supportsTestTemplate}: is this template mine? Here: does the method
 *       take a {@link Currency}?</li>
 *   <li>{@code provideTestTemplateInvocationContexts}: one context per invocation.
 *       Each context names the invocation ({@code getDisplayName}) and adds
 *       extensions for that invocation only ({@code getAdditionalExtensions}), here
 *       a {@link ParameterResolver} that hands over the currency.</li>
 * </ol>
 * Without {@code @ForEachCurrency} (plain {@code @TestTemplate} plus
 * {@code @ExtendWith}) it uses EUR, USD and GBP. A provider returning no contexts
 * makes the template fail, unless {@code mayReturnZeroTestTemplateInvocationContexts}
 * says that is fine.
 */
public class CurrencyInvocationContextProvider implements TestTemplateInvocationContextProvider {

    private static final List<String> DEFAULT_CODES = List.of("EUR", "USD", "GBP");

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return Arrays.asList(context.getRequiredTestMethod().getParameterTypes()).contains(Currency.class);
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        List<String> codes = annotation(context).map(a -> List.of(a.value())).orElse(DEFAULT_CODES);
        return codes.stream().map(Currency::getInstance).map(CurrencyInvocationContextProvider::invocation);
    }

    @Override
    public boolean mayReturnZeroTestTemplateInvocationContexts(ExtensionContext context) {
        return annotation(context).map(ForEachCurrency::allowZero).orElse(false);
    }

    private static Optional<ForEachCurrency> annotation(ExtensionContext context) {
        Method method = context.getRequiredTestMethod();
        return AnnotationSupport.findAnnotation(method, ForEachCurrency.class);
    }

    private static TestTemplateInvocationContext invocation(Currency currency) {
        return new TestTemplateInvocationContext() {

            @Override
            public String getDisplayName(int invocationIndex) {
                return "[" + invocationIndex + "] " + currency.getCurrencyCode();
            }

            @Override
            public List<Extension> getAdditionalExtensions() {
                return List.of(new CurrencyResolver(currency));
            }
        };
    }

    /** Resolves {@code Currency} parameters for one invocation. */
    private record CurrencyResolver(Currency currency) implements ParameterResolver {

        @Override
        public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
            return parameterContext.getParameter().getType() == Currency.class;
        }

        @Override
        public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
            return currency;
        }
    }
}
