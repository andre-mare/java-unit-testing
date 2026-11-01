package com.example.module_01.section_06;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Module 1.6 lab – a subclass with NO annotations of its own.
 * <p>
 * Expected result tree:
 * <pre>
 * AnnotationInheritanceTest            &lt;- class name, not the base @DisplayName
 * ├─ runsFirst()                       &lt;- runs: base @Disabled was not inherited
 * ├─ runsSecond()                      &lt;- @Order honoured: @TestMethodOrder inherited
 * ├─ classTagIsInherited(TestInfo)
 * └─ displayNameIsNotInherited()
 * </pre>
 * Exercises:
 * <ol>
 *   <li>Add {@code @Disabled} to THIS class and run it: all four tests are skipped.</li>
 *   <li>Remove it, add {@code @DisplayName("Mine")}, and watch
 *       {@code displayNameIsNotInherited} fail.</li>
 * </ol>
 */
class AnnotationInheritanceTest extends AnnotatedBaseTest {

    private static String classDisplayName;

    @BeforeAll
    static void captureClassInfo(TestInfo classInfo) {
        // In @BeforeAll, TestInfo describes the test class container itself.
        classDisplayName = classInfo.getDisplayName();
        executionOrder.clear();
    }

    @Test
    @Order(3)
    void classTagIsInherited(TestInfo info) {
        assertTrue(info.getTags().contains("inherited-tag"),
            "class-level @Tag on the superclass applies here");
    }

    @Test
    @Order(4)
    void displayNameIsNotInherited() {
        assertEquals("AnnotationInheritanceTest", classDisplayName,
            "the base class @DisplayName does not carry over to the subclass");
    }
}
