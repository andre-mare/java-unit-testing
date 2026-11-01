package com.example.module_05.support;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

/**
 * Module 5.4 helper – a clock that only moves when the test says so.
 * <p>
 * Pass it to {@code TokenBucket} to test refill logic in microseconds of real
 * time, with exact results. Prefer this over real sleeping plus a timeout
 * whenever the code lets you inject time.
 */
public final class FakeNanoClock implements LongSupplier {

    private final AtomicLong nanos = new AtomicLong();

    @Override
    public long getAsLong() {
        return nanos.get();
    }

    public void advance(Duration duration) {
        nanos.addAndGet(duration.toNanos());
    }
}
