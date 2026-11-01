package com.example.module_04.section_03;

import com.example.stack.LinkedBoundedStack;

/**
 * Module 4.3 lab – runs the full {@link BoundedStackContract} against {@link LinkedBoundedStack}.
 * <p>
 * No tests of its own: one factory method buys all 10 contract tests.
 * Exercise: break {@code LinkedBoundedStack.clear()} (forget to reset {@code size})
 * and watch exactly this class fail while {@link ArrayBoundedStackTest} stays green.
 */
class LinkedBoundedStackTest implements BoundedStackContract<LinkedBoundedStack<String>> {

    @Override
    public LinkedBoundedStack<String> createStack(int capacity) {
        return new LinkedBoundedStack<>(capacity);
    }
}
