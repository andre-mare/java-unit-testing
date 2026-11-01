package com.example.ratelimit;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

/**
 * A token-bucket rate limiter, Module 5 production code.
 * <p>
 * The bucket starts full with {@code capacity} tokens. Each call takes one
 * token; tokens come back at a fixed rate, never above capacity.
 * <ul>
 *   <li>{@link #tryAcquire()} never blocks: it returns {@code false} when empty.</li>
 *   <li>{@link #acquire()} and {@link #tryAcquire(Duration)} block until a token
 *       is available, which makes them good subjects for timeout tests (Module 5.4).</li>
 * </ul>
 * Time comes from a {@link LongSupplier} of nanoseconds. Production uses
 * {@link System#nanoTime()}; tests can pass a fake clock to control time exactly.
 * The blocking methods need a clock that really moves, so use them only with
 * the real clock.
 */
public final class TokenBucket {

    private final int capacity;
    private final long nanosPerToken;
    private final LongSupplier nanoClock;
    private long tokens;
    private long lastRefill;

    public TokenBucket(int capacity, int tokensPerSecond) {
        this(capacity, tokensPerSecond, System::nanoTime);
    }

    public TokenBucket(int capacity, int tokensPerSecond, LongSupplier nanoClock) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be at least 1: " + capacity);
        }
        if (tokensPerSecond < 1) {
            throw new IllegalArgumentException("tokensPerSecond must be at least 1: " + tokensPerSecond);
        }
        this.capacity = capacity;
        this.nanosPerToken = TimeUnit.SECONDS.toNanos(1) / tokensPerSecond;
        this.nanoClock = Objects.requireNonNull(nanoClock, "nanoClock");
        this.tokens = capacity;
        this.lastRefill = nanoClock.getAsLong();
    }

    public int capacity() {
        return capacity;
    }

    /** Takes a token if one is available; never blocks. */
    public synchronized boolean tryAcquire() {
        refill();
        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    /** Number of tokens that could be taken right now. */
    public synchronized int availableTokens() {
        refill();
        return (int) tokens;
    }

    /** Blocks until a token is available, then takes it. */
    public void acquire() throws InterruptedException {
        while (!tryAcquire()) {
            sleepNanos(nanosUntilNextToken());
        }
    }

    /**
     * Blocks up to {@code timeout} for a token.
     *
     * @return {@code true} if a token was taken, {@code false} if the timeout ran out first
     */
    public boolean tryAcquire(Duration timeout) throws InterruptedException {
        long deadline = nanoClock.getAsLong() + timeout.toNanos();
        while (!tryAcquire()) {
            long remaining = deadline - nanoClock.getAsLong();
            if (remaining <= 0) {
                return false;
            }
            sleepNanos(Math.min(remaining, nanosUntilNextToken()));
        }
        return true;
    }

    private synchronized long nanosUntilNextToken() {
        refill();
        return tokens > 0 ? 0 : nanosPerToken - (nanoClock.getAsLong() - lastRefill);
    }

    private void refill() {
        long now = nanoClock.getAsLong();
        long earned = (now - lastRefill) / nanosPerToken;
        if (earned > 0) {
            tokens = Math.min(capacity, tokens + earned);
            // A full bucket does not bank time; otherwise keep the unused fraction of a token.
            lastRefill = tokens == capacity ? now : lastRefill + earned * nanosPerToken;
        }
    }

    private static void sleepNanos(long nanos) throws InterruptedException {
        TimeUnit.NANOSECONDS.sleep(Math.max(nanos, TimeUnit.MILLISECONDS.toNanos(1)));
    }
}
