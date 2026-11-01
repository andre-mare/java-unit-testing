package com.example.audit;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Sends audit events to a destination (a SIEM, say) on a background thread,
 * Module 9 production code.
 * <p>
 * It holds a thread, so it must be stopped, but it is deliberately not
 * {@link AutoCloseable}: the method is called {@link #shutdown()}. Shutting down
 * stops new submissions and waits up to five seconds for queued events.
 */
public final class AuditShipper {

    private final Consumer<AuditEvent> destination;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "audit-shipper");
        thread.setDaemon(true);
        return thread;
    });

    public AuditShipper(Consumer<AuditEvent> destination) {
        this.destination = Objects.requireNonNull(destination, "destination");
    }

    /** Queues an event; it is delivered later, in order. */
    public void submit(AuditEvent event) {
        if (executor.isShutdown()) {
            throw new IllegalStateException("Shipper is shut down");
        }
        executor.execute(() -> destination.accept(event));
    }

    /** Delivers what is queued, then stops. Calling it twice is fine. */
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public boolean isShutdown() {
        return executor.isShutdown();
    }
}
