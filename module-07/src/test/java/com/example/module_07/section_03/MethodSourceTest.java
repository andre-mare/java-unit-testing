package com.example.module_07.section_03;

import static com.example.module_07.support.TokenScenarios.validator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.Duration;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.example.iban.Iban;
import com.example.iban.IbanGenerator;
import com.example.iban.IbanValidator;
import com.example.module_07.support.TokenScenarios;
import com.example.tokens.ClaimProblem;
import com.example.tokens.Claims;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Module 7.3 lab – {@code @MethodSource}: arguments from a factory method.
 * <p>
 * The most flexible source: any Java code can build the arguments, including
 * objects that cannot be written as literals. A factory returns a stream (or a
 * collection, iterable, iterator or array) of single values, {@code Object[]}
 * or {@link Arguments}.
 * <ul>
 *   <li>Factories in the test class must be {@code static}, unless the class uses
 *       {@code PER_CLASS} (Module 4.1). External factories are always {@code static}.</li>
 *   <li>No name given: JUnit looks for a factory with the same name as the test.</li>
 *   <li>Another class: {@code "fully.qualified.Class#method"}.</li>
 *   <li>A returned {@code Stream} is closed by JUnit, so {@code Files.lines(...)} is safe.</li>
 * </ul>
 */
class MethodSourceTest {

    static Stream<Iban> generatedIbans() {
        IbanGenerator generator = new IbanGenerator(new Random(7));
        return Stream.generate(generator::nextDutchIban).limit(5);
    }

    @ParameterizedTest
    @MethodSource("generatedIbans")
    void explicitFactoryName(Iban iban) {
        assertTrue(IbanValidator.isValid(iban.formatted()));
    }

    static Stream<String> lowerCaseIbans() {
        return Stream.of("nl91abna0417164300", "de89370400440532013000");
    }

    @ParameterizedTest
    @MethodSource // same name as the test method
    void lowerCaseIbans(String iban) {
        assertTrue(IbanValidator.isValid(iban));
    }

    static IntStream hoursAhead() {
        return IntStream.rangeClosed(1, 4);
    }

    @ParameterizedTest
    @MethodSource("hoursAhead")
    void primitiveStream(int hours) {
        assertTrue(validator().isValid(TokenScenarios.claimsExpiringIn(Duration.ofHours(hours))));
    }

    static Stream<Arguments> claimsWithTheirProblem() {
        return TokenScenarios.singleProblems();
    }

    @ParameterizedTest
    @MethodSource("claimsWithTheirProblem")
    void severalArgumentsPerInvocation(Claims claims, ClaimProblem expected) {
        assertEquals(Set.of(expected), validator().validate(claims));
    }

    @ParameterizedTest
    @MethodSource("com.example.module_07.support.TokenScenarios#expiryScenarios")
    void externalFactory(Duration expiresIn, boolean expired) {
        Set<ClaimProblem> problems = validator().validate(TokenScenarios.claimsExpiringIn(expiresIn));
        assertEquals(expired, problems.contains(ClaimProblem.EXPIRED));
    }

    static Stream<Arguments> objectArrays() {
        return Stream.of(arguments("NL91ABNA0417164300", "NL"), arguments("BE68539007547034", "BE"));
    }

    @ParameterizedTest
    @MethodSource({ "objectArrays", "moreObjectArrays" }) // several factories, one after another
    void multipleFactories(String iban, String country) {
        assertEquals(country, Iban.parse(iban).countryCode());
    }

    static Stream<Object[]> moreObjectArrays() {
        return Stream.<Object[]>of(new Object[] { "DE89370400440532013000", "DE" });
    }
}
