package com.example.module_03.section_02;

import org.junit.jupiter.api.Disabled;

/**
 * Module 3.2 – to keep a subclass disabled, declare {@code @Disabled} again.
 * <p>
 * Has no tests of its own; it would run the inherited one, but is skipped.
 */
@Disabled("Redeclared: subclasses of a disabled class must repeat @Disabled")
class StillDisabledSubclassTest extends DisabledBaseTest {
}
