package com.example.module_06.support;

import java.util.concurrent.atomic.AtomicInteger;

import com.example.auth.TokenGenerator;

/** A predictable fake {@link TokenGenerator}: "token-1", "token-2", ... */
public final class SequentialTokenGenerator implements TokenGenerator {

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public String newTokenValue() {
        return "token-" + counter.incrementAndGet();
    }
}
