package com.example.module_04.section_03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Module 4.3 – mixing behaviour into a test class by implementing interfaces.
 * <p>
 * From {@link LifecycleLogging} it inherits four lifecycle methods and the
 * {@code PER_CLASS} lifecycle. From {@link Timed} it inherits a tag and an
 * extension. Each test verifies one of those. Expected console output:
 * <pre>
 * [lifecycle] before all
 * [lifecycle] before inheritsLifecycleMethods(TestInfo)
 * [timing] inheritsLifecycleMethods(TestInfo) took 85 µs
 * [lifecycle] after inheritsLifecycleMethods(TestInfo)
 * ...
 * [lifecycle] after all
 * </pre>
 * The class can also declare lifecycle methods of its own; they run alongside
 * the inherited ones (interface methods first for "before", last for "after").
 */
class InterfaceMixinTest implements LifecycleLogging, Timed {

    private static final Set<Object> instances = Collections.newSetFromMap(new IdentityHashMap<>());

    @BeforeEach
    void recordInstance() {
        instances.add(this);
    }

    @Test
    void inheritsLifecycleMethods(TestInfo info) {
        List<String> events = List.copyOf(EVENTS);
        assertEquals("before all", events.get(0));
        assertEquals("before " + info.getDisplayName(), events.get(events.size() - 1),
            "the inherited @BeforeEach ran just before this test");
    }

    @Test
    void inheritsTagFromInterface(TestInfo info) {
        assertTrue(info.getTags().contains("timed"));
    }

    @Test
    void inheritsExtensionFromInterface() {
        assertTrue(ExecutionTimeExtension.isTimingCurrentTest(), "the inherited extension is active");
    }

    @AfterAll
    void inheritsPerClassLifecycle() { // non-static: only legal because PER_CLASS was inherited
        assertEquals(1, instances.size(), "three tests, one shared instance");
        instances.clear();
    }
}
