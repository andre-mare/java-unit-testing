package com.example.module_05.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Module 5.3 – {@code MethodOrderer.MethodName}: alphanumeric by method name
 * (then by parameter list). The methods are declared in the "wrong" order on purpose.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class MethodNameOrderTest {

    private static final List<String> executed = new ArrayList<>();

    @BeforeAll
    static void reset() {
        executed.clear();
    }

    @Test
    void step3Activate() {
        executed.add("step3Activate");
    }

    @Test
    void step1Register() {
        executed.add("step1Register");
    }

    @Test
    void step2Verify() {
        executed.add("step2Verify");
    }

    @AfterAll
    static void verifyOrder() {
        assertEquals(List.of("step1Register", "step2Verify", "step3Activate"), executed);
    }
}
