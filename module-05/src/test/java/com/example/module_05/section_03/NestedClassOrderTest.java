package com.example.module_05.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Module 5.3 – ordering {@code @Nested} classes with {@code @TestClassOrder}, and
 * how orderers are inherited.
 * <ul>
 *   <li>{@code @TestClassOrder(ClassOrderer.OrderAnnotation.class)} orders the nested
 *       classes by their {@code @Order}.</li>
 *   <li>{@code @TestMethodOrder} on the outer class is inherited by the nested classes:
 *       {@code Second} orders its methods by {@code @Order} without saying so.</li>
 *   <li>{@code Third} opts out with {@code MethodOrderer.Default}, back to JUnit's
 *       deterministic but non-obvious default.</li>
 * </ul>
 * Top-level classes are ordered with the {@code junit.jupiter.testclass.order.default}
 * configuration parameter instead, for example
 * {@code -Djunit.jupiter.testclass.order.default=org.junit.jupiter.api.ClassOrderer$OrderAnnotation}.
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NestedClassOrderTest {

    static final List<String> executed = new ArrayList<>();

    @BeforeAll
    static void reset() {
        executed.clear();
    }

    @AfterAll
    static void verifyOrder() {
        List<String> classesInOrder = executed.stream().map(e -> e.split("\\.")[0]).distinct().toList();
        assertEquals(List.of("First", "Second", "Third"), classesInOrder);
        assertEquals(List.of("Second.b", "Second.a"),
            executed.stream().filter(e -> e.startsWith("Second.")).toList(),
            "Second inherited @TestMethodOrder(OrderAnnotation) from the outer class");
    }

    @Nested
    @Order(3)
    @TestMethodOrder(MethodOrderer.Default.class)
    class Third {

        @Test
        void x() {
            executed.add("Third.x");
        }

        @Test
        void y() {
            executed.add("Third.y");
        }
    }

    @Nested
    @Order(1)
    class First {

        @Test
        void only() {
            executed.add("First.only");
        }
    }

    @Nested
    @Order(2)
    class Second {

        @Test
        @Order(2)
        void a() {
            executed.add("Second.a");
        }

        @Test
        @Order(1)
        void b() {
            executed.add("Second.b");
        }
    }
}
