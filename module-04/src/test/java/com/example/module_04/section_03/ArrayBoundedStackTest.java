package com.example.module_04.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.stack.ArrayBoundedStack;

import org.junit.jupiter.api.Test;

/**
 * Module 4.3 lab – runs the full {@link BoundedStackContract} against {@link ArrayBoundedStack}.
 * <p>
 * Expect 11 tests: 10 inherited from the contract, plus one of its own.
 * Implementation-specific tests live next to the contract, in the class itself.
 */
class ArrayBoundedStackTest implements BoundedStackContract<ArrayBoundedStack<String>> {

    @Override
    public ArrayBoundedStack<String> createStack(int capacity) {
        return new ArrayBoundedStack<>(capacity);
    }

    @Test
    void reusesSlotsAfterPop() {
        ArrayBoundedStack<String> stack = createStack(1);
        for (int i = 0; i < 100; i++) {
            stack.push("x" + i);
            assertEquals("x" + i, stack.pop());
        }
    }
}
