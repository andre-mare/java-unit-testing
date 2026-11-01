package com.example.module_01.section_04;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.calculator.Calculator;

/**
 * Module 1.4 – the four lifecycle methods and the order JUnit calls them in.
 * <p>
 * Run this class and read the console. Expected order:
 * <pre>
 * @BeforeAll                       (once, static)
 *   constructor  -> @BeforeEach -> test 1 -> @AfterEach
 *   constructor  -> @BeforeEach -> test 2 -> @AfterEach
 * @AfterAll                        (once, static)
 * </pre>
 * Note the constructor runs before EVERY test: by default JUnit creates a new
 * instance per test method ("per-method" lifecycle, Module 4.1). That is why the
 * instance counter field below is always 1 and the static one keeps growing.
 */
class LifecycleDemoTest {

    private static int instancesCreated = 0;

    private int callsOnThisInstance = 0;
    private Calculator calculator;

    LifecycleDemoTest() {
        instancesCreated++;
        System.out.println("  constructor (instance #" + instancesCreated + ")");
    }

    @BeforeAll
    static void initAll() {
        System.out.println("@BeforeAll – e.g. start a shared, expensive resource");
    }

    @BeforeEach
    void init() {
        calculator = new Calculator();
        callsOnThisInstance++;
        System.out.println("    @BeforeEach – fresh Calculator");
    }

    @Test
    void firstTest() {
        System.out.println("      firstTest");
        assertEquals(1, callsOnThisInstance, "each test gets its own instance");
        assertEquals(4, calculator.add(2, 2));
    }

    @Test
    void secondTest() {
        System.out.println("      secondTest");
        assertEquals(1, callsOnThisInstance, "each test gets its own instance");
        assertEquals(6, calculator.multiply(2, 3));
    }

    @AfterEach
    void tearDown() {
        System.out.println("    @AfterEach – clean up per-test state");
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("@AfterAll – " + instancesCreated + " instances were created");
    }
}
