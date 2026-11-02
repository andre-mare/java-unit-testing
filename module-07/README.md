# Module 7: Data-Driven Tests

This module covers how to run the same test against many inputs: how to repeat a test, how to supply arguments from different sources, how JUnit converts and combines those arguments, how each invocation is named, and how to run a whole class once per set of arguments.

## Table of Contents

- [Module 7: Data-Driven Tests](#module-7-data-driven-tests)
  - [Table of Contents](#table-of-contents)
  - [The Domain: IBANs, Payments and Tokens](#the-domain-ibans-payments-and-tokens)
    - [Production classes](#production-classes)
    - [Test helpers and data](#test-helpers-and-data)
  - [Running the Module](#running-the-module)
  - [Sections](#sections)
    - [Section 1: Repeated Tests](#section-1-repeated-tests)
    - [Section 2: Consuming Arguments](#section-2-consuming-arguments)
    - [Section 3: Argument Sources](#section-3-argument-sources)
    - [Section 4: Argument Count Validation](#section-4-argument-count-validation)
    - [Section 5: Argument Conversion](#section-5-argument-conversion)
    - [Section 6: Argument Aggregation](#section-6-argument-aggregation)
    - [Section 7: Invocation Display Names](#section-7-invocation-display-names)
    - [Section 8: Parameterized Classes](#section-8-parameterized-classes)
  - [Deliberately Failing Tests](#deliberately-failing-tests)
    - [Why the failures are kept](#why-the-failures-are-kept)
    - [How they are excluded](#how-they-are-excluded)
    - [How to override the exclusion](#how-to-override-the-exclusion)
    - [What to expect](#what-to-expect)

## The Domain: IBANs, Payments and Tokens

This module has three small domains. Each has rules that are easiest to test as a table of inputs and expected results.

**IBANs.** An International Bank Account Number has a length that depends on its country and a checksum. [`IbanValidator`](src/main/java/com/example/iban/IbanValidator.java) checks both, ignoring spaces and letter case, and supports ten countries. [`Iban`](src/main/java/com/example/iban/Iban.java) is a value that can only be created from valid text, through `Iban.parse`.

**Payments.** A [`Transfer`](src/main/java/com/example/payments/Transfer.java) moves an [`Amount`](src/main/java/com/example/payments/Amount.java) between two accounts using a [`PaymentScheme`](src/main/java/com/example/payments/PaymentScheme.java). The rules in this module are simplified, and aren't a statement of real scheme limits.

| Scheme | Currency | Maximum | Instant? |
| --- | --- | --- | --- |
| `SEPA_CREDIT_TRANSFER` | Euros only | None | No |
| `SEPA_INSTANT` | Euros only | EUR 100,000.00 | Yes |
| `SEPA_DIRECT_DEBIT` | Euros only | None | No |
| `SWIFT_WIRE` | Any | None | No |

A `Transfer` checks itself when it's created. The two accounts must differ, the amount must be more than zero, the scheme must allow the amount, and the reference can't exceed 140 characters.

**Tokens.** [`TokenValidator`](src/main/java/com/example/tokens/TokenValidator.java) checks the claims of an access token against an expected issuer and audience at the current time. It reports every [`ClaimProblem`](src/main/java/com/example/tokens/ClaimProblem.java) it finds: `MISSING_SUBJECT`, `WRONG_ISSUER`, `WRONG_AUDIENCE`, `NOT_YET_VALID` or `EXPIRED`. An allowed clock skew tolerates small differences between clocks, so a token stays valid slightly past its expiry time.

### Production classes

| Type | Purpose |
| --- | --- |
| [`Iban`](src/main/java/com/example/iban/Iban.java) | A valid IBAN, created only through `parse` |
| [`IbanValidator`](src/main/java/com/example/iban/IbanValidator.java) | Checks an IBAN's length and checksum |
| [`IbanGenerator`](src/main/java/com/example/iban/IbanGenerator.java) | Generates valid Dutch IBANs. A seeded random generator makes the output repeatable |
| [`Amount`](src/main/java/com/example/payments/Amount.java) | A non-negative amount in one currency, parsed from text such as `EUR 12.50` |
| [`PaymentScheme`](src/main/java/com/example/payments/PaymentScheme.java) | An enum of the four schemes above |
| [`Transfer`](src/main/java/com/example/payments/Transfer.java) | A payment order with six fields, validated on creation |
| [`Claims`](src/main/java/com/example/tokens/Claims.java) | The claims of a token, any of which may be missing |
| [`ClaimProblem`](src/main/java/com/example/tokens/ClaimProblem.java) | An enum of the reasons claims can be rejected |
| [`TokenValidator`](src/main/java/com/example/tokens/TokenValidator.java) | Checks claims using an injected clock, so expiry can be tested without waiting |

### Test helpers and data

These files aren't test classes, so JUnit doesn't run them directly.

| File | Purpose |
| --- | --- |
| [`SampleIbans`](src/test/java/com/example/module_07/support/SampleIbans.java) | Well-known valid IBANs and broken ones |
| [`TokenScenarios`](src/test/java/com/example/module_07/support/TokenScenarios.java) | A fixed "now", a ready-made validator and reusable argument factories |
| [`RandomIbansProvider`](src/test/java/com/example/module_07/section_03/RandomIbansProvider.java) | A custom `ArgumentsProvider` that supplies ten generated IBANs, the same on every run |
| [`AmountConverter`](src/test/java/com/example/module_07/section_05/AmountConverter.java) | An untyped converter from text to `Amount` |
| [`HumanDurationConverter`](src/test/java/com/example/module_07/section_05/HumanDurationConverter.java) | A typed converter from text such as `90s` or `5m` to `Duration` |
| [`@HumanDuration`](src/test/java/com/example/module_07/section_05/HumanDuration.java) | A shorter form of `@ConvertWith(HumanDurationConverter.class)` |
| [`TransferAggregator`](src/test/java/com/example/module_07/section_06/TransferAggregator.java) | A reusable aggregator that turns a row of columns into a `Transfer` |
| [`@CsvToTransfer`](src/test/java/com/example/module_07/section_06/CsvToTransfer.java) | A shorter form of `@AggregateWith(TransferAggregator.class)` |
| [`ibans.csv`](src/test/resources/ibans.csv) | A table of countries, IBANs and whether each is valid |
| [`transfers.csv`](src/test/resources/transfers.csv) | A table of transfers and whether each crosses a border |

## Running the Module

Run these commands from the repository root.

| Goal | Command | Result |
| --- | --- | --- |
| Run the module | `mvn test -pl module-07` | 259 tests pass; the two failure demos are excluded |
| Run one class | `mvn test -pl module-07 -Dtest=RepeatedIbanGenerationTest` | 28 tests pass |
| Run a parameterized class | `mvn test -pl module-07 -Dtest=SepaSchemeTest` | 15 tests pass |
| Run a failure demo | `mvn test -pl module-07 -Dtest=ArgumentCountFailureDemoTest -DexcludedGroups=none` | `BUILD FAILURE`, as intended |

The number of tests is much higher than the number of test methods, because each repetition or set of arguments counts as a test. Maven's output for parameterized classes also lists each invocation separately, with some lines showing `Tests run: 0`. The overall total at the end is correct. Your IDE's result tree shows the structure and invocation names more clearly.

## Sections

Each section has its own package under [`src/test/java/com/example/module_07`](src/test/java/com/example/module_07).

### Section 1: Repeated Tests

**What it tests:** that generated IBANs are valid and different from each other, and how repeated tests are named and stopped. Start with `RepeatedIbanGenerationTest`.

| Class | What it tests |
| --- | --- |
| [`RepeatedIbanGenerationTest`](src/test/java/com/example/module_07/section_01/RepeatedIbanGenerationTest.java) | That generated IBANs are valid and unique, with the default, custom and long name patterns. This produces 28 tests |
| [`FlakyHuntDemoTest`](src/test/java/com/example/module_07/section_01/FlakyHuntDemoTest.java) | Makes a false claim about generated IBANs, and [fails on purpose](#deliberately-failing-tests) to show how `failureThreshold` stops the repetitions |

**JUnit concepts**

- **`@RepeatedTest`** runs a test a set number of times. It's a container, and each repetition is a test with the full lifecycle, so `@BeforeEach` runs before each one.
- **Name placeholders:** `{displayName}`, `{currentRepetition}` and `{totalRepetitions}` can be used in `name`. `RepeatedTest.LONG_DISPLAY_NAME` is a ready-made pattern.
- **`RepetitionInfo`** tells a test or `@BeforeEach` method which repetition is running, how many failures there have been and more. Module 6 introduced it.
- **`failureThreshold`** stops repeating once that many repetitions have failed, and reports the rest as skipped. When hunting a flaky test, a threshold of 1 is enough, because one failure proves it's flaky.
- **Repeatable randomness:** each repetition seeds its generator with its own repetition number, so a failing repetition fails the same way on every run.

### Section 2: Consuming Arguments

**What it tests:** IBAN validation with simple inputs, how a method's parameters must be ordered, and what happens to arguments after they're used. Start with `FirstParameterizedTest`.

| Class | What it tests |
| --- | --- |
| [`FirstParameterizedTest`](src/test/java/com/example/module_07/section_02/FirstParameterizedTest.java) | Valid and invalid IBANs, with one invocation per value, alongside an ordinary `@Test` in the same class |
| [`ParameterOrderTest`](src/test/java/com/example/module_07/section_02/ParameterOrderTest.java) | A method using all three groups of parameters, and that `@BeforeEach` runs before each invocation |
| [`AutoCloseArgumentsTest`](src/test/java/com/example/module_07/section_02/AutoCloseArgumentsTest.java) | That `AutoCloseable` arguments are closed after their invocation, and that a shared one stays open with `autoCloseArguments = false` |

**JUnit concepts**

- **`@ParameterizedTest`** needs a source of arguments and parameters to receive them. Each set of arguments becomes one invocation, reported separately. The class can also contain ordinary tests.
- **Parameter names in reports:** a name such as `iban = "NL91..."` needs parameter names in the compiled code. The course's [parent POM](../pom.xml) compiles with `-parameters` for this reason. Without it, you would see `arg0`.
- **Parameter order:** parameters must be declared in three groups, in this order:

  1. Indexed parameters, filled from the source by position
  2. Aggregators: `ArgumentsAccessor` or `@AggregateWith`, covered in Section 6
  3. Parameters supplied by other resolvers, such as `TestInfo` and `TestReporter`

- **Invocation lifecycle:** each invocation has the lifecycle of a normal test. Lifecycle methods don't receive the source's arguments, but other resolvers such as `TestInfo` still work there.
- **`autoCloseArguments`:** arguments that are `AutoCloseable` are closed after their invocation by default. Set this to `false` if the same object is used by several invocations.

### Section 3: Argument Sources

**What it tests:** IBAN validation, scheme rules and token claims, using each built-in way of supplying arguments.

| Class | What it tests |
| --- | --- |
| [`ValueSourceTest`](src/test/java/com/example/module_07/section_03/ValueSourceTest.java) | Different spellings of an IBAN, valid lengths, invalid characters and final classes, using literal values |
| [`NullAndEmptySourcesTest`](src/test/java/com/example/module_07/section_03/NullAndEmptySourcesTest.java) | That null, empty and blank IBANs are rejected, that a null subject is reported, and that an empty audience is reported |
| [`EnumSourceTest`](src/test/java/com/example/module_07/section_03/EnumSourceTest.java) | Rules for all payment schemes, or for those selected by name, exclusion, regular expression or range |
| [`MethodSourceTest`](src/test/java/com/example/module_07/section_03/MethodSourceTest.java) | Generated IBANs and token claims with their expected problems, using factory methods in the class and in another class |
| [`FieldSourceTest`](src/test/java/com/example/module_07/section_03/FieldSourceTest.java) | IBANs and scheme limits from arrays, lists, a `Supplier` and fields in another class |
| [`CsvSourceTest`](src/test/java/com/example/module_07/section_03/CsvSourceTest.java) | IBANs and scheme limits from inline tables, and the rules for delimiters, quotes, nulls, empty values and whitespace |
| [`CsvFileSourceTest`](src/test/java/com/example/module_07/section_03/CsvFileSourceTest.java) | Whether each IBAN in `ibans.csv` is valid, reading the file from the classpath and from the file system |
| [`ArgumentsSourceTest`](src/test/java/com/example/module_07/section_03/ArgumentsSourceTest.java) | IBANs from the custom `RandomIbansProvider`, and combining or repeating sources on one test |

**JUnit concepts**

- **`@ValueSource`** supplies an array of literal values, for a method that takes one argument.
- **`@NullSource`, `@EmptySource` and `@NullAndEmptySource`** add a `null`, an empty value or both. Bad input is a common place to find bugs, and these can be combined with `@ValueSource` to cover blank strings too.
- **`@EnumSource`** supplies enum constants. `names`, `mode`, `from` and `to` narrow the selection, and the enum type can be left out when the parameter already declares it.
- **`@MethodSource`** calls a factory method, so any Java code can build the arguments, including objects that can't be written as literals. With no name, it looks for a method named after the test. A factory in the test class must be `static` unless the class uses `PER_CLASS`, and one in another class is written as `fully.qualified.Class#method`.
- **`@FieldSource`** reads arguments from a field, which is simpler when the data is fixed. A field can't hold a plain `Stream` because it can only be read once, so wrap it in a `Supplier`.
- **`@CsvSource`** supplies a small table inline, as strings or a text block. An unquoted empty value is `null`, and `''` is an empty string. `delimiter`, `quoteCharacter`, `nullValues`, `emptyValue` and `ignoreLeadingAndTrailingWhitespace` change these rules.
- **`@CsvFileSource`** reads the table from a file, which people who don't read Java can maintain. `resources` reads from the classpath and `files` reads from the file system. `numLinesToSkip` skips a header line, and `useHeadersInDisplayName` uses it in the invocation names. Its quote character is `"`, and lines starting with `#` are comments.
- **`Arguments`** holds several values for one invocation.
- **`@ArgumentsSource` with `ArgumentsProvider`** supplies arguments from your own reusable class.
- **Combining sources:** different kinds of source can be used on one test, and most can be repeated.

### Section 4: Argument Count Validation

**What it tests:** what happens when a source supplies more arguments than a method declares, or none at all.

| Class | What it tests |
| --- | --- |
| [`ArgumentCountValidationTest`](src/test/java/com/example/module_07/section_04/ArgumentCountValidationTest.java) | That an extra column is ignored by default, that `STRICT` accepts matching columns, and that `allowZeroInvocations` accepts an empty source |
| [`ArgumentCountFailureDemoTest`](src/test/java/com/example/module_07/section_04/ArgumentCountFailureDemoTest.java) | Nothing passes: all three tests [fail on purpose](#deliberately-failing-tests) with the errors the class above avoids |

**JUnit concepts**

- **Extra arguments** are ignored by default, which can hide mistakes such as a column you forgot to check.
- **`argumentCountValidation = STRICT`** makes extra arguments an error for one test. The `junit.jupiter.params.argumentCountValidation` setting does this for a whole run, and its values are `none` and `strict`.
- **Too few arguments** are always an error, unless another resolver can supply the missing parameter.
- **`allowZeroInvocations = true`** accepts a source that supplies nothing. Otherwise this fails the test, because it usually means the data is missing.

**Try it:** add the strict setting for the whole module:

```bash
mvn test -pl module-07 -Djunit.jupiter.params.argumentCountValidation=strict
```

One of the 259 tests now fails: `lenientByDefault`, which relies on the extra column being ignored. An aggregator or `ArgumentsAccessor` reads all the columns, so the tests that use them still pass.

### Section 5: Argument Conversion

**What it tests:** that text from a source becomes the parameter's type, in each of the three ways JUnit can do this.

| Class | What it tests |
| --- | --- |
| [`ImplicitConversionTest`](src/test/java/com/example/module_07/section_05/ImplicitConversionTest.java) | Widening numbers, and text becoming enums, dates, durations, currencies, locales, UUIDs, classes and more |
| [`FallbackConversionTest`](src/test/java/com/example/module_07/section_05/FallbackConversionTest.java) | Text becoming an `Iban` through its single `parse` factory method |
| [`ExplicitConversionTest`](src/test/java/com/example/module_07/section_05/ExplicitConversionTest.java) | Clock skew from text such as `90s`, amounts through `AmountConverter`, and dates in a custom format |

**JUnit concepts**

- **Implicit conversion** covers common types. An `int` can widen to a `long` or `double`, and text can become a primitive, enum, `java.time` value, `BigDecimal`, `Currency`, `Locale`, `UUID`, `Class` and others.
- **Fallback conversion** covers your own types. JUnit looks in the target type for exactly one non-private static factory method that takes a `String` and returns the type, or else exactly one such constructor. If it finds several factory methods, it ignores them all. So `Iban`, with one, converts automatically, but `Amount`, with `parse` and `euro`, needs an explicit converter.
- **`@ConvertWith`** attaches a converter to one parameter, and it takes priority over implicit and fallback conversion.
- **`TypedArgumentConverter`** checks the source and target types for you. **`SimpleArgumentConverter`** leaves those checks to you. Throw `ArgumentConversionException` for bad input.
- **`@JavaTimeConversionPattern`** is a built-in converter for dates in another format, such as `dd-MM-yyyy`.
- **Composed annotations**, such as `@HumanDuration`, give a converter a shorter name.

### Section 6: Argument Aggregation

**What it tests:** that each row of `transfers.csv` becomes a valid `Transfer`, and that invalid rows are rejected.

| Class | What it tests |
| --- | --- |
| [`ArgumentsAccessorTest`](src/test/java/com/example/module_07/section_06/ArgumentsAccessorTest.java) | Valid rows from the file and invalid inline rows, reading the columns by index |
| [`AggregatorTest`](src/test/java/com/example/module_07/section_06/AggregatorTest.java) | The same file through `TransferAggregator`, with `@AggregateWith` and with the composed `@CsvToTransfer` |

**JUnit concepts**

- **`ArgumentsAccessor`** puts all the arguments in one parameter, which is easier than declaring seven. `get(index, Type.class)` applies the same conversion as parameters, and `getString`, `getBoolean` and others are shortcuts.
- **`@AggregateWith`** turns the arguments into one object using a reusable class, so the mapping is written once.
- **`SimpleArgumentsAggregator`** is a convenient base class for that. An aggregator must be top-level or a `static` nested class, with a no-argument constructor.
- **Reading all columns:** an aggregator sees all the columns, so `withComposedAnnotation` can also read the seventh column through an `ArgumentsAccessor`.
- **Composed annotations**, such as `@CsvToTransfer`, give an aggregator a shorter name.

### Section 7: Invocation Display Names

**What it tests:** the name of each invocation. Every test asserts its own display name, so this class documents the placeholders and proves them.

| Class | What it tests |
| --- | --- |
| [`InvocationDisplayNamesTest`](src/test/java/com/example/module_07/section_07/InvocationDisplayNamesTest.java) | Placeholders, quoted text arguments, apostrophes, `Named` arguments and argument sets |

**JUnit concepts**

- **Default pattern:** `[{index}] {argumentSetNameOrArgumentsWithNames}`. The `junit.jupiter.params.displayname.default` setting changes it for a run.
- **Placeholders:** `{index}`, `{0}`, `{1}`, `{arguments}`, `{argumentsWithNames}`, `{argumentSetName}`, `{argumentSetNameOrArgumentsWithNames}` and `{displayName}`.
- **Quoted text:** text arguments are shown in quotes, such as `"NL91..."` and `'!'`. `quoteTextArguments = false` turns this off. Values from `@CsvSource` start as text, so a number from a CSV row is quoted too.
- **Apostrophes:** a pattern is a `MessageFormat`, so write `''` for one apostrophe.
- **`Named.of`** shows a readable name in the report while passing a different value to the test.
- **`argumentSet`** gives a whole set of arguments one name.

### Section 8: Parameterized Classes

**What it tests:** the rules of each SEPA scheme, token validation under several configurations, and the transfer rules, with every test in a class running once per set of arguments.

| Class | What it tests |
| --- | --- |
| [`SepaSchemeTest`](src/test/java/com/example/module_07/section_08/SepaSchemeTest.java) | Five tests for each of the three SEPA schemes, including a `@Nested` class, with the scheme injected into a field |
| [`ConstructorInjectionTest`](src/test/java/com/example/module_07/section_08/ConstructorInjectionTest.java) | Cross-border rules and paying yourself, with `Iban` values passed to the constructor |
| [`TokenValidatorSuiteTest`](src/test/java/com/example/module_07/section_08/TokenValidatorSuiteTest.java) | The same token rules for three issuer, audience and skew settings, written as a `record` |
| [`ParameterizedClassLifecycleTest`](src/test/java/com/example/module_07/section_08/ParameterizedClassLifecycleTest.java) | The exact order of lifecycle methods across invocations, recorded in a log and checked in `@AfterAll` |

The lifecycle for two invocations, each with two tests, is:

```text
@BeforeAll                                 once
  @BeforeParameterizedClassInvocation      first set of arguments
    @BeforeEach -> test a
    @BeforeEach -> test b
  @AfterParameterizedClassInvocation
  @BeforeParameterizedClassInvocation      second set of arguments
    @BeforeEach -> test a
    @BeforeEach -> test b
  @AfterParameterizedClassInvocation
@AfterAll                                  once
```

**JUnit concepts**

- **`@ParameterizedClass`** runs a whole class once per set of arguments, including its `@Nested` classes. It uses the same argument sources as `@ParameterizedTest`.
- **Field injection:** a `@Parameter` field receives the argument. With several arguments, give each field its index, such as `@Parameter(1)`.
- **Constructor injection:** without `@Parameter` fields, the arguments go to the constructor. Other parameters, such as `TestInfo`, come after them.
- **Records:** a record's components are its constructor parameters, so a `record` test class gets constructor injection without writing a constructor.
- **`@BeforeParameterizedClassInvocation` and `@AfterParameterizedClassInvocation`** run once per invocation, and are `static` unless the class uses `PER_CLASS`. They can receive the class's arguments, unless `injectArguments = false`.
- **`@BeforeEach`** runs before every test of every invocation, but can't receive the arguments. Use a field or the constructor instead.

## Deliberately Failing Tests

In this module, two classes fail on purpose: [`FlakyHuntDemoTest`](src/test/java/com/example/module_07/section_01/FlakyHuntDemoTest.java) and [`ArgumentCountFailureDemoTest`](src/test/java/com/example/module_07/section_04/ArgumentCountFailureDemoTest.java). Other modules have similar classes, listed in the [root README](../README.md#deliberately-failing-tests).

### Why the failures are kept

The point of these classes is to show you real failure output: how `failureThreshold` behaves when repetitions fail, and the errors caused by a mismatch between a source and a method. A class can't show a failure without failing, so its tests can't simply be corrected.

Left in the normal build, though, they would make every `mvn test` end in `BUILD FAILURE`, and a real failure would be hard to spot. `@Disabled` wouldn't help, because the tests would never run and there would be no output to read. A tag solves both problems: the tests stay out of the normal build, but can still be run when you ask for them.

### How they are excluded

Two pieces work together:

1. Each class is labelled with a tag:

   ```java
   @Tag("expected-failure-tests")
   class ArgumentCountFailureDemoTest {
   ```

2. The [parent POM](../pom.xml) sets a property that Maven Surefire, the plugin that runs the tests, reads as its list of tags to exclude:

   ```xml
   <excludedGroups>expected-failure-tests</excludedGroups>
   ```

Because the tag is on the class, all of its tests are excluded.

### How to override the exclusion

A value passed on the command line takes priority over the property in the POM. That makes `-DexcludedGroups=none` the usual override. `none` isn't a special keyword: it is just a tag that no test uses, so nothing is excluded.

| Goal | Command |
| --- | --- |
| Run the flaky hunt demo | `mvn test -pl module-07 -Dtest=FlakyHuntDemoTest -DexcludedGroups=none` |
| Run the argument count demo | `mvn test -pl module-07 -Dtest=ArgumentCountFailureDemoTest -DexcludedGroups=none` |
| Run the whole module, including both demos | `mvn test -pl module-07 -DexcludedGroups=none` |
| Run only the tests with this tag | `mvn test -pl module-07 -Dgroups=expected-failure-tests -DexcludedGroups=none` |
| Run a demo without failing the build | `mvn test -pl module-07 -Dtest=ArgumentCountFailureDemoTest -DexcludedGroups=none -Dmaven.test.failure.ignore=true` |

The exclusion has to be cleared even when you select the class or tag by name. These two commands report `BUILD SUCCESS` with `Tests run: 0`, because the exclusion still applies:

```bash
mvn test -pl module-07 -Dtest=ArgumentCountFailureDemoTest
mvn test -pl module-07 -Dgroups=expected-failure-tests
```

The exclusion is part of the Maven build, so running the class directly from your IDE normally ignores it, unless the IDE is set to run tests through Maven.

Keep the override on the command line. Removing the tags or the POM property would make every normal build fail.

### What to expect

Each demo ends in `BUILD FAILURE`, which is the intended result.

`FlakyHuntDemoTest` claims that a generated IBAN never contains the digit 7, and uses `failureThreshold = 2`. Each repetition seeds its generator with its own number, so every run gives the same result:

```text
Tests run: 50, Failures: 2, Errors: 0, Skipped: 47
```

| Repetition | IBAN | Result |
| --- | --- | --- |
| 1 | `NL45ABNA9975555895` | Fails with `NL45ABNA9975555895 contains a 7` |
| 2 | `NL57TRIO8309410504` | Passes, as this IBAN has no 7 in its account number |
| 3 | `NL82BUNQ1149717570` | Fails with `NL82BUNQ1149717570 contains a 7`, which reaches the threshold |
| 4 to 50 | Not generated | Skipped with `Failure threshold [2] exceeded` |

`ArgumentCountFailureDemoTest` ends with this summary:

```text
Tests run: 3, Failures: 0, Errors: 3, Skipped: 0
```

| Test | What it shows | Failure message |
| --- | --- | --- |
| `strictWithAnExtraColumn` | `STRICT` rejecting a row with an extra column | `Configuration error: @ParameterizedTest consumes 2 parameters but there were 3 arguments provided.` |
| `tooFewArguments` | A row with too few columns, and nothing else to supply the missing parameter | `No ParameterResolver registered for parameter [java.lang.String country]...` |
| `zeroInvocations` | A source that supplies no arguments | `Configuration error: You must configure at least one set of arguments for this @ParameterizedTest` |

Maven counts these as errors rather than failures, because the tests never reach an assertion: the problem is in how they're set up.
