package com.example.module_04.section_01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import com.example.stack.ArrayBoundedStack;
import com.example.stack.BoundedStack;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 * Module 4.1 – {@code PER_METHOD}, the default: a new test instance for every test method.
 * <p>
 * Each test gets fresh instance fields, so no state leaks between tests, and
 * the tests may run in any order. The price: {@code @BeforeAll} and
 * {@code @AfterAll} must be {@code static}, because they run before the first
 * instance exists and after the last one is gone.
 * <p>
 * {@code @TestInstance(PER_METHOD)} is normally implicit. It is written out here so
 * this class keeps its behaviour even when you change the global default in
 * {@link DefaultLifecycleTest}'s exercise.
 * <p>
 * Lab: run it and compare the printed instance ids with {@link PerClassLifecycleTest}.
 */
@TestInstance(Lifecycle.PER_METHOD)
class PerMethodLifecycleTest {

    /** Identity set: counts distinct objects, not equal ones, and keeps them reachable. */
    private static final Set<Object> instances = Collections.newSetFromMap(new IdentityHashMap<>());

    private final BoundedStack<String> stack = new ArrayBoundedStack<>(3);

    @BeforeAll
    static void beforeAll() {
        instances.clear();
    }

    @BeforeEach
    void recordInstance() {
        instances.add(this);
        System.out.println("PER_METHOD instance " + System.identityHashCode(this));
    }

    @Test
    void pushOne() {
        stack.push("a");
        assertEquals(1, stack.size());
    }

    @Test
    void pushTwo() {
        stack.push("a");
        stack.push("b");
        assertEquals(2, stack.size());
    }

    @Test
    void startsEmpty() {
        assertTrue(stack.isEmpty(), "pushes from other tests never leak into this one");
    }

    @AfterAll
    static void oneInstancePerTest() {
        assertEquals(3, instances.size(), "three tests, three instances");
    }
}
