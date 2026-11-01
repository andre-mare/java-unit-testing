# Module 1: Foundations

This module covers the basics of JUnit: how to write a test, what can happen when it runs, how JUnit creates and calls test classes, how results are organized, and how annotations can be inherited and combined.

## Table of Contents

- [Module 1: Foundations](#module-1-foundations)
  - [Table of Contents](#table-of-contents)
  - [The Domain: a Calculator](#the-domain-a-calculator)
  - [Running the Module](#running-the-module)
  - [Sections](#sections)
    - [Section 1: Your First Test](#section-1-your-first-test)
    - [Section 2: Test Outcomes](#section-2-test-outcomes)
    - [Section 3: Test Class Rules and Inheritance](#section-3-test-class-rules-and-inheritance)
    - [Section 4: The Test Lifecycle](#section-4-the-test-lifecycle)
    - [Section 5: The Test Tree](#section-5-the-test-tree)
    - [Section 6: Annotation Inheritance](#section-6-annotation-inheritance)
    - [Section 7: Composed Annotations](#section-7-composed-annotations)
  - [Deliberately Failing Tests](#deliberately-failing-tests)
    - [Why the failures are kept](#why-the-failures-are-kept)
    - [How they are excluded](#how-they-are-excluded)
    - [How to override the exclusion](#how-to-override-the-exclusion)
    - [What to expect](#what-to-expect)

## The Domain: a Calculator

The class under test is [`Calculator`](src/main/java/com/example/calculator/Calculator.java), a small class with no dependencies. Its behaviour is easy to predict, so you can focus on JUnit rather than on the code being tested.

| Method | Behaviour | Edge case |
| --- | --- | --- |
| `add`, `subtract`, `multiply` | Exact `int` arithmetic | Throws `ArithmeticException` on overflow |
| `divide` | Integer division, which drops the remainder | Throws `ArithmeticException` when dividing by zero |
| `sqrt` | Square root | Throws `IllegalArgumentException` for negative input |
| `factorial` | `n!` for 0 to 20 | Throws `IllegalArgumentException` below 0 and `ArithmeticException` above 20 |
| `isPrime` | Prime check | Numbers below 2 are not prime |

This module mostly tests normal behaviour. The edge cases are already in the class so that Modules 2, 7 and 8 can test them without changing it.

## Running the Module

Run these commands from the repository root.

| Goal | Command | Result |
| --- | --- | --- |
| Run the module | `mvn test -pl module-01` | 30 tests pass; `TestOutcomesTest` is excluded |
| Run one class | `mvn test -pl module-01 -Dtest=LifecycleDemoTest` | 2 tests pass |
| Run the `fast` tag | `mvn test -pl module-01 -Dgroups=fast` | 2 tests pass |
| Run the `unit` tag | `mvn test -pl module-01 -Dgroups=unit` | 1 test passes |
| Run the failure demo | `mvn test -pl module-01 -Dtest=TestOutcomesTest -DexcludedGroups=none` | `BUILD FAILURE`, as intended |

## Sections

Each section has its own package under [`src/test/java/com/example/module_01`](src/test/java/com/example/module_01).

### Section 1: Your First Test

**What it tests:** the basic arithmetic methods of `Calculator`.

| Class | What it tests |
| --- | --- |
| [`CalculatorFirstTest`](src/test/java/com/example/module_01/section_01/CalculatorFirstTest.java) | Addition, subtraction that gives a negative result, multiplication by zero, and integer division that drops the remainder |

**JUnit concepts**

- **`@Test`** marks a method as a test.
- **Test class and method rules:** a test class doesn't need to be `public`. A test method must not be `private` or `static`, and must return `void`.
- **Arrange, Act, Assert** is a common structure for a test: set up the inputs, call the code, then check the result.
- **`assertEquals(expected, actual)`** fails the test if the two values differ. The expected value comes first, and an optional last argument adds a message to the failure.

### Section 2: Test Outcomes

**What it tests:** what JUnit reports, rather than `Calculator` itself. The class shows every possible outcome side by side, and it [fails on purpose](#deliberately-failing-tests).

| Class | What it tests |
| --- | --- |
| [`TestOutcomesTest`](src/test/java/com/example/module_01/section_02/TestOutcomesTest.java) | One passing test, three failing tests, one disabled test and one aborted test |

**JUnit concepts**

- **Succeeded:** the test finished without throwing an exception.
- **Failed:** an assertion failed or the test threw an unexpected exception. No assertion is needed: an uncaught exception is enough to fail a test.
- **Skipped:** the test never started. **`@Disabled`** does this, and should always include a reason.
- **Aborted:** the test started but stopped early because an assumption wasn't met. **`assumeTrue`** does this, and the rest of the test doesn't run.
- **`fail()`** always fails the test, whatever the state of the code.
- **`@Tag`** labels a class or method so that a build can include or exclude it.

### Section 3: Test Class Rules and Inheritance

**What it tests:** how a test class can be declared, and which members it inherits, using simple `Calculator` calls.

| Class | What it tests |
| --- | --- |
| [`AbstractCalculatorTest`](src/test/java/com/example/module_01/section_03/AbstractCalculatorTest.java) | Nothing directly, because it is `abstract`. It provides a setup method and two tests for its subclass |
| [`InheritedCalculatorTest`](src/test/java/com/example/module_01/section_03/InheritedCalculatorTest.java) | Runs three tests: one inherited, one overridden and one of its own. Shows that the inherited `@BeforeEach` still runs |
| [`CalculatorRecordTest`](src/test/java/com/example/module_01/section_03/CalculatorRecordTest.java) | Addition and factorial, using a Java `record` as the test class |

**JUnit concepts**

- **Abstract test classes** are never run directly. Their tests and lifecycle methods run as part of each concrete subclass.
- **Inheritance:** subclasses inherit `@Test` and `@BeforeEach` methods.
- **Overriding a test:** when a subclass overrides a test method, only the subclass version runs. The override needs its own `@Test`.
- **Single constructor rule:** a test class must have exactly one constructor. A `record` has one canonical constructor, so it qualifies.

### Section 4: The Test Lifecycle

**What it tests:** the order in which JUnit calls lifecycle methods, and that each test gets its own instance of the class.

| Class | What it tests |
| --- | --- |
| [`LifecycleDemoTest`](src/test/java/com/example/module_01/section_04/LifecycleDemoTest.java) | Prints each lifecycle step to the console, and asserts that an instance field is never shared between tests |

The order for two tests is:

```text
@BeforeAll                                     once, static
  constructor -> @BeforeEach -> test -> @AfterEach
  constructor -> @BeforeEach -> test -> @AfterEach
@AfterAll                                      once, static
```

**JUnit concepts**

- **`@BeforeAll` and `@AfterAll`** run once for the whole class, and are `static` by default. They suit expensive shared resources.
- **`@BeforeEach` and `@AfterEach`** run around every test. They suit per-test setup and cleanup.
- **Per-method lifecycle:** by default JUnit creates a new instance of the class for each test, so instance fields start fresh. Static fields are shared. Module 4 covers this in depth.

### Section 5: The Test Tree

**What it tests:** `isPrime`, written four different ways, to show how each kind of test method appears in the results.

| Class | What it tests |
| --- | --- |
| [`TestTreeTest`](src/test/java/com/example/module_01/section_05/TestTreeTest.java) | Prime numbers, using a plain test, a repeated test, a parameterized test and a test factory. Together they produce 12 tests |

Run this class in your IDE and compare the result tree with the concepts below.

**JUnit concepts**

- **Container or test:** a container groups other nodes, such as a test class. A test is a node that actually runs and produces a result.
- **`@Test`** is the only annotation here that produces a test directly. The other three produce a container of tests.
- **`@RepeatedTest(3)`** runs the same test three times.
- **`@ParameterizedTest` with `@ValueSource`** runs the test once per value, passing each value as an argument.
- **`@TestFactory` and `DynamicTest`** generate tests in code while the suite runs.

Modules 7 and 8 cover these annotations in depth.

### Section 6: Annotation Inheritance

**What it tests:** which class-level annotations a subclass receives from its superclass.

| Class | What it tests |
| --- | --- |
| [`AnnotatedBaseTest`](src/test/java/com/example/module_01/section_06/AnnotatedBaseTest.java) | Nothing directly, because it is `abstract`. It carries four class-level annotations and two ordered tests |
| [`AnnotationInheritanceTest`](src/test/java/com/example/module_01/section_06/AnnotationInheritanceTest.java) | Has no class-level annotations of its own. Its four tests show what did and didn't carry over from the base class |

| Annotation on the base class | Inherited? | How the subclass shows it |
| --- | --- | --- |
| `@Tag` | Yes | `classTagIsInherited` finds `inherited-tag` on the test |
| `@TestMethodOrder` | Yes | `runsSecond` confirms that `@Order` was followed |
| `@DisplayName` | No | `displayNameIsNotInherited` finds the class name instead |
| `@Disabled` | No | The subclass tests run |

**JUnit concepts**

- **`@TestMethodOrder` and `@Order`** set the order in which test methods run.
- **`@DisplayName`** sets the name shown in test results.
- **`TestInfo`** is an object JUnit can pass to a test or lifecycle method, describing what is currently running, including its tags and display name. Module 6 covers this kind of injection.

**Try it:** in `AnnotationInheritanceTest`, add `@Disabled` and run the class to see all four tests skipped. Then replace it with `@DisplayName("Mine")` to see `displayNameIsNotInherited` fail.

### Section 7: Composed Annotations

**What it tests:** that a custom annotation applies the JUnit annotations it is built from. Each test reads its own tags to prove it.

| Class | What it tests |
| --- | --- |
| [`ComposedAnnotationsTest`](src/test/java/com/example/module_01/section_07/ComposedAnnotationsTest.java) | Uses each of the three custom annotations below on one test, and asserts that the expected tag is present |

The custom annotations it uses aren't test classes:

| Annotation | Combines | Used as |
| --- | --- | --- |
| [`@Fast`](src/test/java/com/example/module_01/section_07/annotation/Fast.java) | `@Tag("fast")` | A shorter tag, alongside `@Test`, on a class or method |
| [`@FastTest`](src/test/java/com/example/module_01/section_07/annotation/FastTest.java) | `@Test` and `@Tag("fast")` | A replacement for `@Test` on a method |
| [`@UnitTest`](src/test/java/com/example/module_01/section_07/annotation/UnitTest.java) | `@Test` and `@Tag("unit")` | A replacement for `@Test` on a method |

**JUnit concepts**

- **Meta-annotation:** a JUnit annotation placed on your own annotation, such as `@Tag` on `@Fast`.
- **Composed annotation:** your own annotation that carries one or more meta-annotations. JUnit treats it as if those annotations were on the test directly, so a tag name or a common combination is defined in one place.
- **Runtime retention:** a custom annotation needs `@Retention(RetentionPolicy.RUNTIME)`, or JUnit can't see it while the tests run.
- **Filtering by tag:** `-Dgroups=fast` runs only the tests with that tag, including those tagged through a composed annotation.

## Deliberately Failing Tests

In this module, [`TestOutcomesTest`](src/test/java/com/example/module_01/section_02/TestOutcomesTest.java) is the only class that fails on purpose. Other modules have similar classes, listed in the [root README](../README.md#deliberately-failing-tests).

### Why the failures are kept

The point of these classes is to show you real failure output: the messages, the stack traces and the icons in your IDE. A class can't show a failure without failing, so its tests can't simply be corrected.

Left in the normal build, though, they would make every `mvn test` end in `BUILD FAILURE`, and a real failure would be hard to spot. `@Disabled` wouldn't help, because the tests would never run and there would be no output to read. A tag solves both problems: the tests stay out of the normal build, but can still be run when you ask for them.

### How they are excluded

Two pieces work together:

1. The class is labelled with a tag:

   ```java
   @Tag("expected-failure-tests")
   class TestOutcomesTest {
   ```

2. The [parent POM](../pom.xml) sets a property that Maven Surefire, the plugin that runs the tests, reads as its list of tags to exclude:

   ```xml
   <excludedGroups>expected-failure-tests</excludedGroups>
   ```

Because the tag is on the class, all six of its tests are excluded, including the ones that would pass.

### How to override the exclusion

A value passed on the command line takes priority over the property in the POM. That makes `-DexcludedGroups=none` the usual override. `none` isn't a special keyword: it is just a tag that no test uses, so nothing is excluded.

| Goal | Command |
| --- | --- |
| Run only the failure demo | `mvn test -pl module-01 -Dtest=TestOutcomesTest -DexcludedGroups=none` |
| Run the whole module, including the demo | `mvn test -pl module-01 -DexcludedGroups=none` |
| Run only the tests with this tag | `mvn test -pl module-01 -Dgroups=expected-failure-tests -DexcludedGroups=none` |
| Run the demo without failing the build | `mvn test -pl module-01 -Dtest=TestOutcomesTest -DexcludedGroups=none -Dmaven.test.failure.ignore=true` |

The exclusion has to be cleared even when you select the class or tag by name. These two commands report `BUILD SUCCESS` with `Tests run: 0`, because the exclusion still applies:

```bash
mvn test -pl module-01 -Dtest=TestOutcomesTest
mvn test -pl module-01 -Dgroups=expected-failure-tests
```

The exclusion is part of the Maven build, so running the class directly from your IDE normally ignores it, unless the IDE is set to run tests through Maven.

Keep the override on the command line. Removing the tag or the POM property would make every normal build fail.

### What to expect

Running the demo ends with this summary and `BUILD FAILURE`, which is the intended result:

```text
Tests run: 6, Failures: 2, Errors: 1, Skipped: 2
```

| Test | Outcome | Counted by Maven as | Why |
| --- | --- | --- | --- |
| `succeedingTest` | Succeeded | Passed | `4 + 6` is `10` |
| `failingTest` | Failed | Failure | The expectation is wrong on purpose: `expected: <5> but was: <4>` |
| `failingWithFail` | Failed | Failure | `fail()` always fails |
| `failingWithUnexpectedException` | Failed | Error | Dividing by zero throws an uncaught `ArithmeticException` |
| `skippedTest` | Skipped | Skipped | `@Disabled` stops it from starting |
| `abortedTest` | Aborted | Skipped | `assumeTrue` stops it before `fail()` is reached |

The Maven summary is less detailed than JUnit's outcomes in two ways:

- **Failures and errors:** Maven reports a failed assertion as a failure and an unexpected exception as an error. JUnit treats both as failed.
- **Skipped and aborted:** Maven counts both as skipped. Compare the result icons in your IDE to see the difference.
