package com.example.module_04.section_01;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import com.example.stack.ArrayBoundedStack;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Module 4.1 lab – changing the default lifecycle for a whole run.
 * <p>
 * This class has NO {@code @TestInstance}, so it follows the project default.
 * It prints how many instances its three tests used; {@code @AfterAll} is
 * static, which works under both modes.
 * <p>
 * <b>Run 1</b> – {@code mvn test -Dtest=DefaultLifecycleTest} prints
 * {@code 3 instance(s) for 3 tests}.
 * <p>
 * <b>Run 2</b> – {@code mvn test -Dtest=DefaultLifecycleTest -Djunit.jupiter.testinstance.lifecycle.default=per_class}
 * prints {@code 1 instance(s) for 3 tests}.
 * <p>
 * For a permanent change, the JUnit guide recommends {@code junit-platform.properties}
 * over a JVM flag, so the IDE and the build agree. Do not set it permanently in
 * this course project: lifecycle demos without an explicit {@code @TestInstance}
 * (such as Module 1's {@code LifecycleDemoTest}) expect per-method instances.
 */
class DefaultLifecycleTest {

    private static final Set<Object> instances = Collections.newSetFromMap(new IdentityHashMap<>());

    @BeforeEach
    void recordInstance() {
        instances.add(this);
    }

    @Test
    void first() {
        assertTrue(new ArrayBoundedStack<String>(1).isEmpty());
    }

    @Test
    void second() {
        assertTrue(new ArrayBoundedStack<String>(2).isEmpty());
    }

    @Test
    void third() {
        assertTrue(new ArrayBoundedStack<String>(3).isEmpty());
    }

    @AfterAll
    static void report() {
        System.out.println(instances.size() + " instance(s) for 3 tests");
        instances.clear();
    }
}
