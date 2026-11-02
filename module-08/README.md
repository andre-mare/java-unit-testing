# Module 8: Templates and Dynamic Tests

This module covers how to run tests whose number or context isn't fixed in the code: how a test template runs a method several times, how a class template runs a whole class several times, and how a test factory builds tests while the suite runs.

## Table of Contents

- [Module 8: Templates and Dynamic Tests](#module-8-templates-and-dynamic-tests)
  - [Table of Contents](#table-of-contents)
  - [The Domain: Currencies, Consents, Primes and Scopes](#the-domain-currencies-consents-primes-and-scopes)
    - [Production classes](#production-classes)
    - [Test helpers and data](#test-helpers-and-data)
  - [Running the Module](#running-the-module)
  - [Sections](#sections)
    - [Section 1: Test Templates](#section-1-test-templates)
    - [Section 2: Class Templates](#section-2-class-templates)
    - [Section 3: Dynamic Tests](#section-3-dynamic-tests)
  - [Deliberately Failing Tests](#deliberately-failing-tests)
    - [Why the failures are kept](#why-the-failures-are-kept)
    - [How they are excluded](#how-they-are-excluded)
    - [How to override the exclusion](#how-to-override-the-exclusion)
    - [What to expect](#what-to-expect)

## The Domain: Currencies, Consents, Primes and Scopes

This module has four small domains, each suited to a different technique.

**Currency conversion**, for test templates. [`CurrencyConverter`](src/main/java/com/example/fx/CurrencyConverter.java) converts between EUR, USD, GBP, CHF and JPY using fixed, made-up rates. Results are rounded half-even to the target currency's decimals: two for most currencies, and none for yen. The same rules must hold for every currency, so one test method can run once per currency.

**Account-access consents**, for class templates. [`ConsentService`](src/main/java/com/example/consent/ConsentService.java) manages a person's permission for a third party to access their accounts, with rules simplified from PSD2, the EU payment services directive. It works with two repositories that must behave the same, so one test class can run against each.

| Action | Rule | If the rule is broken |
| --- | --- | --- |
| `request` | Validity must be more than 0 and at most 180 days | Throws `IllegalArgumentException` |
| `authorise` | The consent must be awaiting authorisation and not expired | Throws `IllegalStateException` |
| `reject` | The consent must be awaiting authorisation | Throws `IllegalStateException` |
| `revoke` | The consent must be authorised. Revoking twice changes nothing | Throws `IllegalStateException` |
| `allows` | The consent must be authorised and not expired, and include the permission | Returns `false` |

**Prime numbers**, for generated tests. [`Primes`](src/main/java/com/example/primes/Primes.java) has two independent algorithms, trial division and a sieve, so tests can compare them. It can also list a number's prime factors.

**OAuth 2.0 scopes**, for tests read from a file. [`ScopeSet`](src/main/java/com/example/scope/ScopeSet.java) strictly parses a scope string: tokens are case-sensitive and separated by exactly one space, and only certain characters are allowed.

### Production classes

| Type | Purpose |
| --- | --- |
| [`CurrencyConverter`](src/main/java/com/example/fx/CurrencyConverter.java) | Converts and rounds amounts between currencies |
| [`ConsentService`](src/main/java/com/example/consent/ConsentService.java) | Applies the consent rules above |
| [`Consent`](src/main/java/com/example/consent/Consent.java) | An immutable consent. A status change returns a new copy |
| [`ConsentStatus`](src/main/java/com/example/consent/ConsentStatus.java) | `AWAITING_AUTHORISATION`, `AUTHORISED`, `REVOKED` or `REJECTED` |
| [`ConsentRepository`](src/main/java/com/example/consent/ConsentRepository.java) | The interface for storing consents |
| [`InMemoryConsentRepository`](src/main/java/com/example/consent/InMemoryConsentRepository.java) | Stores consents in a map |
| [`FileConsentRepository`](src/main/java/com/example/consent/FileConsentRepository.java) | Stores one consent per line in a text file |
| [`Primes`](src/main/java/com/example/primes/Primes.java) | Checks, lists and factorizes prime numbers |
| [`ScopeSet`](src/main/java/com/example/scope/ScopeSet.java) | A parsed set of OAuth 2.0 scope tokens |

### Test helpers and data

These files aren't test classes, so JUnit doesn't run them directly.

| File | Purpose |
| --- | --- |
| [`ConsentFixtures`](src/test/java/com/example/module_08/support/ConsentFixtures.java) | Shared consent test data, including a start time and predictable IDs |
| [`MutableClock`](src/test/java/com/example/module_08/support/MutableClock.java) | A clock that stands still until a test moves it, so tests can pass an expiry date without waiting |
| [`@ForEachCurrency`](src/test/java/com/example/module_08/section_01/ForEachCurrency.java) | A custom annotation that runs a test once per currency |
| [`CurrencyInvocationContextProvider`](src/test/java/com/example/module_08/section_01/CurrencyInvocationContextProvider.java) | The extension behind `@ForEachCurrency`, which supplies one invocation per currency |
| [`ConsentRepositoryProvider`](src/test/java/com/example/module_08/section_02/ConsentRepositoryProvider.java) | The extension that runs a test class once per repository, and deletes the temporary files afterwards |
| [`RepositoryAware`](src/test/java/com/example/module_08/section_02/RepositoryAware.java) | An interface for test classes that receive a repository from that provider |
| [`RepositoryFixture`](src/test/java/com/example/module_08/section_02/RepositoryFixture.java) | What each test receives: a name, an empty repository and a way to reopen it |
| [`scope-vectors.txt`](src/test/resources/scope-vectors.txt) | A list of valid and invalid scope strings |

## Running the Module

Run these commands from the repository root.

| Goal | Command | Result |
| --- | --- | --- |
| Run the module | `mvn test -pl module-08` | 221 tests pass; the two failure demos are excluded |
| Run a test template class | `mvn test -pl module-08 -Dtest=CurrencyConverterTemplateTest` | 21 tests pass |
| Run a class template | `mvn test -pl module-08 -Dtest=ConsentServiceTest` | 22 tests pass |
| Run a test factory class | `mvn test -pl module-08 -Dtest=PrimeContainersTest` | 101 tests pass |
| Run a failure demo | `mvn test -pl module-08 -Dtest=DynamicFailureDemoTest -DexcludedGroups=none` | `BUILD FAILURE`, as intended |

The number of tests is much higher than the number of test methods, because each invocation or dynamic test counts as a test. Maven's output can be confusing here. It lists class template runs separately, with some lines showing `Tests run: 0`, and it lists `ScopeVectorsTest`'s dynamic tests under `com.example.scope.ScopeSet`, because of the test sources they declare. The overall total at the end is correct. Your IDE's result tree shows the structure and names more clearly.

## Sections

Each section has its own package under [`src/test/java/com/example/module_08`](src/test/java/com/example/module_08).

Sections 1 and 3 differ in one important way, so keep this comparison in mind:

| | Test template invocation | Dynamic test |
| --- | --- | --- |
| Defined by | A method, run once per invocation context | Code in a `@TestFactory` that runs during the test run |
| `@BeforeEach` and `@AfterEach` | Run around each invocation | Run once around the whole factory |
| Test instance | A new one per invocation, by default | One shared by all the dynamic tests from a factory |

### Section 1: Test Templates

**What it tests:** the rules of `CurrencyConverter`, checked once per currency, and how a template gets its invocations. Start with `FirstTestTemplateTest`.

| Class | What it tests |
| --- | --- |
| [`FirstTestTemplateTest`](src/test/java/com/example/module_08/section_01/FirstTestTemplateTest.java) | A plain `@TestTemplate` that runs once per default currency, with its names and `@BeforeEach` count checked, and a template allowed to have no invocations |
| [`CurrencyConverterTemplateTest`](src/test/java/com/example/module_08/section_01/CurrencyConverterTemplateTest.java) | Every conversion rule with `@ForEachCurrency`: same currency, zero, decimals, round trips, negative amounts and unsupported currencies. This produces 21 tests |
| [`BuiltInTemplatesTest`](src/test/java/com/example/module_08/section_01/BuiltInTemplatesTest.java) | That `@RepeatedTest` and `@ParameterizedTest` are themselves test templates |
| [`TemplateFailureDemoTest`](src/test/java/com/example/module_08/section_01/TemplateFailureDemoTest.java) | Nothing passes: both tests [fail on purpose](#deliberately-failing-tests), in the two ways a template can fail before it runs |

**JUnit concepts**

- **`@TestTemplate`** marks a method as a template rather than a test. It does nothing by itself: a registered provider must supply its invocations.
- **`TestTemplateInvocationContextProvider`** is the extension that supplies them. `supportsTestTemplate` says whether it handles a template, and `provideTestTemplateInvocationContexts` returns one context per invocation.
- **`TestTemplateInvocationContext`** describes one invocation. `getDisplayName` names it, and `getAdditionalExtensions` adds extensions for that invocation only, such as a parameter resolver that supplies the currency.
- **`mayReturnZeroTestTemplateInvocationContexts`** decides whether a provider can return no invocations. By default, that's an error.
- **Invocation lifecycle:** each invocation is a full test with the normal lifecycle, so `@BeforeEach` runs before each one.
- **Custom template annotations:** `@ForEachCurrency` combines `@TestTemplate` and `@ExtendWith`. `@RepeatedTest` and `@ParameterizedTest` are built on `@TestTemplate` in the same way.

### Section 2: Class Templates

**What it tests:** that the consent rules hold with either repository, and how a class template behaves. Start with `FirstClassTemplateTest`.

| Class | What it tests |
| --- | --- |
| [`FirstClassTemplateTest`](src/test/java/com/example/module_08/section_02/FirstClassTemplateTest.java) | A small class template that runs for two scopes, with its lifecycle recorded and checked, and that `@ParameterizedClass` is a class template |
| [`ConsentServiceTest`](src/test/java/com/example/module_08/section_02/ConsentServiceTest.java) | The full consent suite, including a `@Nested` class, run as `[1] in-memory` and `[2] file-backed`. This produces 22 tests |

**JUnit concepts**

- **`@ClassTemplate`** runs a whole test class once per invocation context, including its `@Nested` classes. A registered provider must supply the invocations.
- **`ClassTemplateInvocationContextProvider`** is the class-level version of the test template provider. `supportsClassTemplate` chooses the classes it handles, and `provideClassTemplateInvocationContexts` returns one context per run.
- **`ClassTemplateInvocationContext`** names a run and adds extensions for that run only.
- **`TestInstancePostProcessor`** runs for each new test instance, including `@Nested` instances. It's used here to give each test a new, empty repository.
- **Lifecycle:** `@BeforeAll` and `@AfterAll` run once around all the invocations.
- **`ExtensionContext.Store` for cleanup:** an `AutoCloseable` value in the store is closed when its context ends. `ConsentRepositoryProvider` uses this to delete its temporary directories.
- **`@ParameterizedClass`** from Module 7 is the built-in class template.

**Try it:** a shared suite shows which implementation is broken. In `FileConsentRepository.parse`, replace `ConsentStatus.valueOf(f[5])` with `ConsentStatus.AWAITING_AUTHORISATION` and run `ConsentServiceTest`. Six of the 22 tests fail, all in the `file-backed` run, while every `in-memory` test still passes. Restore the line afterwards.

### Section 3: Dynamic Tests

**What it tests:** prime numbers and OAuth scopes, using tests built in code or from a data file, and what dynamic tests do and don't get from JUnit. Start with `FirstDynamicTestsTest`.

| Class | What it tests |
| --- | --- |
| [`FirstDynamicTestsTest`](src/test/java/com/example/module_08/section_03/FirstDynamicTestsTest.java) | Prime checks and factors, with one factory for each return type: a single test, a collection, an iterator, an array, a stream and a container |
| [`PrimeContainersTest`](src/test/java/com/example/module_08/section_03/PrimeContainersTest.java) | One test per number from 1 to 100, in `primes (25)` and `non-primes (75)` containers, checked against a fixed list, plus an ordinary `@Test` for the sieve |
| [`DynamicTestStreamTest`](src/test/java/com/example/module_08/section_03/DynamicTestStreamTest.java) | Factors and scopes using the `DynamicTest.stream` forms: an iterator, a stream, named inputs and named executables |
| [`DynamicLifecycleTest`](src/test/java/com/example/module_08/section_03/DynamicLifecycleTest.java) | That `@BeforeEach` runs once for a factory, so its five dynamic tests share one repository |
| [`ScopeVectorsTest`](src/test/java/com/example/module_08/section_03/ScopeVectorsTest.java) | Valid and invalid scopes from `scope-vectors.txt`, with each test pointing back to its line in the file |
| [`DynamicExecutionModeTest`](src/test/java/com/example/module_08/section_03/DynamicExecutionModeTest.java) | Prime counts up to three limits, built with the configuration callback that sets each node's execution mode |
| [`DynamicFailureDemoTest`](src/test/java/com/example/module_08/section_03/DynamicFailureDemoTest.java) | Dynamic tests from one factory that pass, abort and [fail on purpose](#deliberately-failing-tests) independently |

**JUnit concepts**

- **`@TestFactory`** marks a method that builds tests while the suite runs. The factory isn't a test itself: JUnit runs what it returns. It must not be `private` or `static`.
- **Return types:** a factory returns a `DynamicNode`, or a `Stream`, `Collection`, `Iterable`, `Iterator` or array of them. A stream is the usual choice.
- **`DynamicTest`** is a display name and a body. **`DynamicContainer`** is a display name and child nodes, so you can build a tree of tests.
- **`DynamicTest.stream`** creates tests from input data. You can supply the inputs with a name function and a body, `Named` inputs that carry their own names, or `NamedExecutable` values that are each their own test.
- **Lifecycle caveat:** dynamic tests have no lifecycle callbacks of their own. `@BeforeEach` and `@AfterEach` run once around the whole factory, so state set up there is shared. If each test needs fresh state, create it in the test body.
- **Test sources:** a dynamic test or container can declare a `URI` for its origin, which IDEs and reports can use to take you there. Schemes include `classpath:` with `?line=`, `method:`, `class:` and `file:`.
- **Data files:** building tests from a file means you can add a case without changing Java. Adding a line such as `VALID [read write] 2` to `scope-vectors.txt` adds a sixteenth test.
- **Configuration callback:** `dynamicTest(config -> ...)` and `dynamicContainer(config -> ...)` set options by name instead of by position.
- **Execution modes:** `executionMode` sets a node's own mode, and a container's `childExecutionMode` sets the mode of children that don't set their own. These only matter when parallel execution is enabled, which Module 9 covers.
- **Independent results:** each dynamic test passes, aborts or fails separately.

## Deliberately Failing Tests

In this module, two classes fail on purpose: [`TemplateFailureDemoTest`](src/test/java/com/example/module_08/section_01/TemplateFailureDemoTest.java) and [`DynamicFailureDemoTest`](src/test/java/com/example/module_08/section_03/DynamicFailureDemoTest.java). Other modules have similar classes, listed in the [root README](../README.md#deliberately-failing-tests).

### Why the failures are kept

The point of these classes is to show you real failure output: the errors from a misconfigured template, and how dynamic tests report separate results. A class can't show a failure without failing, so its tests can't simply be corrected.

Left in the normal build, though, they would make every `mvn test` end in `BUILD FAILURE`, and a real failure would be hard to spot. `@Disabled` wouldn't help, because the tests would never run and there would be no output to read. A tag solves both problems: the tests stay out of the normal build, but can still be run when you ask for them.

### How they are excluded

Two pieces work together:

1. Each class is labelled with a tag:

   ```java
   @Tag("expected-failure-tests")
   class TemplateFailureDemoTest {
   ```

2. The [parent POM](../pom.xml) sets a property that Maven Surefire, the plugin that runs the tests, reads as its list of tags to exclude:

   ```xml
   <excludedGroups>expected-failure-tests</excludedGroups>
   ```

Because the tag is on the class, all of its tests are excluded, including the dynamic test that would pass.

### How to override the exclusion

A value passed on the command line takes priority over the property in the POM. That makes `-DexcludedGroups=none` the usual override. `none` isn't a special keyword: it is just a tag that no test uses, so nothing is excluded.

| Goal | Command |
| --- | --- |
| Run the template failure demo | `mvn test -pl module-08 -Dtest=TemplateFailureDemoTest -DexcludedGroups=none` |
| Run the dynamic failure demo | `mvn test -pl module-08 -Dtest=DynamicFailureDemoTest -DexcludedGroups=none` |
| Run the whole module, including both demos | `mvn test -pl module-08 -DexcludedGroups=none` |
| Run only the tests with this tag | `mvn test -pl module-08 -Dgroups=expected-failure-tests -DexcludedGroups=none` |
| Run a demo without failing the build | `mvn test -pl module-08 -Dtest=DynamicFailureDemoTest -DexcludedGroups=none -Dmaven.test.failure.ignore=true` |

The exclusion has to be cleared even when you select the class or tag by name. These two commands report `BUILD SUCCESS` with `Tests run: 0`, because the exclusion still applies:

```bash
mvn test -pl module-08 -Dtest=DynamicFailureDemoTest
mvn test -pl module-08 -Dgroups=expected-failure-tests
```

The exclusion is part of the Maven build, so running the class directly from your IDE normally ignores it, unless the IDE is set to run tests through Maven.

Keep the override on the command line. Removing the tags or the POM property would make every normal build fail.

### What to expect

Each demo ends in `BUILD FAILURE`, which is the intended result.

`TemplateFailureDemoTest` ends with this summary:

```text
Tests run: 2, Failures: 0, Errors: 2, Skipped: 0
```

| Test | What it shows | Failure message |
| --- | --- | --- |
| `noProvider` | A `@TestTemplate` with no registered provider | `You must register at least one TestTemplateInvocationContextProvider that supports @TestTemplate method...` |
| `zeroInvocations` | A provider that returns no invocations without allowing it | `Provider [CurrencyInvocationContextProvider] did not provide any invocation contexts, but was expected to do so...` |

`DynamicFailureDemoTest` ends with this summary:

```text
Tests run: 3, Failures: 0, Errors: 1, Skipped: 1
```

| Dynamic test | Outcome | Why |
| --- | --- | --- |
| `passes` | Passes | The factors of 10 are 2 and 5 |
| `aborts` | Skipped | `assumeTrue(false, ...)` aborts it, and Maven counts this as skipped |
| `fails` | Error | `Only numbers from 2 have prime factors: 1`, an uncaught `IllegalArgumentException` |

All three come from one factory, and one failing doesn't stop the others. The factory itself is a container, so it isn't counted as a test. Maven lists these tests by position, as `mixedResults()[1]` and so on, rather than by display name, and shows the failing one as `lambda$mixedResults$2`. Your IDE shows the names you gave them.
