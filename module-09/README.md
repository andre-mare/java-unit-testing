# Module 9: Built-in Extensions and Parallel Execution

This module covers JUnit's built-in help for tests that touch the outside world: temporary directories, resources that need closing, the default locale and time zone, and system properties. It then covers how to run tests in parallel, and how to keep tests that share state from interfering with each other.

## Table of Contents

- [Module 9: Built-in Extensions and Parallel Execution](#module-9-built-in-extensions-and-parallel-execution)
  - [Table of Contents](#table-of-contents)
  - [The Domain: Audit Logs, Sessions and Settings](#the-domain-audit-logs-sessions-and-settings)
    - [Production classes](#production-classes)
    - [Test helpers and data](#test-helpers-and-data)
  - [Running the Module](#running-the-module)
  - [Sections](#sections)
    - [Section 1: Temporary Directories](#section-1-temporary-directories)
    - [Section 2: Closing Resources Automatically](#section-2-closing-resources-automatically)
    - [Section 3: Default Locale and Time Zone](#section-3-default-locale-and-time-zone)
    - [Section 4: System Properties](#section-4-system-properties)
    - [Section 5: Parallel Execution](#section-5-parallel-execution)
  - [Deliberately Failing Tests](#deliberately-failing-tests)
    - [Why this module has no excluded tests](#why-this-module-has-no-excluded-tests)
    - [What the usual override does here](#what-the-usual-override-does-here)
    - [How to run the race demo](#how-to-run-the-race-demo)
    - [What to expect](#what-to-expect)

## The Domain: Audit Logs, Sessions and Settings

Each class in this module depends on something outside itself, such as a file, a background thread, or a global JVM setting. That makes each one hard to test without the features this module covers.

**Audit logs**, for temporary directories, closing resources and parallel execution. An [`AuditEvent`](src/main/java/com/example/audit/AuditEvent.java) is one line in a security audit log, such as `2026-11-02T09:00:00Z|alice|login|SUCCESS`.

| Class | Why it's hard to test |
| --- | --- |
| [`AuditLogWriter`](src/main/java/com/example/audit/AuditLogWriter.java) | Keeps a file open, so it needs a directory and must be closed |
| [`AuditArchiver`](src/main/java/com/example/audit/AuditArchiver.java) | Moves files between directories, and must never overwrite one |
| [`AuditShipper`](src/main/java/com/example/audit/AuditShipper.java) | Holds a background thread, and stops with `shutdown()` rather than `close()` |
| [`AuditTrail`](src/main/java/com/example/audit/AuditTrail.java) | Is a global list on purpose, so tests running at the same time can disturb each other |

**Formatting and sessions**, for the default locale and time zone. [`AmountFormatter`](src/main/java/com/example/display/AmountFormatter.java) formats money in the JVM's default locale. [`SessionExpiry`](src/main/java/com/example/session/SessionExpiry.java) ends a session at local midnight, using the default time zone, so the answer depends on the machine. A local day can even be 23 hours long when the clocks change.

**Settings**, for system properties. [`AuthSettings`](src/main/java/com/example/settings/AuthSettings.java) reads which identity provider to trust from system properties:

| Property | Rule | Default |
| --- | --- | --- |
| `auth.issuer` | Required, and must be an `https://` URL | None |
| `auth.audience` | The expected audience | `payments-api` |
| `auth.clock-skew-seconds` | A number from 0 to 300 | `30` |

### Production classes

| Type | Purpose |
| --- | --- |
| [`AuditEvent`](src/main/java/com/example/audit/AuditEvent.java) | One audit log line: when, who, what and the outcome |
| [`AuditLogWriter`](src/main/java/com/example/audit/AuditLogWriter.java) | Appends events to a file. Closing twice is allowed, but writing after close isn't |
| [`AuditLogReader`](src/main/java/com/example/audit/AuditLogReader.java) | Reads a log back as lines or events |
| [`AuditArchiver`](src/main/java/com/example/audit/AuditArchiver.java) | Moves a finished log to an archive directory, adding a number if the name is taken |
| [`AuditShipper`](src/main/java/com/example/audit/AuditShipper.java) | Sends events to a destination on a background thread |
| [`AuditTrail`](src/main/java/com/example/audit/AuditTrail.java) | A process-wide list of events, with a lock name for tests to use |
| [`AmountFormatter`](src/main/java/com/example/display/AmountFormatter.java) | Formats amounts in the default locale or a given locale |
| [`SessionExpiry`](src/main/java/com/example/session/SessionExpiry.java) | Works out when a session ends in the default time zone |
| [`AuthSettings`](src/main/java/com/example/settings/AuthSettings.java) | Reads and validates the settings above |

### Test helpers and data

These files aren't test classes, so JUnit doesn't run them directly.

| File | Purpose |
| --- | --- |
| [`SampleAuditEvents`](src/test/java/com/example/module_09/support/SampleAuditEvents.java) | A few audit events from one morning |
| [`ParallelSettings`](src/test/java/com/example/module_09/support/ParallelSettings.java) | Reports whether parallel execution is on for this run |
| [`PrefixedTempDirFactory`](src/test/java/com/example/module_09/section_01/PrefixedTempDirFactory.java) | A `TempDirFactory` that names directories after the test class |
| [`JimfsTempDirFactory`](src/test/java/com/example/module_09/section_01/JimfsTempDirFactory.java) | A `TempDirFactory` that creates directories in memory, using the Jimfs library |
| [`@JimfsTempDir`](src/test/java/com/example/module_09/section_01/JimfsTempDir.java) | A shorter form of `@TempDir(factory = JimfsTempDirFactory.class)` |
| [`AuditTrailLocks`](src/test/java/com/example/module_09/section_05/AuditTrailLocks.java) | A `ResourceLocksProvider` that gives `reads...` methods a shared lock and others an exclusive one |
| [`module9-parallel.properties`](src/test/resources/module9-parallel.properties) | Example parallel settings, with a checklist of what usually breaks. JUnit doesn't read this file automatically |

## Running the Module

Run these commands from the repository root.

| Goal | Command | Result |
| --- | --- | --- |
| Run the module | `mvn test -pl module-09` | 93 tests pass. Nothing is excluded, so this includes the race demo, which passes when run sequentially |
| Run one class | `mvn test -pl module-09 -Dtest=DefaultTimeZoneTest` | 4 tests pass |
| Leave out the race demo | `mvn test -pl module-09 -DexcludedGroups=failure-demo` | 73 tests pass |
| Run the race demo in parallel | `mvn test -pl module-09 -Dtest=AuditTrailRaceDemoTest -Djunit.jupiter.execution.parallel.enabled=true` | `BUILD FAILURE`, as intended |

`ParallelismProbeTest` is listed with `Tests run: 0` in a normal run, because its two tests only run when parallel execution is on.

## Sections

Each section has its own package under [`src/test/java/com/example/module_09`](src/test/java/com/example/module_09).

### Section 1: Temporary Directories

**What it tests:** that audit logs can be written, read back and archived, using directories that JUnit creates and deletes. Start with `TempDirInjectionTest`.

| Class | What it tests |
| --- | --- |
| [`TempDirInjectionTest`](src/test/java/com/example/module_09/section_01/TempDirInjectionTest.java) | Injecting a directory into a field and into parameters, as a `Path` or a `File`, and that each declaration gets its own empty directory |
| [`TempDirConstructorTest`](src/test/java/com/example/module_09/section_01/TempDirConstructorTest.java) | Injecting a directory into a constructor, so the object under test can be `final` and starts fresh for each test |
| [`SharedTempDirTest`](src/test/java/com/example/module_09/section_01/SharedTempDirTest.java) | A `static` directory shared by all the tests in a class, with their order fixed |
| [`AuditLogRoundTripTest`](src/test/java/com/example/module_09/section_01/AuditLogRoundTripTest.java) | Writing a log and checking it with `assertLinesMatch`, appending, archiving without overwriting, and the error cases |
| [`TempDirCleanupTest`](src/test/java/com/example/module_09/section_01/TempDirCleanupTest.java) | That `ALWAYS` deletes a directory and `NEVER` keeps it, checked in `@AfterAll` |
| [`TempDirFactoryTest`](src/test/java/com/example/module_09/section_01/TempDirFactoryTest.java) | A custom factory, a deletion strategy that ignores failures, and in-memory directories with Jimfs |

**JUnit concepts**

- **`@TempDir`** gives a test a new, empty directory and deletes it afterwards. It works on fields, constructor parameters, and lifecycle or test method parameters, as a `Path` or a `File`. Fields must not be `private` or `final`.
- **Separate directories:** each declaration gets its own directory. A `static` field is shared by all the tests in the class, which suits expensive setup but makes the tests depend on each other.
- **`cleanup`** sets when a directory is deleted. `ALWAYS` is the default, `ON_SUCCESS` keeps it when the test fails so you can look at the files, and `NEVER` always keeps it. The `junit.jupiter.tempdir.cleanup.mode.default` setting changes the default for a run.
- **`factory` with `TempDirFactory`** controls where a directory comes from, such as an in-memory file system. A `Path` from Jimfs has no `File`, so inject it as a `Path` only.
- **`deletionStrategy`** controls how deletion failures are handled. `Standard` fails the test, and `IgnoreFailures` only logs a warning.
- **`assertLinesMatch`** suits logs: each expected line can be exact or a regular expression, and `>> 1 >>` skips an exact number of lines.

### Section 2: Closing Resources Automatically

**What it tests:** that JUnit closes writers and shippers at the right moment. Each class checks the timing in `@AfterAll`.

| Class | What it tests |
| --- | --- |
| [`AutoCloseFieldsTest`](src/test/java/com/example/module_09/section_02/AutoCloseFieldsTest.java) | That instance fields are closed after each test, and that a `static` field isn't closed until after `@AfterAll` |
| [`AutoCloseCustomMethodTest`](src/test/java/com/example/module_09/section_02/AutoCloseCustomMethodTest.java) | Closing an `AuditShipper` through its `shutdown()` method, and that shutting down delivers everything queued |
| [`AutoClosePerClassTest`](src/test/java/com/example/module_09/section_02/AutoClosePerClassTest.java) | That under `PER_CLASS`, an instance field stays open across tests and is closed once after `@AfterAll` |

**JUnit concepts**

- **`@AutoClose`** closes a field for you, so you don't need an `@AfterEach` for it. It calls `close()` by default, and `@AutoClose("shutdown")` names another method.
- **Timing:** an instance field is closed after each test, following `@AfterEach`. A `static` field is closed once, after `@AfterAll`. Under `PER_CLASS`, an instance field lives as long as the class, so it's also closed after `@AfterAll`.

### Section 3: Default Locale and Time Zone

**What it tests:** that formatting and session expiry give the right answer in a chosen locale or time zone, whatever the machine is set to.

| Class | What it tests |
| --- | --- |
| [`DefaultLocaleTest`](src/test/java/com/example/module_09/section_03/DefaultLocaleTest.java) | Number and currency formats in British, Dutch, American and Belgian French locales, set in each of the three ways |
| [`DefaultTimeZoneTest`](src/test/java/com/example/module_09/section_03/DefaultTimeZoneTest.java) | That one login ends at different times in Amsterdam and UTC, and that the day the clocks go forward has 23 hours |
| [`LocaleSensitiveCodeTest`](src/test/java/com/example/module_09/section_03/LocaleSensitiveCodeTest.java) | That the code follows the machine's defaults, without setting them |

**JUnit concepts**

- **`@DefaultLocale`** sets the default locale for a class or test and restores it afterwards. You can give a language tag, parts such as `language` and `country`, or a `localeProvider` class, but not a combination. An annotation on a method overrides one on the class.
- **`@DefaultTimeZone`** does the same for the time zone, with a zone ID or a `timeZoneProvider` class. Use full IDs such as `Europe/Amsterdam` rather than abbreviations such as `CET`.
- **`LocaleProvider` and `TimeZoneProvider`** compute the value in code, and need a no-argument constructor.
- **`@ReadsDefaultLocale` and `@ReadsDefaultTimeZone`** declare that a test reads a default. The parallel scheduler then won't run it while another test changes that default.
- **Testable design:** the `AmountFormatter` methods that take a `Locale` are easier to test than those that read the default, because they don't depend on global state.

Exact currency strings can differ between JDK versions, so `DefaultLocaleTest` checks the separators rather than the whole string.

### Section 4: System Properties

**What it tests:** that `AuthSettings` reads its properties correctly and rejects bad values, and that no test leaks a property into the next one.

| Class | What it tests |
| --- | --- |
| [`AuthSettingsTest`](src/test/java/com/example/module_09/section_04/AuthSettingsTest.java) | The defaults, switching the issuer, several properties at once, and rejecting a missing issuer, plain HTTP and bad clock skew values |
| [`RestoreSystemPropertiesTest`](src/test/java/com/example/module_09/section_04/RestoreSystemPropertiesTest.java) | That properties set in code are restored, proved by a second test that finds nothing left behind |

**JUnit concepts**

- **`@SetSystemProperty` and `@ClearSystemProperty`** set or remove a property for a class or test, and restore the original value afterwards. They're repeatable, and an annotation on a method takes priority.
- **`@RestoreSystemProperties`** takes a snapshot of all the properties before a test and restores them afterwards. It suits code that sets properties itself.
- **`@ReadsSystemProperty`** declares that a test only reads properties, and `@WritesSystemProperty` declares that it changes them, so the parallel scheduler can keep them apart.

### Section 5: Parallel Execution

**What it tests:** that tests really can run at the same time, and that locks stop tests that share `AuditTrail` from interfering. Start with `ParallelismProbeTest`.

Parallel execution is off by default, so most of what this section shows is only visible with it on. In a normal run, these classes run sequentially and pass.

| Class | What it tests |
| --- | --- |
| [`ParallelismProbeTest`](src/test/java/com/example/module_09/section_05/ParallelismProbeTest.java) | Two tests that each wait for the other to start, which only works on two threads at once. With parallel execution off, an assumption stops them |
| [`AuditTrailRaceDemoTest`](src/test/java/com/example/module_09/section_05/AuditTrailRaceDemoTest.java) | Repeated tests that clear and count a global list with no lock, and [fail on purpose](#deliberately-failing-tests) in parallel |
| [`AuditTrailLockTest`](src/test/java/com/example/module_09/section_05/AuditTrailLockTest.java) | The same test with `@ResourceLock(AuditTrail.LOCK)`, the fix for the race |
| [`ResourceLocksProviderTest`](src/test/java/com/example/module_09/section_05/ResourceLocksProviderTest.java) | Locks decided in code by `AuditTrailLocks`, so readers can share and writers run alone |
| [`SharedReadLockTest`](src/test/java/com/example/module_09/section_05/SharedReadLockTest.java) | JUnit's predefined `Resources.LOCALE` and `Resources.TIME_ZONE` locks, held as read locks on each method |
| [`IsolatedBootstrapTest`](src/test/java/com/example/module_09/section_05/IsolatedBootstrapTest.java) | A test that changes `System.out`, the locale, the time zone and the audit trail, and runs with nothing else alongside it |

**JUnit concepts**

- **Parallel settings:** `junit.jupiter.execution.parallel.enabled` switches it on. `mode.default` controls methods within a class, and `mode.classes.default` controls top-level classes. [`module9-parallel.properties`](src/test/resources/module9-parallel.properties) has the full names, along with the thread pool settings.
- **`@Execution`** sets the mode for a class or method. `CONCURRENT` allows it to run alongside others, and `SAME_THREAD` keeps it on its parent's thread. It has no effect while parallel execution is off.
- **`@ResourceLock`** names a shared resource. Tests holding the same lock don't run at the same time. **A lock only protects you from tests that also declare it.**
- **`ResourceAccessMode`:** `READ_WRITE`, the default, is exclusive, and `READ` lets readers share.
- **`target = ResourceLockTarget.CHILDREN`** puts the lock on each method rather than on the whole class, so read locks don't force the methods to run one at a time.
- **`Resources`** names common global resources: `SYSTEM_PROPERTIES`, `SYSTEM_OUT`, `SYSTEM_ERR`, `LOCALE` and `TIME_ZONE`. The built-in extensions from Sections 3 and 4 take these locks for you, but code that changes them by hand must declare them itself.
- **`ResourceLocksProvider`** decides locks in code, rather than with an annotation on each test.
- **`@Isolated`** runs a class with nothing else alongside it. Use it sparingly, because it stops all parallelism while it runs.

**Try it:** run this whole module in parallel, leaving out the race demo. All the remaining tests pass, including the two in `ParallelismProbeTest`, which brings the total to 75:

```bash
mvn test -pl module-09 -DexcludedGroups=failure-demo \
  -Djunit.jupiter.execution.parallel.enabled=true \
  -Djunit.jupiter.execution.parallel.mode.default=concurrent \
  -Djunit.jupiter.execution.parallel.mode.classes.default=concurrent
```

Running the whole course this way currently fails, as the [root README](../README.md#parallel-execution) explains. Exposing and fixing tests that aren't safe to run in parallel is this section's lab.

## Deliberately Failing Tests

In this module, [`AuditTrailRaceDemoTest`](src/test/java/com/example/module_09/section_05/AuditTrailRaceDemoTest.java) is the only class that's meant to fail, and only when parallel execution is on. Unlike the demos in other modules, listed in the [root README](../README.md#deliberately-failing-tests), it isn't excluded from the normal build.

### Why this module has no excluded tests

The other modules tag their failure demos `expected-failure-tests`, because those classes would fail every build. No class in this module has that tag.

The race demo is tagged `failure-demo` instead, and the [parent POM](../pom.xml) doesn't exclude that tag:

```java
@Tag("failure-demo")
@Execution(ExecutionMode.CONCURRENT)
class AuditTrailRaceDemoTest {
```

```xml
<excludedGroups>expected-failure-tests</excludedGroups>
```

The class can stay in the normal build because tests run sequentially by default, so its 20 repetitions can't disturb each other and they all pass. That's also the point of the demo: a race can stay hidden until tests run in parallel.

### What the usual override does here

`-DexcludedGroups=none` clears the POM's exclusion, but no test in this module has the excluded tag, so the override changes nothing:

| Command | Result |
| --- | --- |
| `mvn test -pl module-09 -DexcludedGroups=none` | The same as a normal run: 93 tests pass |
| `mvn test -pl module-09 -Dgroups=expected-failure-tests -DexcludedGroups=none` | `BUILD SUCCESS` with `Tests run: 0` |

### How to run the race demo

The setting that matters in this module is parallel execution:

| Goal | Command |
| --- | --- |
| See the race | `mvn test -pl module-09 -Dtest=AuditTrailRaceDemoTest -Djunit.jupiter.execution.parallel.enabled=true` |
| See the fix pass | `mvn test -pl module-09 -Dtest=AuditTrailLockTest -Djunit.jupiter.execution.parallel.enabled=true` |
| Select the demo by its tag | `mvn test -pl module-09 -Dgroups=failure-demo` |
| Leave the demo out | `mvn test -pl module-09 -DexcludedGroups=failure-demo` |
| See the race without failing the build | `mvn test -pl module-09 -Dtest=AuditTrailRaceDemoTest -Djunit.jupiter.execution.parallel.enabled=true -Dmaven.test.failure.ignore=true` |

Setting `-DexcludedGroups=failure-demo` replaces the POM's value for that run. This is safe in this module because no test has the `expected-failure-tests` tag, but in other modules it would bring their failure demos back. To leave both out across modules, use `-DexcludedGroups=expected-failure-tests,failure-demo`.

### What to expect

With parallel execution on, the demo ends in `BUILD FAILURE`, which is the intended result. Some repetitions fail with a message such as:

```text
expected: <5> but was: <50>
```

Each repetition clears the list, adds 5 entries and counts them, but other repetitions are adding entries at the same time. The numbers depend on your machine and can change between runs. This is what a flaky test looks like. For example, one machine with 10 processors reported this when running the class alone:

```text
Tests run: 20, Failures: 10, Errors: 0, Skipped: 0
```

The demo also shows the limit of a lock. If you run the whole module in parallel with the demo included, `AuditTrailLockTest` can fail too, even though it holds the lock, because the demo changes the same list without taking the lock:

| Parallel run of the whole module | Result |
| --- | --- |
| Including the race demo | Failures in `AuditTrailRaceDemoTest`, and sometimes in `AuditTrailLockTest` |
| Using `-DexcludedGroups=failure-demo` | All 75 tests pass |

So every test that uses a shared resource must declare the lock, not just the ones you're worried about.
