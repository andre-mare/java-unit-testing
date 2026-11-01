package com.example.module_04.section_02;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Module 4.2 – the order of lifecycle methods across nesting levels.
 * <p>
 * Every lifecycle method writes to a shared log. The inner test checks the log
 * so far, and the outer {@code @AfterAll} checks the complete sequence:
 * <pre>
 * outer @BeforeAll
 *   inner @BeforeAll
 *     outer @BeforeEach
 *       inner @BeforeEach
 *         inner test
 *       inner @AfterEach
 *     outer @AfterEach
 *   inner @AfterAll
 * outer @AfterAll
 * </pre>
 * "Before" methods run outside-in, "after" methods inside-out.
 * <p>
 * {@code static} methods inside an inner class require Java 16 or later; with
 * Java 17 as the course baseline, {@code @BeforeAll} works on every level.
 */
class NestedLifecycleOrderTest {

    static final List<String> log = new ArrayList<>();

    @BeforeAll
    static void outerBeforeAll() {
        log.clear();
        log.add("outer @BeforeAll");
    }

    @BeforeEach
    void outerBeforeEach() {
        log.add("outer @BeforeEach");
    }

    @AfterEach
    void outerAfterEach() {
        log.add("outer @AfterEach");
    }

    @AfterAll
    static void outerAfterAll() {
        assertEquals(List.of(
            "outer @BeforeAll",
            "inner @BeforeAll",
            "outer @BeforeEach",
            "inner @BeforeEach",
            "inner test",
            "inner @AfterEach",
            "outer @AfterEach",
            "inner @AfterAll"), log);
    }

    @Nested
    class Inner {

        @BeforeAll
        static void innerBeforeAll() {
            log.add("inner @BeforeAll");
        }

        @BeforeEach
        void innerBeforeEach() {
            log.add("inner @BeforeEach");
        }

        @Test
        void test() {
            log.add("inner test");
            assertEquals(List.of(
                "outer @BeforeAll",
                "inner @BeforeAll",
                "outer @BeforeEach",
                "inner @BeforeEach",
                "inner test"), log);
        }

        @AfterEach
        void innerAfterEach() {
            log.add("inner @AfterEach");
        }

        @AfterAll
        static void innerAfterAll() {
            log.add("inner @AfterAll");
        }
    }
}
