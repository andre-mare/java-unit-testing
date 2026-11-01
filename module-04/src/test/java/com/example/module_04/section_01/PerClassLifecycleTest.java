package com.example.module_04.section_01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import com.example.stack.BoundedStack;
import com.example.stack.LinkedBoundedStack;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 * Module 4.1 – {@code PER_CLASS}: one test instance shared by every test method.
 * <p>
 * Benefits:
 * <ul>
 *   <li>{@code @BeforeAll} and {@code @AfterAll} can be instance methods, so an
 *       expensive fixture can live in an ordinary field.</li>
 *   <li>The same applies to {@code @MethodSource} factory methods (Module 7) and
 *       to {@code default} methods in test interfaces (Module 4.3).</li>
 * </ul>
 * The cost: instance fields now carry state from one test to the next. Reset
 * that state in {@code @BeforeEach}, as {@code resetSharedStack()} does here. Remove
 * that method and see {@link PerClassStateLeakDemoTest} for what goes wrong.
 */
@TestInstance(Lifecycle.PER_CLASS)
class PerClassLifecycleTest {

    private static final Set<Object> instances = Collections.newSetFromMap(new IdentityHashMap<>());

    private BoundedStack<String> sharedStack;
    private int testsRunOnThisInstance;

    @BeforeAll
    void createSharedFixture() { // not static: allowed with PER_CLASS
        instances.clear();
        sharedStack = new LinkedBoundedStack<>(1_000);
    }

    @BeforeEach
    void resetSharedStack() {
        instances.add(this);
        sharedStack.clear();
        System.out.println("PER_CLASS instance " + System.identityHashCode(this));
    }

    @Test
    void pushOne() {
        testsRunOnThisInstance++;
        sharedStack.push("a");
        assertEquals(1, sharedStack.size());
    }

    @Test
    void pushTwo() {
        testsRunOnThisInstance++;
        sharedStack.push("a");
        sharedStack.push("b");
        assertEquals(2, sharedStack.size());
    }

    @Test
    void startsEmptyBecauseBeforeEachClears() {
        testsRunOnThisInstance++;
        assertTrue(sharedStack.isEmpty());
    }

    @AfterAll
    void oneInstanceForAllTests() { // not static either
        assertEquals(1, instances.size(), "three tests, one instance");
        assertEquals(3, testsRunOnThisInstance, "the counter field survived between tests");
    }
}
