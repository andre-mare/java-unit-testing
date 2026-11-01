package com.example.module_09.section_02;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.audit.AuditEvent;
import com.example.audit.AuditShipper;
import com.example.module_09.support.SampleAuditEvents;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * Module 9.2 lab – auto-closing something that is not {@code AutoCloseable}.
 * <p>
 * {@link AuditShipper} stops with {@code shutdown()}, so the annotation names that
 * method: {@code @AutoClose("shutdown")}. Each test gets its own shipper; after
 * all tests, every shipper must be shut down and every event delivered (shutting
 * down waits for the queue).
 */
@Execution(ExecutionMode.SAME_THREAD)
class AutoCloseCustomMethodTest {

    private static final List<AuditShipper> shippers = new CopyOnWriteArrayList<>();
    private static final List<AuditEvent> delivered = new CopyOnWriteArrayList<>();

    @AutoClose("shutdown")
    final AuditShipper shipper = new AuditShipper(delivered::add);

    @Test
    void shipsThreeEvents() {
        shippers.add(shipper);
        SampleAuditEvents.ALL.forEach(shipper::submit);
    }

    @Test
    void shipsOneEvent() {
        shippers.add(shipper);
        shipper.submit(SampleAuditEvents.DENIED);
    }

    @Test
    void aShutDownShipperRefusesEvents() {
        AuditShipper local = new AuditShipper(event -> { });
        local.shutdown();
        assertThrows(IllegalStateException.class, () -> local.submit(SampleAuditEvents.LOGIN));
    }

    @AfterAll
    static void allShutDownAndDelivered() {
        assertEquals(2, shippers.size());
        assertTrue(shippers.stream().allMatch(AuditShipper::isShutdown));
        assertEquals(4, delivered.size(), "shutdown() delivers what was queued");
    }
}
