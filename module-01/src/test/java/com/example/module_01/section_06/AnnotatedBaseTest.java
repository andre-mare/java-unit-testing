package com.example.module_01.section_06;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import com.example.calculator.Calculator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Module 1.6 – annotations overview: which class-level annotations are inherited.
 * <p>
 * This base class carries four annotations. {@link AnnotationInheritanceTest}
 * extends it with no annotations of its own, and its tests prove what carried over:
 * <table>
 *   <caption>Inheritance of class-level annotations</caption>
 *   <tr><th>Annotation</th><th>Inherited?</th></tr>
 *   <tr><td>{@code @Tag}</td><td>yes (at class level)</td></tr>
 *   <tr><td>{@code @TestMethodOrder}</td><td>yes</td></tr>
 *   <tr><td>{@code @DisplayName}</td><td>no</td></tr>
 *   <tr><td>{@code @Disabled}</td><td>no</td></tr>
 * </table>
 * Other inherited annotations, per the guide: {@code @TestInstance},
 * {@code @TestClassOrder}, {@code @DisplayNameGeneration}, {@code @Timeout},
 * {@code @ExtendWith}, {@code @ParameterizedClass}, {@code @ClassTemplate}.
 * Not inherited: {@code @Nested}, {@code @Tag} on methods.
 * <p>
 * The class is {@code abstract}, so JUnit does not run it directly; only the
 * subclass runs. Note {@code @Disabled} here would skip this class, yet the
 * subclass still runs.
 */
@Tag("inherited-tag")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Display name on the base class")
@Disabled("Not inherited: subclasses still run")
abstract class AnnotatedBaseTest {

    /** Records the order in which test methods ran, per subclass run. */
    protected static final List<String> executionOrder = new ArrayList<>();

    protected final Calculator calculator = new Calculator();

    @Test
    @Order(1)
    void runsFirst() {
        executionOrder.add("runsFirst");
        assertEquals(2, calculator.add(1, 1));
    }

    @Test
    @Order(2)
    void runsSecond() {
        executionOrder.add("runsSecond");
        assertEquals(List.of("runsFirst", "runsSecond"), executionOrder,
            "@TestMethodOrder was inherited, so @Order is honoured");
    }
}
