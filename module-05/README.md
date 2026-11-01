    
    Module 5 gets three new production classes, each feeding one part of the module:

- **`TokenBucket`**, a rate limiter, for the timeout tests.
- **`AppConfig`**, which reads settings from system properties and environment variables, for the conditional tests.
- **`Onboarding`**, a step-by-step sign-up flow, for the execution-order tests.

Now the Module 5 support package: the composed annotations, a fake clock, and condition helpers. None of these names start or end with `Test`, so Surefire won't try to run them.

Now 5.1, conditional execution. Every enabled test body checks the condition that let it run, so a test can only pass in the environment it claims to need.

`SystemPropertyConditionsTest` ended up with a pointless private `Set` helper class shadowing `java.util.Set`. I'm replacing it with the normal import.

Now 5.2, tagging and filtering.

Now 5.3, execution order. Each class records the order its tests ran and checks it in `@AfterAll`.

Now 5.4, timeouts. I'm using generous limits, a few times the expected run time, so the tests stay reliable on slow CI machines.

Next I'll write the `package-info` files, then compile the production classes and check the real timings and values the tests depend on.

The check script hung: the scheduled executor's thread kept the JVM alive, so it never exited. I'll make it exit explicitly and write the output to a file.

The three lab lines are updated. Packaging only the Module 5 files, plus the Maven profile snippet:

Module 5 is in `junit6-module5.zip`, with no Module 1–4 code, no `pom.xml` and no `junit-platform.properties`. Unzip it into the folder that contains `unit-testing-examples`.

I compiled the production classes and ran 24 checks covering the values and the real timings the tests depend on; all passed. I couldn't run JUnit itself because Maven Central is blocked here, so please run `mvn test` on your machine.

**Production classes** (new, unrelated to earlier modules)

- **`com.example.config.AppConfig`** reads settings from a system property (`app.region`) or, failing that, an environment variable (`APP_REGION`).
- **`com.example.onboarding.Onboarding`** is a sign-up flow that must go register, send code, verify, activate, in that order.
- **`com.example.ratelimit.TokenBucket`** is a rate limiter with a non-blocking `tryAcquire()` and a blocking `acquire()`. Its clock can be swapped for a fake one in tests.

**Shared within Module 5** (`support`)

- **Composed annotations:** `@Integration`, `@UnixOnly`, `@Smoke`, `@Security` and `@LongRunning`.
- **Helpers:** `FakeNanoClock` and `Environments`, which holds condition methods.

**5.1 `s01_conditional_execution`**

| Class | Covers |
| --- | --- |
| `OsConditionsTest` | Operating system and processor architecture |
| `JreConditionsTest` | Java version, by enum constant and by plain number |
| `NativeImageConditionsTest` | GraalVM native images |
| `SystemPropertyConditionsTest` | System properties, including two conditions on one test |
| `EnvironmentVariableConditionsTest` | Environment variables |
| `CustomConditionsTest` | `@EnabledIf` / `@DisabledIf` with local and external methods |
| `SandboxOnlyTest` | A condition on the whole class |
| `CombinedConditionsTest` | One "disabled" condition is enough to skip |
| `AppConfigIntegrationTest` | The lab, using `@Integration` and `@UnixOnly` |

Every enabled test checks the condition that enabled it. Expect many skipped tests; which ones depends on your OS, Java version and environment variables.

**5.2 `s02_tagging_filtering`**

- **`TaggedClassTest`** checks the full tag set of each test.
- **`TagExpressionsTest`** has a table of expressions such as `fast & !slow` and `none()`, and the tests each one selects.

**5.3 `s03_execution_order`**

| Class | Covers |
| --- | --- |
| `OnboardingScenarioTest` | The lab: six ordered steps sharing one instance |
| `OrderAnnotationTest` | How `@Order` treats negative numbers and unannotated methods |
| `MethodNameOrderTest`, `DisplayNameOrderTest` | Ordering by method name and by display name |
| `RandomOrderTest` | Random order, repeatable with a seed |
| `HiddenCouplingDemoTest` | A hidden dependency between tests that random order exposes |
| `NestedClassOrderTest` | Ordering `@Nested` classes, and how an orderer is inherited or switched off |

Each ordering test records the actual order and checks it in `@AfterAll`.

**5.4 `s04_timeouts`**

| Class | Covers |
| --- | --- |
| `TokenBucketFakeClockTest` | Start here: exact tests with no waiting and no timeout needed |
| `TimeoutAnnotationTest` | `@Timeout` on tests and lifecycle methods, both thread modes |
| `ClassLevelTimeoutTest` | A timeout on the class, which skips lifecycle methods |
| `PollingTimeoutTest` | Polling until a condition holds |
| `TimeoutOnTemplatesTest` | Per repetition for `@RepeatedTest`; only the factory method for `@TestFactory` |
| `DefaultTimeoutExerciseTest` | A four-run exercise with the timeout configuration settings |

**Good to know**

- **Deliberate failures:** `HiddenCouplingDemoTest` and `TimeoutFailuresDemoTest` are tagged `failure-demo`, so the normal build skips them. Add `-DexcludedGroups=none` to run them.
- **Run time:** Module 5 takes about 6 seconds because the timeout tests use real time.
- **Maven profiles:** the 5.2 lab needs profiles in your `pom.xml`. I didn't edit the pom; `module5-pom-profiles.xml` in the zip has `fast`, `no-slow`, `security` and `integration` profiles to paste in.
- **Course outline:** the three Module 5 lab lines now describe these examples.
    
    mvn test -Pfast           only tests tagged "fast"
    mvn test -Pno-slow        everything except tests tagged "slow"
    mvn test -Psecurity       only tests tagged "security"
    mvn test -Pintegration    only tests tagged "integration"
                              (they still need APP_ENV=integration to run)