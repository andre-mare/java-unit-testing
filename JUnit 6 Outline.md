# JUnit 6 Learning Outline

This outline shows everything you will learn in this repository and which module teaches it. There are nine modules, from a first `@Test` to custom templates and parallel execution. Each topic is a runnable test you can read and change, so you can learn at your own pace.

## How to use this outline

Work through the modules in order, or go straight to the topic you need. The modules build on each other conceptually, but they share no code, so any of them can be run on its own.

For each section:

1. Read the section in the module's README, which explains its concepts and test classes.
2. Read the test classes, and run them with `mvn test` or from your IDE.
3. Change the code or the tests, predict what will happen, and read the output.

**What you need**

- JDK 26 or later, and Maven 3.9 or later
- JUnit 6.1.3 and Maven Surefire 3.5.5, which the build already sets up
- AssertJ, Mockito and Jimfs, which the modules that use them already include

## The modules at a glance

| Module | Level | What you will learn | Practised on |
| --- | --- | --- | --- |
| [1. Foundations](module-01/README.md) | Beginner | Writing and running tests, test outcomes, class rules, lifecycle methods, the test tree, composed annotations | A calculator |
| [2. Assertions, Assumptions and Exceptions](module-02/README.md) | Beginner | Checking results, stopping tests in the wrong environment, testing error paths | A bank account |
| [3. Readable and Skippable Tests](module-03/README.md) | Beginner | Naming tests so reports read well, disabling tests cleanly | A password policy |
| [4. Structuring Test Suites](module-04/README.md) | Intermediate | Test instances, nested tests, reusable contract tests | A bounded stack and money |
| [5. Controlling What Runs, in What Order, and for How Long](module-05/README.md) | Intermediate | Conditions, tags, execution order, timeouts | App configuration, onboarding and a rate limiter |
| [6. Dependency Injection](module-06/README.md) | Intermediate | Parameter resolvers, writing your own, mocking with Mockito | An OAuth 2.0 token service |
| [7. Data-Driven Tests](module-07/README.md) | Intermediate to advanced | Repeated and parameterized tests, argument sources, conversion, aggregation, parameterized classes | IBANs, payments and tokens |
| [8. Templates and Dynamic Tests](module-08/README.md) | Advanced | Test templates, class templates, tests generated at runtime | Currencies, consents, primes and scopes |
| [9. Built-in Extensions and Parallel Execution](module-09/README.md) | Advanced | Temporary directories, closing resources, global JVM state, parallel execution | Audit logs, sessions and settings |

## Module 1: Foundations

You will learn how to write and run a test, read its result, and organize a test class.

The minimum for a first test is the `junit-jupiter` dependency, a class under `src/test/java`, a method annotated with `@Test`, and one assertion:

```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class CalculatorFirstTest {
    @Test
    void addsTwoNumbers() {
        assertEquals(5, new Calculator().add(2, 3));
    }
}
```

