# Module 4: Structuring Test Suites

This module covers how to organize a growing test suite: how JUnit creates test instances and when state is shared, how nested classes group tests by scenario, and how interfaces let you reuse tests across classes.

## Table of Contents

- [Module 4: Structuring Test Suites](#module-4-structuring-test-suites)
  - [Table of Contents](#table-of-contents)
  - [The Domain: a Bounded Stack and Money](#the-domain-a-bounded-stack-and-money)
    - [Production classes](#production-classes)
    - [Test helpers](#test-helpers)
  - [Running the Module](#running-the-module)
  - [Sections](#sections)
    - [Section 1: Test Instance Lifecycle](#section-1-test-instance-lifecycle)
    - [Section 2: Nested Tests](#section-2-nested-tests)
    - [Section 3: Test Interfaces and Contract Tests](#section-3-test-interfaces-and-contract-tests)
  - [Deliberately Failing Tests](#deliberately-failing-tests)
    - [Why the failures are kept](#why-the-failures-are-kept)
    - [How they are excluded](#how-they-are-excluded)
    - [How to override the exclusion](#how-to-override-the-exclusion)
    - [What to expect](#what-to-expect)

## The Domain: a Bounded Stack and Money

This module has two small domains, each suited to a different structuring technique.

A [`BoundedStack`](src/main/java/com/example/stack/BoundedStack.java) is a last-in, first-out stack with a fixed maximum size. It moves between clear states (empty, partly filled and full), which suits nested tests. It also has two implementations, so one set of tests can verify both.

| Method | Behaviour | If the rule is broken |
| --- | --- | --- |
| Creating a stack | The capacity must be at least 1 | Throws `IllegalArgumentException` |
| `push` | Adds an element to the top. The element must not be `null`, and the stack must not be full | Throws `NullPointerException` or `IllegalStateException` |
| `pop` | Removes and returns the top element | Throws `NoSuchElementException` when the stack is empty |
| `peek` | Returns the top element without removing it | Throws `NoSuchElementException` when the stack is empty |
| `clear` | Removes all elements | Not applicable |
| `size`, `capacity`, `isEmpty`, `isFull` | Report the current state | Not applicable |

[`Money`](src/main/java/com/example/money/Money.java) is a value object: an amount in a currency. Two `Money` values with the same amount and currency are equal, which makes it a good subject for reusable equality and ordering tests.

| Behaviour | Rule | If the rule is broken |
| --- | --- | --- |
| `Money.of` | The amount is stored at the currency's number of decimals, so `10` and `10.00` euros are equal. Extra decimals are never rounded | Throws `IllegalArgumentException` for too many decimals, such as `100.5` yen |
| `plus`, `compareTo` | Both values must have the same currency | Throws `IllegalArgumentException` |
| `toString` | Prints the currency and amount, such as `EUR 10.00` | Not applicable |

### Production classes

| Class | Purpose |
| --- | --- |
| [`BoundedStack`](src/main/java/com/example/stack/BoundedStack.java) | The interface that defines how a stack behaves |
| [`ArrayBoundedStack`](src/main/java/com/example/stack/ArrayBoundedStack.java) | An implementation backed by a fixed-size array |
| [`LinkedBoundedStack`](src/main/java/com/example/stack/LinkedBoundedStack.java) | An implementation backed by a linked list of nodes |
| [`Money`](src/main/java/com/example/money/Money.java) | A record holding an amount and a currency |

### Test helpers

These types are in the [`section_03`](src/test/java/com/example/module_04/section_03) package. They aren't test classes, so JUnit doesn't run them directly. Their names deliberately avoid starting or ending in `Test`, because Maven would otherwise try to run them as test classes.

| Type | Purpose |
| --- | --- |
| [`BoundedStackContract`](src/test/java/com/example/module_04/section_03/BoundedStackContract.java) | An interface with ten tests that every `BoundedStack` implementation must pass |
| [`ValueFactory`](src/test/java/com/example/module_04/section_03/ValueFactory.java) | The parent of the next two contracts. It supplies the value under test |
| [`EqualsContract`](src/test/java/com/example/module_04/section_03/EqualsContract.java) | An interface with five tests of the rules for `equals` and `hashCode` |
| [`ComparableContract`](src/test/java/com/example/module_04/section_03/ComparableContract.java) | An interface with three tests of the rules for `compareTo` |
| [`LifecycleLogging`](src/test/java/com/example/module_04/section_03/LifecycleLogging.java) | An interface that gives a class four logging lifecycle methods and the `PER_CLASS` lifecycle |
| [`Timed`](src/test/java/com/example/module_04/section_03/Timed.java) | An interface with no methods that gives a class a tag and an extension |
| [`ExecutionTimeExtension`](src/test/java/com/example/module_04/section_03/ExecutionTimeExtension.java) | A small extension that prints how long each test took. Module 6 covers how to write extensions |

## Running the Module

Run these commands from the repository root.

| Goal | Command | Result |
| --- | --- | --- |
| Run the module | `mvn test -pl module-04` | 67 tests pass; `PerClassStateLeakDemoTest` is excluded |
| Run one class | `mvn test -pl module-04 -Dtest=BoundedStackNestedTest` | 12 tests pass |
| Run the `contract` tag | `mvn test -pl module-04 -Dgroups=contract` | 21 tests pass |
| Run the `timed` tag | `mvn test -pl module-04 -Dgroups=timed` | 3 tests pass |
| Run the failure demo | `mvn test -pl module-04 -Dtest=PerClassStateLeakDemoTest -DexcludedGroups=none` | `BUILD FAILURE`, as intended |

Maven's output for nested classes can be confusing, because it reports them separately and shows `Tests run: 0` for the outer levels. The overall total at the end is correct. Your IDE's result tree shows the structure more clearly.

## Sections

Each section has its own package under [`src/test/java/com/example/module_04`](src/test/java/com/example/module_04).

### Section 1: Test Instance Lifecycle

**What it tests:** how many instances of a test class JUnit creates, and what that means for state in its fields. The tests use simple stack operations and count the instances. Start with `PerMethodLifecycleTest`.

| Class | What it tests |
| --- | --- |
| [`PerMethodLifecycleTest`](src/test/java/com/example/module_04/section_01/PerMethodLifecycleTest.java) | That three tests use three instances, so pushes in one test never appear in another |
| [`PerClassLifecycleTest`](src/test/java/com/example/module_04/section_01/PerClassLifecycleTest.java) | That three tests share one instance, that `@BeforeAll` and `@AfterAll` don't need to be `static`, and that resetting the stack in `@BeforeEach` keeps the tests independent |
| [`PerClassStateLeakDemoTest`](src/test/java/com/example/module_04/section_01/PerClassStateLeakDemoTest.java) | Shows what happens without that reset: one test [fails on purpose](#deliberately-failing-tests) because of an element left by the other |
| [`DefaultLifecycleTest`](src/test/java/com/example/module_04/section_01/DefaultLifecycleTest.java) | Declares no lifecycle and prints how many instances it used, so you can see the effect of changing the default |

**JUnit concepts**

- **`@TestInstance`** sets how JUnit creates instances of a test class.
- **`Lifecycle.PER_METHOD`** is the default: a new instance for each test. Instance fields start fresh, so tests can't affect each other through them. `@BeforeAll` and `@AfterAll` must be `static`, because they run when no test instance is active.
- **`Lifecycle.PER_CLASS`** uses one instance for all the tests in a class. `@BeforeAll` and `@AfterAll` can be instance methods, so an expensive fixture can live in an ordinary field. The cost is that fields keep their values between tests, so you need to reset them yourself in `@BeforeEach`.
- **Default lifecycle:** the `junit.jupiter.testinstance.lifecycle.default` setting changes the lifecycle for classes that don't declare one.

**Try it:** `DefaultLifecycleTest` prints its instance count, so compare these two runs:

```bash
mvn test -pl module-04 -Dtest=DefaultLifecycleTest
mvn test -pl module-04 -Dtest=DefaultLifecycleTest -Djunit.jupiter.testinstance.lifecycle.default=per_class
```

The first prints `3 instance(s) for 3 tests` and the second prints `1 instance(s) for 3 tests`.

Don't apply this setting to the whole course. All the tests in this module still pass with it, but Module 1's `LifecycleDemoTest` expects one instance per test and fails.

### Section 2: Nested Tests

**What it tests:** the stack in each of its states, and the order in which lifecycle methods run across nesting levels.

| Class | What it tests |
| --- | --- |
| [`BoundedStackNestedTest`](src/test/java/com/example/module_04/section_02/BoundedStackNestedTest.java) | Twelve tests of an `ArrayBoundedStack` with a capacity of 3, grouped as a new stack, a stack after one push, and a full stack |
| [`NestedLifecycleOrderTest`](src/test/java/com/example/module_04/section_02/NestedLifecycleOrderTest.java) | The exact order of lifecycle methods across two levels, recorded in a shared log |
| [`BoundedStackCapacityTest`](src/test/java/com/example/module_04/section_02/BoundedStackCapacityTest.java) | That a `LinkedBoundedStack` starts empty, fills up and rejects one more element, with the whole class repeated for capacities of 1, 2 and 5 |

`BoundedStackNestedTest` reads as a specification in your IDE:

```text
A bounded stack with capacity 3
├─ cannot be created with capacity 0
└─ when new
   ├─ is empty, is not full, throws when popped, throws when peeked
   └─ after pushing an element
      ├─ is no longer empty, peek returns it, pop returns it
      └─ when filled to capacity
         └─ is full, rejects another push, pops in reverse order, accepts a push after a pop
```

Lifecycle methods run in this order, as `NestedLifecycleOrderTest` confirms:

```text
outer @BeforeAll
  inner @BeforeAll
    outer @BeforeEach
      inner @BeforeEach
        inner test
      inner @AfterEach
    outer @AfterEach
  inner @AfterAll
outer @AfterAll
```

**JUnit concepts**

- **`@Nested`** marks an inner class as a group of tests. It must be a non-static inner class, so its tests can read the fields of the classes around it.
- **Layered setup:** each level's `@BeforeEach` builds on the level above. For a test in `when filled to capacity`, JUnit runs `createStack`, then `pushAnElement`, then `fillUp`. This also means you can run one inner test by itself, and the setup above it still runs.
- **Lifecycle order:** "before" methods run from the outside in, and "after" methods run from the inside out.
- **`@BeforeAll` in a nested class:** Java 16 and later allow `static` methods in inner classes, so these can be used at every level.
- **`@ParameterizedClass` with `@Parameter`** runs a whole class once per value, including its nested classes, and puts the value into a field. Module 7 covers this in depth.

### Section 3: Test Interfaces and Contract Tests

**What it tests:** that both stack implementations follow the same rules, that `Money` follows the rules for equality and ordering, and that a class inherits behaviour from the interfaces it implements.

| Class | What it tests |
| --- | --- |
| [`ArrayBoundedStackTest`](src/test/java/com/example/module_04/section_03/ArrayBoundedStackTest.java) | `ArrayBoundedStack`, with the ten tests from `BoundedStackContract` and one of its own, which reuses the stack's slots after each `pop` |
| [`LinkedBoundedStackTest`](src/test/java/com/example/module_04/section_03/LinkedBoundedStackTest.java) | `LinkedBoundedStack`, with the ten tests from `BoundedStackContract`. The class only says how to create the stack |
| [`MoneyTest`](src/test/java/com/example/module_04/section_03/MoneyTest.java) | `Money`, with five tests from `EqualsContract`, three from `ComparableContract` and four of its own, which cover addition, mixed currencies, too many decimals and printing |
| [`InterfaceMixinTest`](src/test/java/com/example/module_04/section_03/InterfaceMixinTest.java) | That implementing `LifecycleLogging` and `Timed` gives a class their lifecycle methods, `PER_CLASS` lifecycle, tag and extension |

**JUnit concepts**

- **Test interfaces:** `@Test`, `@BeforeEach` and `@AfterEach` work on `default` methods in an interface, and a class that implements the interface inherits them as if it had declared them.
- **Contract tests:** an interface states the rules that every implementation must follow, and each test class only supplies the object to test. A new implementation gets the full suite by writing one method.
- **Inherited annotations:** a class inherits `@Tag`, `@ExtendWith` and `@TestInstance` from the interfaces it implements. So `implements Timed` replaces two annotations, and `-Dgroups=contract` selects both stack test classes, including the extra test in `ArrayBoundedStackTest`.
- **`@BeforeAll` and `@AfterAll` in an interface:** a `default` method can only use these with the `PER_CLASS` lifecycle, so `LifecycleLogging` declares it. Otherwise they must be `static`.
- **`@ExtendWith`** registers an extension, which adds behaviour around tests. Here, it times them.

**Try it:** in `LinkedBoundedStack.clear()`, remove the line `size = 0;` and run the module. `LinkedBoundedStackTest.clearEmptiesTheStack` fails while `ArrayBoundedStackTest` still passes, so the failure points to the broken implementation. `PerClassLifecycleTest.pushTwo` also fails, because it uses the same class. Restore the line afterwards.

## Deliberately Failing Tests

In this module, [`PerClassStateLeakDemoTest`](src/test/java/com/example/module_04/section_01/PerClassStateLeakDemoTest.java) is the only class that fails on purpose. Other modules have similar classes, listed in the [root README](../README.md#deliberately-failing-tests).

### Why the failures are kept

The point of this class is to show you a real state leak, which is a common cost of the `PER_CLASS` lifecycle. The bug only appears when the tests run together, so the class can't show it without failing.

Left in the normal build, though, it would make every `mvn test` end in `BUILD FAILURE`, and a real failure would be hard to spot. `@Disabled` wouldn't help, because the tests would never run and there would be no output to read. A tag solves both problems: the tests stay out of the normal build, but can still be run when you ask for them.

### How they are excluded

Two pieces work together:

1. The class is labelled with a tag:

   ```java
   @Tag("expected-failure-tests")
   @TestInstance(Lifecycle.PER_CLASS)
   class PerClassStateLeakDemoTest {
   ```

2. The [parent POM](../pom.xml) sets a property that Maven Surefire, the plugin that runs the tests, reads as its list of tags to exclude:

   ```xml
   <excludedGroups>expected-failure-tests</excludedGroups>
   ```

Because the tag is on the class, both of its tests are excluded, including the one that passes.

### How to override the exclusion

A value passed on the command line takes priority over the property in the POM. That makes `-DexcludedGroups=none` the usual override. `none` isn't a special keyword: it is just a tag that no test uses, so nothing is excluded.

| Goal | Command |
| --- | --- |
| Run only the failure demo | `mvn test -pl module-04 -Dtest=PerClassStateLeakDemoTest -DexcludedGroups=none` |
| Run the whole module, including the demo | `mvn test -pl module-04 -DexcludedGroups=none` |
| Run only the tests with this tag | `mvn test -pl module-04 -Dgroups=expected-failure-tests -DexcludedGroups=none` |
| Run the demo without failing the build | `mvn test -pl module-04 -Dtest=PerClassStateLeakDemoTest -DexcludedGroups=none -Dmaven.test.failure.ignore=true` |

The exclusion has to be cleared even when you select the class or tag by name. These two commands report `BUILD SUCCESS` with `Tests run: 0`, because the exclusion still applies:

```bash
mvn test -pl module-04 -Dtest=PerClassStateLeakDemoTest
mvn test -pl module-04 -Dgroups=expected-failure-tests
```

The exclusion is part of the Maven build, so running the class directly from your IDE normally ignores it, unless the IDE is set to run tests through Maven.

Keep the override on the command line. Removing the tag or the POM property would make every normal build fail.

### What to expect

Running the demo ends with this summary and `BUILD FAILURE`, which is the intended result:

```text
Tests run: 2, Failures: 1, Errors: 0, Skipped: 0
```

Both tests push one element onto a shared stack and expect a size of 1:

| Test | Outcome when it runs first | Outcome when it runs second |
| --- | --- | --- |
| `firstPush` | Passes | Fails with `expected: <1> but was: <2>` |
| `secondPush` | Passes | Fails with `expected: <1> but was: <2>` |

Whichever test runs second finds the element left by the other. In a typical run, `secondPush` is the one that fails, but neither test is wrong in itself. You can see this by running each test alone, which passes:

```bash
mvn test -pl module-04 '-Dtest=PerClassStateLeakDemoTest#firstPush' -DexcludedGroups=none
mvn test -pl module-04 '-Dtest=PerClassStateLeakDemoTest#secondPush' -DexcludedGroups=none
```

A test that passes alone and fails with others is a typical sign of a state leak, and it's what makes this kind of bug hard to find. To fix it, clear the stack in `@BeforeEach`, as `PerClassLifecycleTest` does, or remove `PER_CLASS` so that each test gets its own instance.
