package com.example.stack;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;

/** {@link BoundedStack} backed by a fixed-size array. */
public final class ArrayBoundedStack<T> implements BoundedStack<T> {

    private final Object[] elements;
    private int size;

    public ArrayBoundedStack(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be at least 1: " + capacity);
        }
        this.elements = new Object[capacity];
    }

    @Override
    public void push(T element) {
        Objects.requireNonNull(element, "element");
        if (isFull()) {
            throw new IllegalStateException("Stack is full (capacity " + capacity() + ")");
        }
        elements[size++] = element;
    }

    @Override
    public T pop() {
        T top = peek();
        elements[--size] = null; // let the element be garbage collected
        return top;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return (T) elements[size - 1];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int capacity() {
        return elements.length;
    }

    @Override
    public void clear() {
        Arrays.fill(elements, 0, size, null);
        size = 0;
    }
}
