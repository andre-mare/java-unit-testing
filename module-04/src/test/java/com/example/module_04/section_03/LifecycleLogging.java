package com.example.module_04.section_03;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 * Module 4.3 – lifecycle methods as interface {@code default} methods.
 * <p>
 * {@code @BeforeEach} and {@code @AfterEach} work on {@code default} methods directly.
 * {@code @BeforeAll} and {@code @AfterAll} must be {@code static} unless the class
 * uses {@code PER_CLASS}, so this interface declares {@code @TestInstance(PER_CLASS)},
 * and every implementing class inherits that lifecycle along with the methods.
 * <p>
 * Each event is printed and recorded in {@link #EVENTS} so that tests can check it.
 */
@TestInstance(Lifecycle.PER_CLASS)
interface LifecycleLogging {

    /** Events of the class that is currently running; reset in {@code @BeforeAll}. */
    List<String> EVENTS = Collections.synchronizedList(new ArrayList<>());

    @BeforeAll
    default void logBeforeAll() {
        EVENTS.clear();
        log("before all");
    }

    @BeforeEach
    default void logBeforeEach(TestInfo testInfo) {
        log("before " + testInfo.getDisplayName());
    }

    @AfterEach
    default void logAfterEach(TestInfo testInfo) {
        log("after " + testInfo.getDisplayName());
    }

    @AfterAll
    default void logAfterAll() {
        log("after all");
    }

    private static void log(String event) {
        EVENTS.add(event);
        System.out.println("[lifecycle] " + event);
    }
}
