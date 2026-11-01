Pulling Module 9's submodules from the course outline first.

The `util` annotations are confirmed. Next I'll check `@TempDir` in 6.1, in particular its new `deletionStrategy`.

The rest checks out. Last API check: the current Jimfs version for the in-memory `@TempDir` lab.

Now the exact parallel-execution configuration keys, including the new executor-service setting.

The APIs are confirmed. Next I'll write the production code: an audit log with writer, reader, archiver and asynchronous shipper, plus amount formatting, session expiry and issuer settings.

Now the 9.1 `@TempDir` tests.

That file has a stray `Stream` stand-in that wouldn't compile. Fixing it with the real `java.util.stream.Stream` import.

Now the 9.2 `@AutoClose` tests.

Now the 9.3 default locale and time zone tests.

Now 9.4, the system-property tests.

Now 9.5, parallel execution. I'll add a small support helper that reads whether parallel mode is on.

Now the two snippet files for the zip root (Jimfs dependency and parallel settings), plus package-info files.

Now running the production code against every expected value in the tests, in plain Java.

All behaviour checks pass on JDK 21. Next is the syntax pass over the test sources.

For the 9.5 lab ("fix every flaky test"), I'll scan Modules 1–8 for tests that would break under parallel mode. I'll report what I find rather than edit those modules.

I've built Module 9 and sent it as **junit6-module9.zip**. It has 55 files, with nothing from Modules 1–8 in the code or the zip. JUnit still can't be downloaded here, so the tests haven't run. I checked every value the tests expect against the production code in plain Java on JDK 21. That covered the IBAN-free audit logs, locale formats, the March 2026 clock change and system properties, and all matched.

**Before you build:** add the Jimfs dependency from `module9-pom-dependencies.xml` (Jimfs 1.3.0) to your pom. Without it, the whole test folder won't compile, the same as with Mockito in Module 6. Everything else in Module 9 comes with JUnit 6.1.

**Production code (new for Module 9)**
- **`com.example.audit`:**
  - `AuditEvent` is one line in a security audit log.
  - `AuditLogWriter` writes the log; it must be closed after use.
  - `AuditLogReader` reads it back.
  - `AuditArchiver` moves a finished log away without ever overwriting.
  - `AuditShipper` sends events on a background thread and stops with `shutdown()`.
  - `AuditTrail` is a deliberately global list of events, used for the parallel lab.
- **`com.example.display.AmountFormatter`:** formats amounts in the machine's default locale.
- **`com.example.session.SessionExpiry`:** a session ends at local midnight, which depends on the default time zone.
- **`com.example.settings.AuthSettings`:** reads the trusted issuer, audience and clock skew from system properties.

**Tests per submodule**

| Package | Classes | What it shows |
|---|---|---|
| `s01_temp_dir` | `TempDirInjectionTest`, `TempDirConstructorTest`, `SharedTempDirTest`, `AuditLogRoundTripTest` (lab), `TempDirCleanupTest`, `TempDirFactoryTest`, plus `PrefixedTempDirFactory`, `JimfsTempDirFactory` and `@JimfsTempDir` | Injecting temp folders (field, parameter, constructor, static), `assertLinesMatch` with patterns and line-skipping, the cleanup modes (checked in `@AfterAll`), custom factories, in-memory folders with Jimfs, `IgnoreFailures` deletion |
| `s02_auto_close` | `AutoCloseFieldsTest`, `AutoCloseCustomMethodTest` (lab), `AutoClosePerClassTest` | Instance fields close after each test, static fields after `@AfterAll`; `@AutoClose("shutdown")`; timing under `PER_CLASS` |
| `s03_default_locale_and_time_zone` | `DefaultLocaleTest` (lab), `DefaultTimeZoneTest` (lab), `LocaleSensitiveCodeTest` | Setting the locale by tag, by parts or by a provider class. The same login gives a different session end in Amsterdam and UTC, and 29 March 2026 is a 23-hour day. `@ReadsDefault…` marks tests that only read the defaults. |
| `s04_system_properties` | `AuthSettingsTest` (lab), `RestoreSystemPropertiesTest` | `@SetSystemProperty` and `@ClearSystemProperty` (repeatable, class or method level), `@RestoreSystemProperties`, `@ReadsSystemProperty` |
| `s05_parallel_execution` | `ParallelismProbeTest`, `AuditTrailLockTest` (lab), `AuditTrailRaceDemoTest`*, `ResourceLocksProviderTest` with `AuditTrailLocks`, `SharedReadLockTest`, `IsolatedBootstrapTest` | Proof that tests really run at once; a flaky race and its `@ResourceLock` fix; a provider that gives read-only tests shared locks; JUnit's predefined resources with `target = CHILDREN`; `@Isolated` |

\* Tagged `failure-demo`, so normal builds skip it. With parallel mode on, run `mvn test -DexcludedGroups=none -Dtest=AuditTrailRaceDemoTest`.

**The 9.5 lab**
`module9-parallel.properties` holds the settings to merge into `junit-platform.properties`. It also shows a one-off `-D…` command line, and has a checklist of what usually breaks. I scanned Modules 1–8 for tests that would break in parallel. Two count shared static state in `@AfterAll` without being kept single-threaded:
- `m03…/DisabledMethodTest`
- `m07…/ParameterOrderTest`

Adding `@Execution(ExecutionMode.SAME_THREAD)` to each fixes them. `m01…/LifecycleDemoTest` has the same pattern, but it only prints its count, so nothing fails. I left those modules unchanged, since they're outside this zip.

**Check these first when you run it**
- **`deletionStrategy`:** `@TempDir(deletionStrategy = …)` in `TempDirFactoryTest` is new and experimental in 6.1. I confirmed the strategy classes in the API docs, but not the attribute name itself.
- **`AutoCloseFieldsTest`:** its `@AfterAll` check assumes JUnit closes static `@AutoClose` fields after `@AfterAll`, as the docs describe.
- **`ParallelismProbeTest`:** it needs at least two worker threads. When parallel mode is off it is skipped, not failed.

I also updated the 9.2, 9.3 and 9.4 lab lines in the course outline. All nine modules are now done; the capstone is the only part of the outline left.

Sources:
- [org.junit.jupiter.api.util package (6.1.1 API)](https://docs.junit.org/6.1.1/api/org.junit.jupiter.api/org/junit/jupiter/api/util/package-tree.html)
- [DefaultLocale](https://docs.junit.org/6.1.1/api/org.junit.jupiter.api/org/junit/jupiter/api/util/DefaultLocale.html), [DefaultTimeZone](https://docs.junit.org/6.1.1/api/org.junit.jupiter.api/org/junit/jupiter/api/util/DefaultTimeZone.html), [SetSystemProperty](https://docs.junit.org/6.1.1/api/org.junit.jupiter.api/org/junit/jupiter/api/util/SetSystemProperty.html), [RestoreSystemProperties](https://docs.junit.org/6.1.1/api/org.junit.jupiter.api/org/junit/jupiter/api/util/RestoreSystemProperties.html)
- [TempDirDeletionStrategy](https://docs.junit.org/6.1.1/api/org.junit.jupiter.api/org/junit/jupiter/api/io/TempDirDeletionStrategy.html)
- [Parallel Execution (6.1.2 user guide)](https://docs.junit.org/6.1.2/writing-tests/parallel-execution.html)
- [Jimfs on Maven Central](https://central.sonatype.com/artifact/com.google.jimfs/jimfs/1.3.0)