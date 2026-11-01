package com.example.stack;

import java.util.NoSuchElementException;
import java.util.Objects;

/** {@link BoundedStack} backed by a singly linked list of nodes. */
public final class LinkedBoundedStack<T> implements BoundedStack<T> {

    private record Node<T>(T value, Node<T> next) {
    }

    private final int capacity;
    private Node<T> head;
    private int size;

    public LinkedBoundedStack(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be at least 1: " + capacity);
        }
        this.capacity = capacity;
    }

    @Override
    public void push(T element) {
        Objects.requireNonNull(element, "element");
        if (isFull()) {
            throw new IllegalStateException("Stack is full (capacity " + capacity + ")");
        }
        head = new Node<>(element, head);
        size++;
    }

    @Override
    public T pop() {
        T top = peek();
        head = head.next();
        size--;
        return top;
    }

    @Override
    public T peek() {
        if (head == null) {
            throw new NoSuchElementException("Stack is empty");
        }
        return head.value();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public void clear() {
        head = null;
        size = 0;
    }
}
