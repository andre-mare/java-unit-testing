package com.example.module_04.section_03;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Module 4.3 – a marker interface that carries a tag and an extension.
 * <p>
 * It has no methods at all. A class that implements it inherits
 * {@code @Tag("timed")} and {@code @ExtendWith(ExecutionTimeExtension.class)},
 * so {@code implements Timed} replaces two annotations.
 */
@Tag("timed")
@ExtendWith(ExecutionTimeExtension.class)
interface Timed {
}
