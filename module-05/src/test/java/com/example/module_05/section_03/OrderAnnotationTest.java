package com.example.module_05.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Module 5.3 – how {@code MethodOrderer.OrderAnnotation} treats every case.
 * <ul>
 *   <li>Lower {@code @Order} values run first; negative values are allowed.</li>
 *   <li>A method WITHOUT {@code @Order} gets {@code Order.DEFAULT}
 *       (1073741823), so it runs after the small numbers but before anything
 *       ordered above the default.</li>
 * </ul>
 * {@code @AfterAll} checks the recorded order.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderAnnotationTest {

    private static final List<String> executed = new ArrayList<>();

    @BeforeAll
    static void reset() {
        executed.clear();
    }

    @Test
    @Order(Order.DEFAULT + 1)
    void afterTheDefault() {
        executed.add("afterTheDefault");
    }

    @Test
    void withoutOrder() {
        executed.add("withoutOrder");
    }

    @Test
    @Order(10)
    void ten() {
        executed.add("ten");
    }

    @Test
    @Order(-1)
    void minusOne() {
        executed.add("minusOne");
    }

    @AfterAll
    static void verifyOrder() {
        assertEquals(List.of("minusOne", "ten", "withoutOrder", "afterTheDefault"), executed);
    }
}
