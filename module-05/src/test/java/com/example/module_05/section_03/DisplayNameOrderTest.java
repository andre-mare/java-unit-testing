package com.example.module_05.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Module 5.3 – {@code MethodOrderer.DisplayName}: alphanumeric by display name,
 * ignoring the method names entirely.
 * <p>
 * The sort is on text, not numbers: "10 ..." would sort before "2 ...". Pad
 * numbers ("02", "10") if you rely on numbered display names.
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
class DisplayNameOrderTest {

    private static final List<String> executed = new ArrayList<>();

    @BeforeAll
    static void reset() {
        executed.clear();
    }

    @Test
    @DisplayName("C – activate")
    void a() {
        executed.add("C");
    }

    @Test
    @DisplayName("A – register")
    void b() {
        executed.add("A");
    }

    @Test
    @DisplayName("B – verify")
    void c() {
        executed.add("B");
    }

    @AfterAll
    static void verifyOrder() {
        assertEquals(List.of("A", "B", "C"), executed);
    }
}