1. **1.1 Your first test** ([guide](https://docs.junit.org/6.1.3/writing-tests/test-classes-and-methods.html))
   - Running tests with `mvn test` and from your IDE
   - Arrange, Act, Assert
   - `assertEquals`, with the expected value first and the actual value second
2. **1.2 Test outcomes** ([guide](https://docs.junit.org/6.1.3/writing-tests/test-classes-and-methods.html))
   - Succeeded, failed, skipped and aborted
   - The ways a test can fail: a wrong assertion, `fail()` and an uncaught exception
   - A first look at `@Disabled`, `assumeTrue` and `@Tag`
   - Reading a failure message such as `expected: <5> but was: <4>`
3. **1.3 Test class rules and inheritance** ([guide](https://docs.junit.org/6.1.3/writing-tests/test-classes-and-methods.html))
   - Classes: not `abstract`, one constructor, and no need for `public`
   - Methods: not `private` or `static`, and returning `void`
   - Java `record` classes as test classes
   - Inheriting fields, test methods and lifecycle methods, and overriding a test
4. **1.4 The test lifecycle** ([guide](https://docs.junit.org/6.1.3/writing-tests/test-classes-and-methods.html))
   - `@BeforeEach` and `@AfterEach` around every test
   - `@BeforeAll` and `@AfterAll` once for the class, `static` by default
   - A new test instance for each test method
5. **1.5 The test tree** ([guide](https://docs.junit.org/6.1.3/writing-tests/definitions.html))
   - Containers and tests
   - Why only `@Test` makes a test directly, while `@RepeatedTest`, `@ParameterizedTest` and `@TestFactory` make containers
6. **1.6 Annotation inheritance** ([guide](https://docs.junit.org/6.1.3/writing-tests/annotations.html))
   - Inherited from a superclass: `@Tag` on a class and `@TestMethodOrder`
   - Not inherited: `@DisplayName` and `@Disabled`
   - `@TestMethodOrder`, `@Order` and `TestInfo`, as a first look
7. **1.7 Composed annotations** ([guide](https://docs.junit.org/6.1.3/writing-tests/annotations.html#annotations))
   - Meta-annotations and runtime retention
   - Building `@Fast`, `@FastTest` and `@UnitTest`
   - Filtering by tag with `-Dgroups`

## Module 2: Assertions, Assumptions and Exceptions

You will learn how to check behaviour precisely, tell a failed test from an aborted one, and test error paths.

1. **2.1 Assertions** ([guide](https://docs.junit.org/6.1.3/writing-tests/assertions.html))
   - Single values: `assertEquals`, `assertNotEquals`, `assertTrue`, `assertFalse`, `assertNull`, `assertNotNull`, `assertSame`, `assertNotSame`, `assertInstanceOf` and `fail`
   - Collections and text: `assertArrayEquals`, `assertIterableEquals` and `assertLinesMatch`
   - Failure messages as a `String` or a lazy `Supplier<String>`
   - Grouped and dependent assertions with `assertAll`
   - `assertTimeout` and `assertTimeoutPreemptively`, and the `ThreadLocal` side effect of the preemptive form
   - Why `BigDecimal.equals` fails on scale, and writing a custom assertion
   - AssertJ's `assertThat` as a third-party alternative
2. **2.2 Assumptions** ([guide](https://docs.junit.org/6.1.3/writing-tests/assumptions.html))
   - `assumeTrue`, `assumeFalse`, `assumingThat` and `abort`
   - `TestAbortedException`, and how an aborted test differs from a failed one
   - An assumption in `@BeforeAll`, which stops the whole class
3. **2.3 Exception handling** ([guide](https://docs.junit.org/6.1.3/writing-tests/exception-handling.html))
   - Why an uncaught exception fails a test, and why a `throws` clause isn't an expectation
   - `assertThrows` and `assertThrowsExactly`, and checking the exception they return
   - `assertDoesNotThrow`
   - Checking that a failed operation changed nothing

## Module 3: Readable and Skippable Tests

You will learn how to make test reports read like specifications, and how to switch tests off cleanly.

1. **3.1 Display names** ([guide](https://docs.junit.org/6.1.3/writing-tests/display-names.html))
   - `@DisplayName` with spaces, special characters, other languages and emoji
   - How control characters are replaced: `<CR>`, `<LF>` and U+FFFD
   - Generators through `@DisplayNameGeneration`: `Standard`, `Simple`, `ReplaceUnderscores` and `IndicativeSentences`
   - `@IndicativeSentencesGeneration` and `@SentenceFragment`
   - Setting a default with `junit.jupiter.displayname.generator.default`
   - Which name wins when several apply
   - Writing a custom `DisplayNameGenerator`
2. **3.2 Disabling tests** ([guide](https://docs.junit.org/6.1.3/writing-tests/disabling-tests.html))
   - `@Disabled` on a class or a method, with a reason such as a ticket number
   - What still runs for a disabled method, and why nothing runs for a disabled class
   - Why `@Disabled` isn't inherited
   - Overriding it for one run with `junit.jupiter.conditions.deactivate`

## Module 4: Structuring Test Suites

You will learn how to control test instances, group tests by scenario, and reuse tests across implementations.

1. **4.1 Test instance lifecycle** ([guide](https://docs.junit.org/6.1.3/writing-tests/test-instance-lifecycle.html))
   - `PER_METHOD`, the default: a new instance for each test
   - `@TestInstance(Lifecycle.PER_CLASS)`: one instance, and non-static `@BeforeAll` and `@AfterAll`
   - State leaking between tests under `PER_CLASS`, and resetting it in `@BeforeEach`
   - Setting a default with `junit.jupiter.testinstance.lifecycle.default`
2. **4.2 Nested tests** ([guide](https://docs.junit.org/6.1.3/writing-tests/nested-tests.html))
   - `@Nested` on non-static inner classes, to any depth
   - Layered `@BeforeEach` setup, and running one inner test alone
   - The order of lifecycle methods across levels, including `@BeforeAll` and `@AfterAll`
   - `@Nested` inside a `@ParameterizedClass`, as a first look
3. **4.3 Test interfaces and contract tests** ([guide](https://docs.junit.org/6.1.3/writing-tests/test-interfaces-and-default-methods.html))
   - `@Test`, `@BeforeEach` and `@AfterEach` on `default` methods
   - `@BeforeAll` and `@AfterAll` as `default` methods with `PER_CLASS`
   - `@Tag`, `@ExtendWith` and `@TestInstance` inherited from an interface
   - Contract tests: one set of rules run against every implementation
   - `EqualsContract` and `ComparableContract` for any value type

## Module 5: Controlling What Runs, in What Order, and for How Long

You will learn how to run the right tests in the right environment, filter a suite, and stop slow tests.

1. **5.1 Conditional execution** ([guide](https://docs.junit.org/6.1.3/writing-tests/conditional-test-execution.html))
   - Operating system and architecture: `@EnabledOnOs` and `@DisabledOnOs`
   - Java version: `@EnabledOnJre`, `@DisabledOnJre`, `@EnabledForJreRange` and `@DisabledForJreRange`, by enum constant or plain number
   - Native image: `@EnabledInNativeImage` and `@DisabledInNativeImage`
   - System properties: `@EnabledIfSystemProperty` and `@DisabledIfSystemProperty`
   - Environment variables: `@EnabledIfEnvironmentVariable` and `@DisabledIfEnvironmentVariable`
   - Custom conditions: `@EnabledIf` and `@DisabledIf`, with local or external methods
   - `disabledReason`, combining conditions, and conditions on a whole class
   - Composed condition annotations, such as `@Integration` and `@UnixOnly`
2. **5.2 Tags and filtering** ([guide](https://docs.junit.org/6.1.3/writing-tests/tagging-and-filtering.html))
   - `@Tag` on classes and methods, and the rules for tag names
   - Composed tag annotations, such as `@Smoke`, `@Security` and `@LongRunning`
   - Tag expressions with `!`, `&`, `|`, `any()` and `none()`
   - Filtering with `-Dgroups` and `-DexcludedGroups`, and with Maven profiles
3. **5.3 Execution order** ([guide](https://docs.junit.org/6.1.3/writing-tests/test-execution-order.html))
   - Why the default order is repeatable but not obvious
   - `@TestMethodOrder` with `MethodName`, `DisplayName`, `OrderAnnotation`, `Random` and `Default`
   - `@Order`, including negative numbers and methods without it
   - `@TestClassOrder` for `@Nested` classes
   - Repeating a random order with a seed
   - Ordered scenario tests, and hidden dependencies that random order exposes
4. **5.4 Timeouts** ([guide](https://docs.junit.org/6.1.3/writing-tests/timeouts.html))
   - Avoiding timeouts by controlling time with a fake clock
   - `@Timeout` on tests, lifecycle methods and classes, and what a class-level timeout doesn't cover
   - Thread modes: `SAME_THREAD`, `SEPARATE_THREAD` and `INFERRED`
   - How timeouts apply to `@RepeatedTest` and `@TestFactory`
   - Polling until a condition holds
   - Default timeouts, switching timeouts off with `junit.jupiter.execution.timeout.mode`, and thread dumps

## Module 6: Dependency Injection

You will learn how JUnit passes objects to your tests, how to write your first extensions, and how to replace collaborators.

1. **6.1 Built-in parameter resolvers** ([guide](https://docs.junit.org/6.1.3/writing-tests/dependency-injection-for-constructors-and-methods.html))
   - `TestInfo`: display name, tags, class and method
   - `RepetitionInfo`: current and total repetitions, failure count and threshold
   - `TestReporter`: `publishEntry`, `publishFile` and `publishDirectory`
   - Why secrets must stay out of reports
2. **6.2 Custom parameter resolvers**
   - `ParameterResolver`, and the `TypeBasedParameterResolver<T>` shortcut
   - Registering extensions with `@ExtendWith` and `@RegisterExtension`
   - `ExtensionContext.Store`, to share objects within one test
   - `ParameterContext`, to configure a value with an annotation
   - `BeforeEachCallback`, as a first look at lifecycle callbacks
   - Why competing resolvers fail
3. **6.3 Mocking with Mockito**
   - `MockitoExtension`, `@Mock`, `@Captor` and `@InjectMocks`
   - `when`, `doThrow`, `verify`, `never`, `verifyNoInteractions`, `any`, `argThat`, `ArgumentCaptor` and `inOrder`
   - `@Mock` on test method parameters
   - Strict stubs, `@MockitoSettings` and `lenient()`
   - When to choose fakes over mocks

## Module 7: Data-Driven Tests

You will learn how to run one test, or a whole class, against many inputs. This is the largest module, so it may take a few sessions.

1. **7.1 Repeated tests** ([guide](https://docs.junit.org/6.1.3/writing-tests/repeated-tests.html))
   - `@RepeatedTest` with `value`, `name` and `failureThreshold`
   - Name placeholders and `LONG_DISPLAY_NAME`
   - `RepetitionInfo` in tests and `@BeforeEach`
   - Hunting flaky tests, and making randomness repeatable with a seed
2. **7.2 Consuming arguments** ([guide](https://docs.junit.org/6.1.3/writing-tests/parameterized-classes-and-tests.html))
   - `@ParameterizedTest`: a source of arguments and parameters to receive them
   - The parameter order rule: indexed parameters, then aggregators, then other resolvers
   - The lifecycle of an invocation
   - `autoCloseArguments`, and compiling with `-parameters`
3. **7.3 Argument sources**
   - `@ValueSource`
   - `@NullSource`, `@EmptySource` and `@NullAndEmptySource`
   - `@EnumSource` with `value`, `names`, `from`, `to` and `mode`
   - `@MethodSource`: local, by convention and external
   - `@FieldSource`: arrays, collections and `Supplier<Stream>`
   - `@CsvSource`: delimiters, quotes, empty and null values, whitespace, text blocks and headers
   - `@CsvFileSource`: `resources`, `files`, `numLinesToSkip` and `useHeadersInDisplayName`
   - `@ArgumentsSource` with a custom `ArgumentsProvider`
   - Combining and repeating sources
4. **7.4 Argument count validation**
   - `argumentCountValidation = STRICT` and `junit.jupiter.params.argumentCountValidation`
   - `allowZeroInvocations`
5. **7.5 Argument conversion**
   - Implicit conversion: widening numbers, and text to enums, `java.time` types, `BigDecimal`, `Currency`, `Locale`, `UUID`, `Class` and more
   - Fallback conversion through a factory method or constructor, and when it doesn't apply
   - Explicit conversion: `@ConvertWith`, `SimpleArgumentConverter`, `TypedArgumentConverter` and `@JavaTimeConversionPattern`
   - Composed converter annotations
6. **7.6 Argument aggregation**
   - `ArgumentsAccessor`
   - `@AggregateWith` with `SimpleArgumentsAggregator`
   - Composed aggregator annotations, such as `@CsvToTransfer`
7. **7.7 Invocation display names**
   - Placeholders: `{index}`, `{0}`, `{arguments}`, `{argumentsWithNames}`, `{argumentSetName}`, `{argumentSetNameOrArgumentsWithNames}` and `{displayName}`
   - Quoted text arguments and `quoteTextArguments`
   - `Named` arguments and `argumentSet`
8. **7.8 Parameterized classes**
   - `@ParameterizedClass` with `@Parameter` field injection, constructor injection and record classes
   - `@Nested` classes inside a parameterized class
   - `@BeforeParameterizedClassInvocation`, `@AfterParameterizedClassInvocation` and `injectArguments`

## Module 8: Templates and Dynamic Tests

You will learn how repeated and parameterized tests work underneath, and how to generate tests at runtime.

1. **8.1 Test templates** ([guide](https://docs.junit.org/6.1.3/writing-tests/test-templates.html))
   - `@TestTemplate` with a `TestTemplateInvocationContextProvider`
   - `TestTemplateInvocationContext`: naming an invocation and adding extensions for it
   - Allowing zero invocations
   - Building a custom annotation, `@ForEachCurrency`
   - Why `@RepeatedTest` and `@ParameterizedTest` are test templates
2. **8.2 Class templates** ([guide](https://docs.junit.org/6.1.3/writing-tests/class-templates.html))
   - `@ClassTemplate` with a `ClassTemplateInvocationContextProvider`
   - Running one test class against two repository implementations
   - `TestInstancePostProcessor`, and cleaning up through `ExtensionContext.Store`
   - Why `@ParameterizedClass` is a class template
3. **8.3 Dynamic tests** ([guide](https://docs.junit.org/6.1.3/writing-tests/dynamic-tests.html))
   - `@TestFactory` and its return types
   - `DynamicTest` and `DynamicContainer`
   - `DynamicTest.stream`, `Named` inputs and `NamedExecutable`
   - The lifecycle caveat: `@BeforeEach` and `@AfterEach` run once per factory
   - Tests built from a data file, with URI test sources
   - `executionMode` and `childExecutionMode`

## Module 9: Built-in Extensions and Parallel Execution

You will learn how to manage files, resources and global JVM state safely, and then how to run tests in parallel.

1. **9.1 Temporary directories** ([guide](https://docs.junit.org/6.1.3/writing-tests/built-in-extensions.html#TempDirectory))
   - `@TempDir` on fields, constructor parameters and method parameters, as a `Path` or a `File`
   - Several directories, and a shared `static` directory
   - `cleanup` with `ALWAYS`, `ON_SUCCESS` and `NEVER`
   - `factory` with a custom `TempDirFactory`, including in-memory directories with Jimfs
   - `deletionStrategy` with `Standard` and `IgnoreFailures`
   - Checking log files with `assertLinesMatch`
2. **9.2 Closing resources automatically**
   - `@AutoClose` on instance and `static` fields
   - Naming a custom close method
   - When fields close under `PER_METHOD` and `PER_CLASS`
3. **9.3 Default locale and time zone**
   - `@DefaultLocale`, by language tag, by parts or with a `LocaleProvider`
   - `@DefaultTimeZone`, by zone ID or with a `TimeZoneProvider`
   - Testing behaviour across a clock change
   - `@ReadsDefaultLocale` and `@ReadsDefaultTimeZone`
4. **9.4 System properties**
   - `@SetSystemProperty` and `@ClearSystemProperty`
   - `@RestoreSystemProperties`
   - `@ReadsSystemProperty`
5. **9.5 Parallel execution** ([guide](https://docs.junit.org/6.1.3/writing-tests/parallel-execution.html))
   - `junit.jupiter.execution.parallel.enabled`, `mode.default` and `mode.classes.default`
   - `@Execution` with `CONCURRENT` and `SAME_THREAD`
   - The `dynamic` and `fixed` thread strategies
   - `@ResourceLock` with `READ` and `READ_WRITE`, `target = CHILDREN`, and `ResourceLocksProvider`
   - The predefined `Resources` constants
   - `@Isolated`
   - Recognizing a race, and why a lock only protects against tests that also declare it

## Learning from failures

Seven modules include classes that fail on purpose, so you can see real failure output. These classes are tagged `expected-failure-tests` and left out of the normal build. To run one, clear the exclusion and name the class:

```bash
mvn test -pl module-04 -DexcludedGroups=none -Dtest=PerClassStateLeakDemoTest
```

| Module | Classes | What you will see |
| --- | --- | --- |
| 1 | `TestOutcomesTest` | Every test outcome side by side |
| 2 | `AssertionFailuresDemoTest`, `ExceptionFailuresDemoTest` | The messages from each kind of assertion and exception check |
| 4 | `PerClassStateLeakDemoTest` | State leaking between tests |
| 5 | `HiddenCouplingDemoTest`, `TimeoutFailuresDemoTest` | A hidden dependency between tests, and timeout messages |
| 6 | `ResolutionFailuresDemoTest`, `CompetingResolversDemoTest`, `MockitoFailuresDemoTest` | Injection errors, and common Mockito errors |
| 7 | `FlakyHuntDemoTest`, `ArgumentCountFailureDemoTest` | `failureThreshold` in action, and argument count errors |
| 8 | `TemplateFailureDemoTest`, `DynamicFailureDemoTest` | Template configuration errors, and dynamic tests with separate results |

Module 9's `AuditTrailRaceDemoTest` is different. It stays in the normal build, because it only fails with parallel execution on. Module 3 has no failing classes.

## Annotation reference

Use this table to find where each annotation or API is taught.

| Module | Annotations and APIs |
| --- | --- |
| 1 | `@Test`, `@BeforeAll`, `@BeforeEach`, `@AfterEach`, `@AfterAll`, composed annotations |
| 2 | `Assertions`, `Assumptions` |
| 3 | `@DisplayName`, `@DisplayNameGeneration`, `@IndicativeSentencesGeneration`, `@SentenceFragment`, `@Disabled` |
| 4 | `@TestInstance`, `@Nested`, test interfaces with `default` methods |
| 5 | `@EnabledOnOs`, `@DisabledOnOs`, `@EnabledOnJre`, `@DisabledOnJre`, `@EnabledForJreRange`, `@DisabledForJreRange`, `@EnabledInNativeImage`, `@DisabledInNativeImage` |
| 5 | `@EnabledIfSystemProperty`, `@DisabledIfSystemProperty`, `@EnabledIfEnvironmentVariable`, `@DisabledIfEnvironmentVariable`, `@EnabledIf`, `@DisabledIf` |
| 5 | `@Tag`, `@TestMethodOrder`, `@TestClassOrder`, `@Order`, `@Timeout` |
| 6 | `@ExtendWith`, `@RegisterExtension`, `TestInfo`, `RepetitionInfo`, `TestReporter`, `ParameterResolver` |
| 6 | Mockito: `@Mock`, `@Captor`, `@InjectMocks`, `@MockitoSettings` |
| 7 | `@RepeatedTest`, `@ParameterizedTest`, `@ParameterizedClass`, `@Parameter` |
| 7 | `@ValueSource`, `@NullSource`, `@EmptySource`, `@NullAndEmptySource`, `@EnumSource`, `@MethodSource`, `@FieldSource`, `@CsvSource`, `@CsvFileSource`, `@ArgumentsSource` |
| 7 | `@ConvertWith`, `@JavaTimeConversionPattern`, `@AggregateWith`, `ArgumentsAccessor`, `@BeforeParameterizedClassInvocation`, `@AfterParameterizedClassInvocation` |
| 8 | `@TestTemplate`, `@ClassTemplate`, `@TestFactory`, `DynamicTest`, `DynamicContainer` |
| 9 | `@TempDir`, `@AutoClose`, `@DefaultLocale`, `@DefaultTimeZone`, `@ReadsDefaultLocale`, `@ReadsDefaultTimeZone` |
| 9 | `@SetSystemProperty`, `@ClearSystemProperty`, `@RestoreSystemProperties`, `@ReadsSystemProperty` |
| 9 | `@Execution`, `@ResourceLock`, `@Isolated` |

## What isn't covered

These JUnit topics are in the same user guide, but outside this repository:

- The full extension model, including callbacks and interceptors
- The Suite engine (`@Suite`), the Test Kit and the Launcher API
- Kotlin coroutine tests
- Hamcrest, Truth and Checkstyle rules for assertion libraries
- Custom parallel strategies

## Sources

All from the JUnit 6.1.3 User Guide, "Writing Tests" section: [Annotations](https://docs.junit.org/6.1.3/writing-tests/annotations.html) · [Definitions](https://docs.junit.org/6.1.3/writing-tests/definitions.html) · [Test Classes and Methods](https://docs.junit.org/6.1.3/writing-tests/test-classes-and-methods.html) · [Display Names](https://docs.junit.org/6.1.3/writing-tests/display-names.html) · [Assertions](https://docs.junit.org/6.1.3/writing-tests/assertions.html) · [Assumptions](https://docs.junit.org/6.1.3/writing-tests/assumptions.html) · [Exception Handling](https://docs.junit.org/6.1.3/writing-tests/exception-handling.html) · [Disabling Tests](https://docs.junit.org/6.1.3/writing-tests/disabling-tests.html) · [Conditional Test Execution](https://docs.junit.org/6.1.3/writing-tests/conditional-test-execution.html) · [Tagging and Filtering](https://docs.junit.org/6.1.3/writing-tests/tagging-and-filtering.html) · [Test Execution Order](https://docs.junit.org/6.1.3/writing-tests/test-execution-order.html) · [Test Instance Lifecycle](https://docs.junit.org/6.1.3/writing-tests/test-instance-lifecycle.html) · [Nested Tests](https://docs.junit.org/6.1.3/writing-tests/nested-tests.html) · [Dependency Injection](https://docs.junit.org/6.1.3/writing-tests/dependency-injection-for-constructors-and-methods.html) · [Test Interfaces](https://docs.junit.org/6.1.3/writing-tests/test-interfaces-and-default-methods.html) · [Repeated Tests](https://docs.junit.org/6.1.3/writing-tests/repeated-tests.html) · [Parameterized Classes and Tests](https://docs.junit.org/6.1.3/writing-tests/parameterized-classes-and-tests.html) · [Class Templates](https://docs.junit.org/6.1.3/writing-tests/class-templates.html) · [Test Templates](https://docs.junit.org/6.1.3/writing-tests/test-templates.html) · [Dynamic Tests](https://docs.junit.org/6.1.3/writing-tests/dynamic-tests.html) · [Timeouts](https://docs.junit.org/6.1.3/writing-tests/timeouts.html) · [Parallel Execution](https://docs.junit.org/6.1.3/writing-tests/parallel-execution.html) · [Built-in Extensions](https://docs.junit.org/6.1.3/writing-tests/built-in-extensions.html)
