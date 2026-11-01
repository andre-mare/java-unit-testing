package com.example.stack;

/**
 * A last-in, first-out stack with a fixed maximum size.
 * <p>
 * Module 4 production code. The interface has two implementations, so one set
 * of contract tests (Module 4.3) can verify both. Its states (empty, partly
 * filled, full) suit nested tests (Module 4.2).
 *
 * @param <T> element type; {@code null} elements are not allowed
 */
public interface BoundedStack<T> {

    /**
     * Puts an element on top.
     *
     * @throws IllegalStateException when the stack is full
     * @throws NullPointerException  when the element is null
     */
    void push(T element);

    /**
     * Removes and returns the top element.
     *
     * @throws java.util.NoSuchElementException when the stack is empty
     */
    T pop();

    /**
     * Returns the top element without removing it.
     *
     * @throws java.util.NoSuchElementException when the stack is empty
     */
    T peek();

    /** Number of elements currently on the stack. */
    int size();

    /** Maximum number of elements, fixed at construction. */
    int capacity();

    /** Removes all elements. */
    void clear();

    default boolean isEmpty() {
        return size() == 0;
    }

    default boolean isFull() {
        return size() == capacity();
    }
}
