package com.example.module_04.section_02;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import com.example.stack.ArrayBoundedStack;
import com.example.stack.BoundedStack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Module 4.2 lab – nested tests, one level per state of the stack.
 * <pre>
 * A bounded stack with capacity 3
 * ├─ cannot be created with capacity 0
 * └─ when new
 *    ├─ is empty / is not full / throws on pop / throws on peek
 *    └─ after pushing an element
 *       ├─ is no longer empty / peek returns it / pop returns it
 *       └─ when filled to capacity
 *          └─ is full / rejects another push / pops in reverse order / pop makes room
 * </pre>
 * Each level's {@code @BeforeEach} builds on the level above it: for a test in
 * "when filled to capacity", JUnit runs {@code createStack()}, then
 * {@code pushAnElement()}, then {@code fillUp()}. That is why any inner test can
 * also run on its own: click it in the IDE and the setup above it still runs.
 * <p>
 * {@code @Nested} only works on inner (non-static) classes, so inner tests can
 * read the outer class's fields, such as {@code stack}.
 */
@DisplayName("A bounded stack with capacity 3")
class BoundedStackNestedTest {

    private static final int CAPACITY = 3;

    @Test
    @DisplayName("cannot be created with capacity 0")
    void rejectsZeroCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new ArrayBoundedStack<String>(0));
    }

    @Nested
    @DisplayName("when new")
    class WhenNew {

        BoundedStack<String> stack;

        @BeforeEach
        void createStack() {
            stack = new ArrayBoundedStack<>(CAPACITY);
        }

        @Test
        @DisplayName("is empty")
        void isEmpty() {
            assertTrue(stack.isEmpty());
            assertEquals(0, stack.size());
        }

        @Test
        @DisplayName("is not full")
        void isNotFull() {
            assertFalse(stack.isFull());
        }

        @Test
        @DisplayName("throws NoSuchElementException when popped")
        void throwsWhenPopped() {
            assertThrows(NoSuchElementException.class, stack::pop);
        }

        @Test
        @DisplayName("throws NoSuchElementException when peeked")
        void throwsWhenPeeked() {
            assertThrows(NoSuchElementException.class, stack::peek);
        }

        @Nested
        @DisplayName("after pushing an element")
        class AfterPushing {

            final String anElement = "first";

            @BeforeEach
            void pushAnElement() {
                stack.push(anElement); // uses the field of the enclosing WhenNew instance
            }

            @Test
            @DisplayName("is no longer empty")
            void isNotEmpty() {
                assertFalse(stack.isEmpty());
            }

            @Test
            @DisplayName("returns the element when peeked, and keeps it")
            void peekKeepsElement() {
                assertEquals(anElement, stack.peek());
                assertEquals(1, stack.size());
            }

            @Test
            @DisplayName("returns the element when popped, and is empty again")
            void popRemovesElement() {
                assertEquals(anElement, stack.pop());
                assertTrue(stack.isEmpty());
            }

            @Nested
            @DisplayName("when filled to capacity")
            class WhenFull {

                @BeforeEach
                void fillUp() {
                    stack.push("second");
                    stack.push("third");
                }

                @Test
                @DisplayName("is full")
                void isFull() {
                    assertTrue(stack.isFull());
                    assertEquals(CAPACITY, stack.size());
                }

                @Test
                @DisplayName("rejects another push")
                void rejectsPush() {
                    IllegalStateException e = assertThrows(IllegalStateException.class, () -> stack.push("fourth"));
                    assertEquals("Stack is full (capacity 3)", e.getMessage());
                    assertEquals(CAPACITY, stack.size(), "the failed push changed nothing");
                }

                @Test
                @DisplayName("pops elements in reverse order of pushing")
                void popsInReverseOrder() {
                    assertEquals("third", stack.pop());
                    assertEquals("second", stack.pop());
                    assertEquals(anElement, stack.pop()); // field of the enclosing AfterPushing instance
                }

                @Test
                @DisplayName("accepts a push again after a pop")
                void popMakesRoom() {
                    stack.pop();
                    stack.push("fourth");
                    assertEquals("fourth", stack.peek());
                }
            }
        }
    }
}
