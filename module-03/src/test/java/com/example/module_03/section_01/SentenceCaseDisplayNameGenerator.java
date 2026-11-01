package com.example.module_03.section_01;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayNameGenerator;

/**
 * Module 3.1 lab – a custom {@link DisplayNameGenerator}.
 * <p>
 * Turns ordinary camelCase Java names into sentences:
 * <ul>
 *   <li>class {@code PasswordPolicyRulesTest} becomes "Password policy rules"
 *       (a trailing {@code Test} or {@code Tests} is dropped)</li>
 *   <li>method {@code rejectsPasswordWithoutDigit()} becomes "Rejects password without digit"</li>
 *   <li>method {@code checksLength(int)} becomes "Checks length (int)"</li>
 * </ul>
 * A generator needs a public no-argument constructor, because JUnit creates it
 * by reflection. Only {@code generateDisplayNameForClass} is abstract; the two
 * other methods are overridden here in their JUnit 6 form, with the list of
 * enclosing instance types.
 */
public class SentenceCaseDisplayNameGenerator implements DisplayNameGenerator {

    /** Split before an uppercase letter or digit that follows a lowercase letter, and between digits and letters. */
    private static final String WORD_BOUNDARY = "(?<=[a-z])(?=[A-Z0-9])|(?<=[0-9])(?=[A-Za-z])";

    @Override
    public String generateDisplayNameForClass(Class<?> testClass) {
        return toSentence(stripTestSuffix(testClass.getSimpleName()));
    }

    @Override
    public String generateDisplayNameForNestedClass(List<Class<?>> enclosingInstanceTypes, Class<?> nestedClass) {
        return toSentence(stripTestSuffix(nestedClass.getSimpleName()));
    }

    @Override
    public String generateDisplayNameForMethod(List<Class<?>> enclosingInstanceTypes, Class<?> testClass,
            Method testMethod) {
        String sentence = toSentence(testMethod.getName());
        if (testMethod.getParameterCount() == 0) {
            return sentence;
        }
        return sentence + " " + DisplayNameGenerator.parameterTypesAsString(testMethod);
    }

    /** "rejectsPasswordWithoutDigit" becomes "Rejects password without digit". */
    static String toSentence(String camelCase) {
        if (camelCase.isEmpty()) {
            return camelCase;
        }
        String words = Arrays.stream(camelCase.split(WORD_BOUNDARY))
            .map(word -> word.toLowerCase(Locale.ROOT))
            .collect(Collectors.joining(" "));
        return Character.toUpperCase(words.charAt(0)) + words.substring(1);
    }

    /** Drops a trailing "Tests" or "Test" from a class name. */
    static String stripTestSuffix(String className) {
        if (className.endsWith("Tests")) {
            return className.substring(0, className.length() - "Tests".length());
        }
        if (className.endsWith("Test")) {
            return className.substring(0, className.length() - "Test".length());
        }
        return className;
    }
}
