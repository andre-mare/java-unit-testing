# Module 5: Controlling What Runs, in What Order, and for How Long

This module covers how to control a test run: how to run tests only in the right environment, how to select tests with tags, how to set the order they run in, and how to stop a test that takes too long.

## Table of Contents

- [Module 5: Controlling What Runs, in What Order, and for How Long](#module-5-controlling-what-runs-in-what-order-and-for-how-long)
  - [Table of Contents](#table-of-contents)
  - [The Domain: Configuration, Onboarding and Rate Limiting](#the-domain-configuration-onboarding-and-rate-limiting)
    - [Production classes](#production-classes)
    - [Test helpers](#test-helpers)
  - [Running the Module](#running-the-module)
    - [Maven profiles](#maven-profiles)
  - [Sections](#sections)
    - [Section 1: Conditional Execution](#section-1-conditional-execution)
    - [Section 2: Tags and Filtering](#section-2-tags-and-filtering)
    - [Section 3: Execution Order](#section-3-execution-order)
    - [Section 4: Timeouts](#section-4-timeouts)
  - [Deliberately Failing Tests](#deliberately-failing-tests)
    - [Why the failures are kept](#why-the-failures-are-kept)
    - [How they are excluded](#how-they-are-excluded)
    - [How to override the exclusion](#how-to-override-the-exclusion)
    - [What to expect](#what-to-expect)

## The Domain: Configuration, Onboarding and Rate Limiting

This module has three small domains, each suited to a different part of the module.

[`AppConfig`](src/main/java/com/example/config/AppConfig.java) reads application settings from the environment, so its behaviour depends on where it runs. That makes it a natural fit for conditional tests. A key such as `region` is looked up first as the system property `app.region`, and then as the environment variable `APP_REGION`. Its two lookups can also be replaced, so a test can supply fixed values.

[`Onboarding`](src/main/java/com/example/onboarding/Onboarding.java) is a sign-up flow whose steps must happen in order, which suits ordered scenario tests. Calling a step out of order throws `IllegalStateException`:

```text
STARTED -> register -> REGISTERED -> sendVerificationCode -> CODE_SENT -> verify -> VERIFIED -> activate -> ACTIVE
```

[`TokenBucket`](src/main/java/com/example/ratelimit/TokenBucket.java) is a rate limiter that holds a limited number of tokens. Each call takes a token, and tokens come back at a fixed rate. Some of its methods wait for a token, which makes them good subjects for timeout tests.

| Method | Behaviour |
| --- | --- |
| `tryAcquire()` | Takes a token if one is available, and returns `false` straight away if not |
| `acquire()` | Waits until a token is available, then takes it |
| `tryAcquire(Duration)` | Waits up to the given time, and returns `false` if no token became available |
| `availableTokens()` | Returns the number of tokens available now |

### Production classes

| Class | Purpose |
| --- | --- |
| [`AppConfig`](src/main/java/com/example/config/AppConfig.java) | Reads settings from system properties and environment variables |
| [`Onboarding`](src/main/java/com/example/onboarding/Onboarding.java) | A sign-up flow with steps that must happen in order |
| [`TokenBucket`](src/main/java/com/example/ratelimit/TokenBucket.java) | A rate limiter whose clock can be replaced in tests |

### Test helpers

These types are in the [`support`](src/test/java/com/example/module_05/support) package. They aren't test classes, so JUnit doesn't run them directly.

| Type | Purpose |
| --- | --- |
| [`@Integration`](src/test/java/com/example/module_05/support/Integration.java) | Combines `@Test`, `@Tag("integration")` and a condition that needs `APP_ENV=integration` |
| [`@UnixOnly`](src/test/java/com/example/module_05/support/UnixOnly.java) | Combines `@Test` and a condition that needs Linux or macOS |
| [`@Smoke`](src/test/java/com/example/module_05/support/Smoke.java) | A shorter form of `@Tag("smoke")` |
| [`@Security`](src/test/java/com/example/module_05/support/Security.java) | A shorter form of `@Tag("security")` |
| [`@LongRunning`](src/test/java/com/example/module_05/support/LongRunning.java) | Combines `@Test`, `@Tag("slow")` and a five-second `@Timeout` |
| [`Environments`](src/test/java/com/example/module_05/support/Environments.java) | Holds condition methods that test classes refer to by name |
| [`FakeNanoClock`](src/test/java/com/example/module_05/support/FakeNanoClock.java) | A clock that only moves when a test tells it to |

## Running the Module

Run these commands from the repository root. The results below are from macOS on Apple Silicon with Java 26 and none of the `APP_*` or `CI` variables set. **The number of skipped tests depends on your machine**, because many tests in Section 1 only run in certain environments.

| Goal | Command | Result |
| --- | --- | --- |
| Run the module | `mvn test -pl module-05` | 96 tests: 81 pass and 15 are skipped. The two failure demos are excluded |
| Run one class | `mvn test -pl module-05 -Dtest=OnboardingScenarioTest` | 6 tests pass |
| Run a tag | `mvn test -pl module-05 -Dgroups=fast` | 4 tests pass |
| Run the integration tests | `APP_ENV=integration APP_REGION=eu-west-1 mvn test -pl module-05 -Dtest=AppConfigIntegrationTest` | 6 tests pass |
| Run a failure demo | `mvn test -pl module-05 -Dtest=TimeoutFailuresDemoTest -DexcludedGroups=none` | `BUILD FAILURE`, as intended |

The module takes several seconds to run, because the timeout tests wait in real time.

### Maven profiles

The [module's POM](pom.xml) defines a profile for each common selection, so you don't need to remember the tag names:

| Profile | Command | Selects | Result |
| --- | --- | --- | --- |
| `fast` | `mvn test -pl module-05 -Pfast` | Tests tagged `fast` | 4 tests pass |
| `no-slow` | `mvn test -pl module-05 -Pno-slow` | Everything except `slow` and the failure demos | 94 tests: 79 pass and 15 are skipped |
| `security` | `mvn test -pl module-05 -Psecurity` | Tests tagged `security` | 4 tests pass |
| `integration` | `mvn test -pl module-05 -Pintegration` | Tests tagged `integration` | 2 tests, both skipped unless `APP_ENV=integration` is set |

The integration tests also need a region to pass:

```bash
APP_ENV=integration APP_REGION=eu-west-1 mvn test -pl module-05 -Pintegration
```

## Sections

Each section has its own package under [`src/test/java/com/example/module_05`](src/test/java/com/example/module_05).

### Section 1: Conditional Execution

**What it tests:** that a test runs only in a suitable environment. Each enabled test also checks the condition that let it run, so it can only pass where it should run. Start with `OsConditionsTest`.

| Class | What it tests |
| --- | --- |
| [`OsConditionsTest`](src/test/java/com/example/module_05/section_01/OsConditionsTest.java) | Tests enabled or disabled by operating system and processor architecture, including the `@UnixOnly` composed annotation |
| [`JreConditionsTest`](src/test/java/com/example/module_05/section_01/JreConditionsTest.java) | Tests enabled or disabled by Java version, using single versions and ranges, and enum constants and plain numbers |
| [`NativeImageConditionsTest`](src/test/java/com/example/module_05/section_01/NativeImageConditionsTest.java) | One test for a normal JVM and one for a GraalVM native image. On a normal JVM, the second is skipped |
| [`SystemPropertyConditionsTest`](src/test/java/com/example/module_05/section_01/SystemPropertyConditionsTest.java) | Tests that depend on `os.arch`, `app.region`, `app.env` and `app.offline`, including two conditions on one test |
| [`EnvironmentVariableConditionsTest`](src/test/java/com/example/module_05/section_01/EnvironmentVariableConditionsTest.java) | Tests that depend on the `PATH`, `APP_ENV` and `CI` environment variables |
| [`CustomConditionsTest`](src/test/java/com/example/module_05/section_01/CustomConditionsTest.java) | Conditions written as methods: in the test class, in another class, and an existing JDK method |
| [`SandboxOnlyTest`](src/test/java/com/example/module_05/section_01/SandboxOnlyTest.java) | A condition on the whole class, which runs only when `app.sandbox.url` is set |
| [`CombinedConditionsTest`](src/test/java/com/example/module_05/section_01/CombinedConditionsTest.java) | Several conditions on one test, where a single "disabled" result is enough to skip it |
| [`AppConfigIntegrationTest`](src/test/java/com/example/module_05/section_01/AppConfigIntegrationTest.java) | `AppConfig`'s lookup order using fixed values, which always runs, and the real environment using `@Integration` and `@UnixOnly` |

**JUnit concepts**

- **Conditional execution** decides whether a test runs before it starts. A disabled test is reported as skipped, with the reason.
- **`@EnabledOnOs` and `@DisabledOnOs`** check the operating system, the architecture or both.
- **`@EnabledOnJre` and `@DisabledOnJre`** check for specific Java versions. **`@EnabledForJreRange` and `@DisabledForJreRange`** check a range. Ranges include both ends, and a missing end leaves that side open. Plain numbers, such as `minVersion = 26`, can name versions newer than JUnit's `JRE` enum.
- **`@EnabledInNativeImage` and `@DisabledInNativeImage`** check whether the tests are running in a GraalVM native image.
- **`@EnabledIfSystemProperty` and `@DisabledIfSystemProperty`** check a system property, and **`@EnabledIfEnvironmentVariable` and `@DisabledIfEnvironmentVariable`** check an environment variable. `matches` is a regular expression that must match the whole value. You can repeat these annotations on a test, and every condition must allow it.
- **`@EnabledIf` and `@DisabledIf`** call a method that returns a `boolean`. It can be in the test class, or be a `static` method elsewhere, written as `fully.qualified.Class#method`. The method can take an `ExtensionContext`, which describes the current test.
- **`disabledReason`** explains why a test was skipped, and it appears in the report.
- **Combining conditions:** JUnit skips a test if any one condition disables it.
- **Conditions and assumptions:** a condition is checked before anything in the test or class runs. An assumption, from Module 2, runs inside the test or lifecycle method.
- **Composed annotations:** a condition can be part of a custom annotation, such as `@Integration`.

**Try it:** system properties and environment variables are set in different ways. Compare these runs of `SystemPropertyConditionsTest` to see the number of skipped tests change:

```bash
mvn test -pl module-05 -Dtest=SystemPropertyConditionsTest
mvn test -pl module-05 -Dtest=SystemPropertyConditionsTest -Dapp.region=eu-west-1
mvn test -pl module-05 -Dtest=SystemPropertyConditionsTest -Dapp.region=eu-west-1 -Dapp.env=staging -Dapp.offline=true
```

The first run skips two tests and the others skip one, but a different test each time: the last run enables the two-condition test, and `-Dapp.offline=true` disables `notWhenOffline`.

To run `SandboxOnlyTest`, add `-Dapp.sandbox.url=https://sandbox.example.com`. Environment variables come from the shell rather than `-D`, as in `APP_ENV=integration mvn test -pl module-05 -Dtest=EnvironmentVariableConditionsTest`.

### Section 2: Tags and Filtering

**What it tests:** which tags a test ends up with, and which tests a tag expression selects.

| Class | What it tests |
| --- | --- |
| [`TaggedClassTest`](src/test/java/com/example/module_05/section_02/TaggedClassTest.java) | The full set of tags on each test, from the class, the method and composed annotations such as `@Smoke` and `@LongRunning` |
| [`TagExpressionsTest`](src/test/java/com/example/module_05/section_02/TagExpressionsTest.java) | Five tests with different tags, for predicting which ones each expression selects |

Each expression below selects these tests from `TagExpressionsTest`:

| `-Dgroups=` | Tests selected |
| --- | --- |
| `fast` | `fastOnly`, `fastAndSecurity` |
| `!slow` | Every test except `slowOnly` |
| `fast & security` | `fastAndSecurity` |
| `smoke \| slow` | `slowOnly`, `smokeAndSecurity` |
| `(fast \| smoke) & !security` | `fastOnly` |
| `any()` | Every test that has a tag |
| `none()` | `untagged` |

Quote expressions in the shell, because `&`, `|` and `!` have their own meanings there:

```bash
mvn test -pl module-05 -Dtest=TagExpressionsTest -Dgroups='(fast | smoke) & !security'
```

**JUnit concepts**

- **`@Tag`** labels a class or method, and can be repeated. A test's tags are its own plus those of its class.
- **Tag names** can't be blank, or contain whitespace, control characters or any of `, ( ) & | !`.
- **Tag expressions** select tests using `!` (not), `&` (and), `|` (or) and parentheses.
- **`any()` and `none()`** select tests with at least one tag, and tests with no tags.
- **`-Dgroups` and `-DexcludedGroups`** are how Maven applies these filters. A test must match `groups` and not match `excludedGroups`.
- **Composed tag annotations**, such as `@Smoke`, define a tag name in one place. `@LongRunning` shows that they can also combine a tag with other behaviour, such as a timeout.

### Section 3: Execution Order

**What it tests:** that test methods and nested classes run in the order each orderer promises. Most of these classes record the order and check it in `@AfterAll`. Start with `OnboardingScenarioTest`.

| Class | What it tests |
| --- | --- |
| [`OnboardingScenarioTest`](src/test/java/com/example/module_05/section_03/OnboardingScenarioTest.java) | A full sign-up in six ordered steps that share one `Onboarding` instance |
| [`OrderAnnotationTest`](src/test/java/com/example/module_05/section_03/OrderAnnotationTest.java) | How `@Order` handles negative numbers and methods with no `@Order` |
| [`MethodNameOrderTest`](src/test/java/com/example/module_05/section_03/MethodNameOrderTest.java) | Ordering by method name, with the methods declared out of order |
| [`DisplayNameOrderTest`](src/test/java/com/example/module_05/section_03/DisplayNameOrderTest.java) | Ordering by display name, whatever the method names are |
| [`RandomOrderTest`](src/test/java/com/example/module_05/section_03/RandomOrderTest.java) | Independent onboarding tests that pass in any order. It prints the order so you can compare seeds |
| [`NestedClassOrderTest`](src/test/java/com/example/module_05/section_03/NestedClassOrderTest.java) | Ordering `@Nested` classes, inheriting a method orderer from the outer class, and opting out of it |
| [`HiddenCouplingDemoTest`](src/test/java/com/example/module_05/section_03/HiddenCouplingDemoTest.java) | Shows a hidden dependency between two tests. It [fails on purpose](#deliberately-failing-tests) whenever the order exposes the dependency |

**JUnit concepts**

- **Default order:** without an orderer, JUnit's order is repeatable but deliberately not obvious. Don't rely on it.
- **`@TestMethodOrder`** sets the orderer for a class's methods. Its nested classes inherit it, and `MethodOrderer.Default` opts back out.
- **`MethodOrderer.OrderAnnotation` with `@Order`** runs lower numbers first. A method without `@Order` gets `Order.DEFAULT`, a large number, so it runs after those with small numbers.
- **`MethodOrderer.MethodName` and `MethodOrderer.DisplayName`** sort alphabetically. Sorting is by text, so `10` comes before `2` unless you pad the numbers.
- **`MethodOrderer.Random`** shuffles the methods. Independent tests must pass in any order, so this is a cheap way to find hidden dependencies.
- **Random seed:** the `junit.jupiter.execution.order.random.seed` setting fixes the order, so you can repeat a failing run.
- **`@TestClassOrder` with `ClassOrderer`** orders `@Nested` classes. Top-level classes are ordered with the `junit.jupiter.testclass.order.default` setting instead.
- **Ordered scenarios:** sharing state across ordered tests suits a test that walks through a workflow, because the report shows which step broke. The cost is that a step can't run alone: running only `OnboardingScenarioTest#verify` fails with `Expected step CODE_SENT but was STARTED`. Unit tests shouldn't depend on order.

**Try it:** running `RandomOrderTest` twice with the same seed prints the same order, and a different seed prints a different order:

```bash
mvn test -pl module-05 -Dtest=RandomOrderTest -Djunit.jupiter.execution.order.random.seed=42
mvn test -pl module-05 -Dtest=RandomOrderTest -Djunit.jupiter.execution.order.random.seed=7
```

### Section 4: Timeouts

**What it tests:** the `TokenBucket`'s refill behaviour, and where a timeout does and doesn't apply. Start with `TokenBucketFakeClockTest`, because the best timeout is the one you don't need.

| Class | What it tests |
| --- | --- |
| [`TokenBucketFakeClockTest`](src/test/java/com/example/module_05/section_04/TokenBucketFakeClockTest.java) | The exact refill rules using a fake clock, with no real waiting and no timeout |
| [`TimeoutAnnotationTest`](src/test/java/com/example/module_05/section_04/TimeoutAnnotationTest.java) | `@Timeout` on tests and a lifecycle method, in both thread modes |
| [`ClassLevelTimeoutTest`](src/test/java/com/example/module_05/section_04/ClassLevelTimeoutTest.java) | That a class-level timeout covers nested tests but not lifecycle methods, and that a method-level timeout overrides it |
| [`PollingTimeoutTest`](src/test/java/com/example/module_05/section_04/PollingTimeoutTest.java) | Waiting for a bucket to refill and a background worker to finish, by checking repeatedly with a timeout as the upper limit |
| [`TimeoutOnTemplatesTest`](src/test/java/com/example/module_05/section_04/TimeoutOnTemplatesTest.java) | That each repetition of a `@RepeatedTest` gets the full timeout, and that a `@TestFactory` timeout covers only the factory method, not its dynamic tests |
| [`DefaultTimeoutExerciseTest`](src/test/java/com/example/module_05/section_04/DefaultTimeoutExerciseTest.java) | Takes about 200 ms and has no `@Timeout`, so you can see the effect of the timeout settings |
| [`TimeoutFailuresDemoTest`](src/test/java/com/example/module_05/section_04/TimeoutFailuresDemoTest.java) | Nothing passes: both tests [fail on purpose](#deliberately-failing-tests) by exceeding their timeout |

**JUnit concepts**

- **Controlling time:** if the code lets you inject its clock, a test can move time directly. It then gets exact results, and can't be flaky or hang.
- **`@Timeout`** fails a test or lifecycle method that takes too long. The unit defaults to seconds.
- **`@Timeout` on a class** applies to its test, test factory and test template methods, and to those in nested classes, but not to lifecycle methods. A timeout on a method takes priority.
- **Thread modes:** `SAME_THREAD` interrupts the test thread when time runs out. `SEPARATE_THREAD` runs the code on another thread, as `assertTimeoutPreemptively` does in Module 2. `INFERRED`, the default, reads the `junit.jupiter.execution.timeout.thread.mode.default` setting and otherwise uses `SAME_THREAD`.
- **Polling** suits background work with nothing to wait on directly: check, sleep briefly and check again. `@Timeout` stops the test from hanging the build if the condition never becomes true.
- **Templates and factories:** each invocation of a `@RepeatedTest` or `@ParameterizedTest` gets the full timeout. For a `@TestFactory`, use `assertTimeout` inside the dynamic tests if you need to limit each one.
- **Timeout settings** set defaults and can switch timeouts off. The most specific setting wins, and a value is a number with an optional unit, such as `100ms`. With no unit, it means seconds.

**Try it:** `DefaultTimeoutExerciseTest` behaves differently under each setting:

| Add to `mvn test -pl module-05 -Dtest=DefaultTimeoutExerciseTest` | Result |
| --- | --- |
| Nothing | Passes, because there's no timeout |
| `-Djunit.jupiter.execution.timeout.test.method.default=100ms` | Fails, because every `@Test` now has a 100 ms limit |
| `-Djunit.jupiter.execution.timeout.test.method.default=100ms -Djunit.jupiter.execution.timeout.mode=disabled` | Passes, because timeouts are ignored. `disabled_on_debug` ignores them only under a debugger |
| `-Djunit.jupiter.execution.timeout.test.method.default=100ms -Djunit.jupiter.execution.timeout.threaddump.enabled=true` | Fails, and first prints a dump of all threads, which helps show where a test hangs |

## Deliberately Failing Tests

In this module, two classes fail on purpose: [`HiddenCouplingDemoTest`](src/test/java/com/example/module_05/section_03/HiddenCouplingDemoTest.java) and [`TimeoutFailuresDemoTest`](src/test/java/com/example/module_05/section_04/TimeoutFailuresDemoTest.java). Other modules have similar classes, listed in the [root README](../README.md#deliberately-failing-tests).

### Why the failures are kept

The point of these classes is to show you real failures: a dependency between tests that random ordering exposes, and the messages a timeout produces. A class can't show a failure without failing, so its tests can't simply be corrected.

Left in the normal build, though, they would make `mvn test` end in `BUILD FAILURE`, and a real failure would be hard to spot. `@Disabled` wouldn't help, because the tests would never run and there would be no output to read. A tag solves both problems: the tests stay out of the normal build, but can still be run when you ask for them.

### How they are excluded

Two pieces work together:

1. Each class is labelled with a tag:

   ```java
   @Tag("expected-failure-tests")
   class TimeoutFailuresDemoTest {
   ```

2. The [parent POM](../pom.xml) sets a property that Maven Surefire, the plugin that runs the tests, reads as its list of tags to exclude:

   ```xml
   <excludedGroups>expected-failure-tests</excludedGroups>
   ```

Because the tag is on the class, all of its tests are excluded.

The `no-slow` profile in this module replaces that property's value, so it lists the tag again along with `slow`:

```xml
<excludedGroups>expected-failure-tests,slow</excludedGroups>
```

### How to override the exclusion

A value passed on the command line takes priority over the property in the POM. That makes `-DexcludedGroups=none` the usual override. `none` isn't a special keyword: it is just a tag that no test uses, so nothing is excluded.

| Goal | Command |
| --- | --- |
| Run the timeout failure demo | `mvn test -pl module-05 -Dtest=TimeoutFailuresDemoTest -DexcludedGroups=none` |
| Run the hidden coupling demo | `mvn test -pl module-05 -Dtest=HiddenCouplingDemoTest -DexcludedGroups=none` |
| Run the whole module, including both demos | `mvn test -pl module-05 -DexcludedGroups=none` |
| Run only the tests with this tag | `mvn test -pl module-05 -Dgroups=expected-failure-tests -DexcludedGroups=none` |
| Run a demo without failing the build | `mvn test -pl module-05 -Dtest=TimeoutFailuresDemoTest -DexcludedGroups=none -Dmaven.test.failure.ignore=true` |

The exclusion has to be cleared even when you select the class or tag by name. These two commands report `BUILD SUCCESS` with `Tests run: 0`, because the exclusion still applies:

```bash
mvn test -pl module-05 -Dtest=TimeoutFailuresDemoTest
mvn test -pl module-05 -Dgroups=expected-failure-tests
```

The exclusion is part of the Maven build, so running the class directly from your IDE normally ignores it, unless the IDE is set to run tests through Maven.

Keep the override on the command line. Removing the tags or the POM property would make normal builds fail.

### What to expect

`TimeoutFailuresDemoTest` always ends with this summary and `BUILD FAILURE`, which is the intended result:

```text
Tests run: 2, Failures: 0, Errors: 2, Skipped: 0
```

| Test | What it shows | Failure message |
| --- | --- | --- |
| `waitForever` | A timeout in `SAME_THREAD` mode, which interrupts a blocked `acquire()` | `waitForever() timed out after 200 milliseconds` |
| `waitForeverInASeparateThread` | A timeout in `SEPARATE_THREAD` mode | `waitForeverInASeparateThread() timed out after 200 milliseconds` |

Maven counts these as errors rather than failures, because a timeout isn't a failed assertion.

`HiddenCouplingDemoTest` is different, because **it doesn't always fail**. It uses random order, and `sendsTheCode` relies on `createsTheOnboarding` having set a `static` field first:

| Order | Result |
| --- | --- |
| `createsTheOnboarding` runs first | Both tests pass, and the build succeeds |
| `sendsTheCode` runs first | `sendsTheCode` throws `NullPointerException`, and the build fails |

The seed makes either outcome repeatable:

```bash
# Fails
mvn test -pl module-05 -Dtest=HiddenCouplingDemoTest -DexcludedGroups=none -Djunit.jupiter.execution.order.random.seed=1

# Passes
mvn test -pl module-05 -Dtest=HiddenCouplingDemoTest -DexcludedGroups=none -Djunit.jupiter.execution.order.random.seed=12345
```

Without a seed, each run can go either way. So a run of the whole module with the override reports 100 tests with either 2 or 3 errors. A test like this can pass for months and then fail with no related change, which is why these dependencies are worth finding. To fix it, make each test create its own `Onboarding` rather than adding `@Order`.
