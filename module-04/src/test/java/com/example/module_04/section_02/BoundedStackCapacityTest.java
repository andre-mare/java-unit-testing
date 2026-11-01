package com.example.module_04.section_02;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.stack.BoundedStack;
import com.example.stack.LinkedBoundedStack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Module 4.2 – preview of Module 7: {@code @Nested} inside a {@code @ParameterizedClass}.
 * <p>
 * The whole class, nested class included, runs once per capacity:
 * <pre>
 * BoundedStackCapacityTest
 * ├─ [1] capacity = 1
 * │  ├─ startsEmpty()
 * │  └─ WhenFilledToCapacity
 * │     ├─ isFull()
 * │     └─ rejectsOneMore()
 * ├─ [2] capacity = 2  ...
 * └─ [3] capacity = 5  ...
 * </pre>
 * {@code @Parameter} injects the value into a field of the outer class, which the
 * nested class reads. Parameterized classes are still experimental in JUnit 6.1;
 * Module 7.8 covers them properly.
 */
@ParameterizedClass
@ValueSource(ints = { 1, 2, 5 })
class BoundedStackCapacityTest {

    @Parameter
    int capacity;

    @Test
    void startsEmpty() {
        assertTrue(new LinkedBoundedStack<String>(capacity).isEmpty());
    }

    @Nested
    class WhenFilledToCapacity {

        BoundedStack<Integer> stack;

        @BeforeEach
        void fill() {
            stack = new LinkedBoundedStack<>(capacity);
            for (int i = 0; i < capacity; i++) {
                stack.push(i);
            }
        }

        @Test
        void isFull() {
            assertTrue(stack.isFull());
            assertEquals(capacity, stack.size());
        }

        @Test
        void rejectsOneMore() {
            assertThrows(IllegalStateException.class, () -> stack.push(99));
        }
    }
}
