package com.example.module_04.section_01;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.stack.ArrayBoundedStack;
import com.example.stack.BoundedStack;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 * Module 4.1 – FAILS on purpose: shared state leaking between tests under {@code PER_CLASS}.
 * <p>
 * Both tests push one element and expect a size of 1. Whichever runs second
 * sees the element the first one left behind, and fails with
 * {@code expected: <1> but was: <2>}. Each test passes when run on its own,
 * which is exactly what makes this kind of bug hard to find.
 * <p>
 * Fix: clear the stack in {@code @BeforeEach}, or drop {@code PER_CLASS}.
 * <p>
 * Tagged {@code expected-failure-tests}, so the normal build skips it. Run it with:
 * <pre>mvn test -Dtest=PerClassStateLeakDemoTest -DexcludedGroups=none</pre>
 */
@Tag("expected-failure-tests")
@TestInstance(Lifecycle.PER_CLASS)
class PerClassStateLeakDemoTest {

    private BoundedStack<String> sharedStack;

    @BeforeAll
    void createOnce() {
        sharedStack = new ArrayBoundedStack<>(10);
    }

    @Test
    void firstPush() {
        sharedStack.push("first");
        assertEquals(1, sharedStack.size());
    }

    @Test
    void secondPush() {
        sharedStack.push("second");
        assertEquals(1, sharedStack.size());
    }
}
