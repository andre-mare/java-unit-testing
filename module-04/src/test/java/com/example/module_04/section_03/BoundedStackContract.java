package com.example.module_04.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import com.example.stack.BoundedStack;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Module 4.3 lab – the behaviour every {@link BoundedStack} implementation must have.
 * <p>
 * {@link ArrayBoundedStackTest} and {@link LinkedBoundedStackTest} implement this
 * interface and only say how to create their stack; all ten tests below run for
 * each of them. A new implementation gets the full suite with one method.
 * <p>
 * {@code @Tag("contract")} on the interface is inherited by every implementing
 * class: {@code mvn test -Dgroups=contract} runs exactly these suites.
 *
 * @param <S> the implementation under test
 */
@Tag("contract")
interface BoundedStackContract<S extends BoundedStack<String>> {

    int CAPACITY = 3;

    /** Creates an empty stack of the implementation under test. */
    S createStack(int capacity);

    private S fullStack() {
        S stack = createStack(CAPACITY);
        stack.push("a");
        stack.push("b");
        stack.push("c");
        return stack;
    }

    @Test
    default void newStackIsEmpty() {
        S stack = createStack(CAPACITY);
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
        assertEquals(CAPACITY, stack.capacity());
    }

    @Test
    default void popReturnsElementsInReverseOrder() {
        S stack = fullStack();
        assertEquals("c", stack.pop());
        assertEquals("b", stack.pop());
        assertEquals("a", stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    default void peekDoesNotRemoveTheElement() {
        S stack = createStack(CAPACITY);
        stack.push("a");
        assertEquals("a", stack.peek());
        assertEquals("a", stack.peek());
        assertEquals(1, stack.size());
    }

    @Test
    default void pushOnFullStackThrows() {
        S stack = fullStack();
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> stack.push("d"));
        assertEquals("Stack is full (capacity 3)", e.getMessage());
        assertTrue(stack.isFull());
    }

    @Test
    default void popOnEmptyStackThrows() {
        assertThrows(NoSuchElementException.class, () -> createStack(CAPACITY).pop());
    }

    @Test
    default void peekOnEmptyStackThrows() {
        assertThrows(NoSuchElementException.class, () -> createStack(CAPACITY).peek());
    }

    @Test
    default void rejectsNullElements() {
        S stack = createStack(CAPACITY);
        assertThrows(NullPointerException.class, () -> stack.push(null));
        assertTrue(stack.isEmpty());
    }

    @Test
    default void clearEmptiesTheStack() {
        S stack = fullStack();
        stack.clear();
        assertTrue(stack.isEmpty());
        stack.push("again");
        assertEquals("again", stack.peek());
    }

    @Test
    default void capacityMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> createStack(0));
    }

    @Test
    default void inheritsTheContractTag(TestInfo info) {
        assertTrue(info.getTags().contains("contract"), "@Tag on the interface applies to the class");
    }
}
