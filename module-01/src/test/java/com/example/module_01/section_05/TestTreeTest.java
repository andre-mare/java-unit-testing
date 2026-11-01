package com.example.module_01.section_05;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.stream.Stream;

import com.example.calculator.Calculator;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Module 1.5 lab – definitions: <em>containers</em> vs <em>tests</em> in the test tree.
 * <p>
 * Run this class in your IDE and label every node of the result tree:
 * <pre>
 * TestTreeTest                           container (test class)
 * ├─ plainTest()                         TEST           (@Test)
 * ├─ repeated()                          container      (@RepeatedTest)
 * │  ├─ repetition 1 of 3                test
 * │  ├─ repetition 2 of 3                test
 * │  └─ repetition 3 of 3                test
 * ├─ primes(int)                         container      (@ParameterizedTest)
 * │  ├─ [1] number = 2                   test
 * │  └─ ...
 * └─ generated()                         container      (@TestFactory)
 *    ├─ 7 is prime                       test (dynamic)
 *    └─ ...
 * </pre>
 * Only {@code @Test} produces a test directly. The other test-method annotations
 * create a container that groups tests. Each is covered in depth later:
 * repeated and parameterized tests in Module 7, test factories in Module 8.
 */
class TestTreeTest {

    private final Calculator calculator = new Calculator();

    @Test
    void plainTest() {
        assertTrue(calculator.isPrime(2));
    }

    @RepeatedTest(3)
    void repeated() {
        assertTrue(calculator.isPrime(3));
    }

    @ParameterizedTest
    @ValueSource(ints = { 2, 3, 5, 7, 11 })
    void primes(int number) {
        assertTrue(calculator.isPrime(number));
    }

    @TestFactory
    Stream<DynamicTest> generated() {
        return Stream.of(7, 17, 97)
            .map(n -> dynamicTest(n + " is prime", () -> assertTrue(calculator.isPrime(n))));
    }
}
