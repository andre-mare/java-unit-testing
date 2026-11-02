# Module 6: Dependency Injection

This module covers how tests get the objects they need: how JUnit passes values into constructors and methods, how to write your own parameter resolvers, and how to replace a class's collaborators with Mockito mocks or hand-written fakes.

## Table of Contents

- [Module 6: Dependency Injection](#module-6-dependency-injection)
  - [Table of Contents](#table-of-contents)
  - [The Domain: an OAuth 2.0 Token Service](#the-domain-an-oauth-20-token-service)
    - [Production classes](#production-classes)
    - [Test helpers](#test-helpers)
  - [Running the Module](#running-the-module)
  - [Sections](#sections)
    - [Section 1: Built-in Parameter Resolvers](#section-1-built-in-parameter-resolvers)
    - [Section 2: Custom Parameter Resolvers](#section-2-custom-parameter-resolvers)
    - [Section 3: Mocking with Mockito](#section-3-mocking-with-mockito)
  - [Deliberately Failing Tests](#deliberately-failing-tests)
    - [Why the failures are kept](#why-the-failures-are-kept)
    - [How they are excluded](#how-they-are-excluded)
    - [How to override the exclusion](#how-to-override-the-exclusion)
    - [What to expect](#what-to-expect)

## The Domain: an OAuth 2.0 Token Service

The main class under test is [`ClientCredentialsService`](src/main/java/com/example/auth/ClientCredentialsService.java), which issues access tokens for the OAuth 2.0 client credentials grant: an application presents its ID and secret, and receives a short-lived token.

The service receives every collaborator through its constructor, so a test can pass in real objects, fakes or mocks, which suits a module about dependency injection.

`issue` makes these checks in order, and writes every outcome to the audit log:

| Check | If it fails | Error code |
| --- | --- | --- |
| The client exists and the secret matches | Throws `TokenRequestException`. Both cases give the same error, so callers can't probe for client IDs | `invalid_client` |
| The client is enabled | Throws `TokenRequestException` | `unauthorized_client` |
| The requested scopes are allowed. Requesting no scopes grants all the allowed ones | Throws `TokenRequestException` | `invalid_scope` |

A token that passes these checks lasts five minutes.

### Production classes

| Type | Purpose |
| --- | --- |
| [`ClientCredentialsService`](src/main/java/com/example/auth/ClientCredentialsService.java) | Issues tokens and refuses invalid requests |
| [`AccessToken`](src/main/java/com/example/auth/AccessToken.java) | An issued token. Its `toString` never prints the token value |
| [`Client`](src/main/java/com/example/auth/Client.java) | A registered client, with a hash of its secret, its allowed scopes and whether it's enabled |
| [`TokenRequestException`](src/main/java/com/example/auth/TokenRequestException.java) | A refused request, carrying its OAuth error code |
| [`AuditEvent`](src/main/java/com/example/auth/AuditEvent.java) | A record of a token being issued or denied |
| [`ClientRepository`](src/main/java/com/example/auth/ClientRepository.java), [`InMemoryClientRepository`](src/main/java/com/example/auth/InMemoryClientRepository.java) | Stores registered clients, with an in-memory implementation |
| [`SecretVerifier`](src/main/java/com/example/auth/SecretVerifier.java), [`Sha256SecretVerifier`](src/main/java/com/example/auth/Sha256SecretVerifier.java) | Checks a secret against its hash, with a SHA-256 implementation |
| [`TokenGenerator`](src/main/java/com/example/auth/TokenGenerator.java), [`RandomTokenGenerator`](src/main/java/com/example/auth/RandomTokenGenerator.java) | Creates token values, with a secure random implementation |
| [`AuditLog`](src/main/java/com/example/auth/AuditLog.java) | Receives audit events |

### Test helpers

These types aren't test classes, so JUnit doesn't run them directly. The first three are in the [`support`](src/test/java/com/example/module_06/support) package, and the rest are the custom resolvers from Section 2.

| Type | Purpose |
| --- | --- |
| [`AuthFixtures`](src/test/java/com/example/module_06/support/AuthFixtures.java) | Shared test data: a fixed clock, a test secret and a way to create clients |
| [`RecordingAuditLog`](src/test/java/com/example/module_06/support/RecordingAuditLog.java) | A fake `AuditLog` that remembers every event |
| [`SequentialTokenGenerator`](src/test/java/com/example/module_06/support/SequentialTokenGenerator.java) | A fake `TokenGenerator` that returns `token-1`, `token-2` and so on |
| [`FixedClockResolver`](src/test/java/com/example/module_06/section_02/FixedClockResolver.java) | The simplest resolver: every `Clock` parameter gets the fixed clock |
| [`ClientFixtureExtension`](src/test/java/com/example/module_06/section_02/ClientFixtureExtension.java) | A resolver that builds and wires a repository, audit log, client and service for each test |
| [`@ClientWithScopes`](src/test/java/com/example/module_06/section_02/ClientWithScopes.java) | An annotation that configures the `Client` parameter resolved by `ClientFixtureExtension` |
| [`TokenServiceFixture`](src/test/java/com/example/module_06/section_02/TokenServiceFixture.java) | An extension that takes a constructor argument and has methods that tests can call |

## Running the Module

Run these commands from the repository root.

| Goal | Command | Result |
| --- | --- | --- |
| Run the module | `mvn test -pl module-06` | 45 tests pass; the three failure demos are excluded |
| Run one class | `mvn test -pl module-06 -Dtest=ClientFixtureExtensionTest` | 6 tests pass |
| Run the `auth` tag | `mvn test -pl module-06 -Dgroups=auth` | 4 tests pass |
| Run a failure demo | `mvn test -pl module-06 -Dtest=MockitoFailuresDemoTest -DexcludedGroups=none` | `BUILD FAILURE`, as intended |

Tests that use Mockito may print a warning that `Mockito is currently self-attaching`. It doesn't affect the results in this module, but Mockito warns that this won't work in future JDK releases.

## Sections

Each section has its own package under [`src/test/java/com/example/module_06`](src/test/java/com/example/module_06).

### Section 1: Built-in Parameter Resolvers

**What it tests:** the values JUnit can supply without any setup, using token generation and issuing as examples. Start with `TestInfoTest`.

| Class | What it tests |
| --- | --- |
| [`TestInfoTest`](src/test/java/com/example/module_06/section_01/TestInfoTest.java) | What `TestInfo` reports in a constructor, `@BeforeAll`, `@BeforeEach` and test methods, and using a test's name to create unique test data |
| [`RepetitionInfoTest`](src/test/java/com/example/module_06/section_01/RepetitionInfoTest.java) | The four values `RepetitionInfo` provides, while checking that generated tokens are unique and well formed. This produces 8 tests |
| [`TestReporterTest`](src/test/java/com/example/module_06/section_01/TestReporterTest.java) | Publishing single values, key-value pairs, a file and a directory, and that a token receipt leaves out the token value |
| [`ResolutionFailuresDemoTest`](src/test/java/com/example/module_06/section_01/ResolutionFailuresDemoTest.java) | Nothing passes: both tests [fail on purpose](#deliberately-failing-tests) because they ask for a parameter nothing can supply |

**JUnit concepts**

- **Parameter injection:** test constructors, lifecycle methods and test methods can declare parameters, and JUnit asks a `ParameterResolver` to supply each one. Without a resolver, the test fails before it starts.
- **`TestInfo`** describes what's running: its display name, tags, class and method. In `@BeforeAll`, it describes the class, so it has no test method. In a constructor or `@BeforeEach`, it describes the test about to run.
- **`RepetitionInfo`** describes a `@RepeatedTest`: the current repetition, the total, the failure count and the failure threshold. It's only available during a repeated test, including in its `@BeforeEach` and `@AfterEach` methods.
- **`TestReporter`** publishes extra information attached to the test it belongs to, which `System.out` can't do. `publishEntry` publishes values, and `publishFile` and `publishDirectory` publish files.
- **Report contents:** reports are often stored and shared, so never publish secrets.

Published files go to JUnit's reporting output directory. In this module's Maven build, that's `module-06/target/junit-jupiter`, in a folder for each test. You can change it with the `junit.platform.reporting.output.dir` setting. Maven's console output doesn't show values from `publishEntry`, so view those in an IDE or tool that displays report entries.

### Section 2: Custom Parameter Resolvers

**What it tests:** that custom resolvers supply the right objects, and that a test using them can focus on the behaviour of the token service. Start with `TypeBasedResolverTest`.

| Class | What it tests |
| --- | --- |
| [`TypeBasedResolverTest`](src/test/java/com/example/module_06/section_02/TypeBasedResolverTest.java) | That `FixedClockResolver` supplies the fixed clock to a constructor and to test methods, and that a token expires exactly at its expiry time |
| [`ClientFixtureExtensionTest`](src/test/java/com/example/module_06/section_02/ClientFixtureExtensionTest.java) | A ready-to-use service and client, clients configured by `@ClientWithScopes`, and that a test and its `@BeforeEach` share one set of objects while the next test starts empty |
| [`RegisterExtensionTest`](src/test/java/com/example/module_06/section_02/RegisterExtensionTest.java) | An extension created by the test class, with its own "now". Tests call its methods directly, and its state resets before each test |
| [`CompetingResolversDemoTest`](src/test/java/com/example/module_06/section_02/CompetingResolversDemoTest.java) | Nothing passes: its test [fails on purpose](#deliberately-failing-tests) because two resolvers claim the same parameter |

**JUnit concepts**

- **`ParameterResolver`** is the extension type behind injection. `supportsParameter` says whether it can supply a parameter, and `resolveParameter` supplies it.
- **`TypeBasedParameterResolver<T>`** is a shortcut for the common case: it supports exactly the type `T`, so you only write `resolveParameter`.
- **`@ExtendWith`** registers an extension for a class or method. Custom resolvers must always be registered. Only the built-in resolvers are active by default.
- **`@RegisterExtension`** registers an extension held in a field. The test class creates the instance, so it can pass constructor arguments and call its methods. A `static` field is shared by all the tests, and an instance field gets a new extension for each test.
- **`ExtensionContext`** gives an extension information about the current test. **`ExtensionContext.Store`** keeps values for that test, which is how `ClientFixtureExtension` gives all the parameters of one test the same instances. Constructor parameters resolve in a different context, so this extension is designed for method parameters.
- **`ParameterContext`** describes the parameter being resolved, including its position and annotations, so an annotation such as `@ClientWithScopes` can configure the value.
- **`BeforeEachCallback`** is a lifecycle callback, which `TokenServiceFixture` uses to reset its state before each test.
- **Competing resolvers:** JUnit doesn't choose between two resolvers for the same parameter. Exactly one must claim it, so make `supportsParameter` specific enough, for example by also requiring an annotation.

### Section 3: Mocking with Mockito

**What it tests:** the logic of `ClientCredentialsService` in isolation, by controlling its collaborators and checking how it uses them. Start with `ClientCredentialsServiceTest`.

| Class | What it tests |
| --- | --- |
| [`ClientCredentialsServiceTest`](src/test/java/com/example/module_06/section_03/ClientCredentialsServiceTest.java) | Every outcome of `issue` with all the collaborators mocked except the clock, including the order of the checks and a failing audit log |
| [`InjectMocksTest`](src/test/java/com/example/module_06/section_03/InjectMocksTest.java) | Letting Mockito build the service with `@InjectMocks`, which is why the `Clock` must be mocked here too |
| [`MockParameterInjectionTest`](src/test/java/com/example/module_06/section_03/MockParameterInjectionTest.java) | Mocks declared as test method parameters next to `TestInfo`, mixed with real objects |
| [`LenientStubbingTest`](src/test/java/com/example/module_06/section_03/LenientStubbingTest.java) | Stubs shared in `@BeforeEach`, which needs relaxed stubbing rules because one test doesn't use them |
| [`FakesInsteadOfMocksTest`](src/test/java/com/example/module_06/section_03/FakesInsteadOfMocksTest.java) | Similar scenarios with no Mockito, using working in-memory objects and checking the resulting state |
| [`MockitoFailuresDemoTest`](src/test/java/com/example/module_06/section_03/MockitoFailuresDemoTest.java) | Nothing passes: all three tests [fail on purpose](#deliberately-failing-tests) with a common Mockito error |

**JUnit and Mockito concepts**

- **`MockitoExtension`** is a JUnit extension. It creates the `@Mock` and `@Captor` fields before each test and checks the stubbing afterwards. It's also a parameter resolver, so `@Mock` works on test method parameters.
- **`@Mock`** creates a stand-in for a collaborator. A method that hasn't been stubbed returns an empty value, such as `Optional.empty()`.
- **`when(...).thenReturn(...)`** sets what a mock returns. **`doThrow(...).when(...)`** makes a `void` method throw.
- **`verify`** checks that a call happened. **`never()`** and **`verifyNoInteractions`** check that it didn't.
- **`any` and `argThat`** match arguments loosely or by a condition. **`@Captor` with `ArgumentCaptor`** captures an argument so you can inspect it.
- **`inOrder`** checks the order of calls across several mocks.
- **`@InjectMocks`** builds the object under test and passes in the mocks by type. An argument with no matching mock gets `null`. Calling the constructor yourself takes one more line, but keeps the wiring visible and checked by the compiler.
- **Strict stubs** are `MockitoExtension`'s default. They fail a test that has a stub it never uses, or that calls a stubbed method with different arguments.
- **`@MockitoSettings(strictness = Strictness.LENIENT)`** relaxes this for a class, and **`lenient()`** relaxes it for one stub. Prefer stubbing per test, because leniency hides stubs that no longer matter.
- **Fakes or mocks:** fakes are simple working implementations, so tests read like normal use and survive changes to how the service calls its collaborators. Mocks give exact control, including "never called" checks and failures such as a broken audit log. Use fakes where they're cheap, and mocks where you must observe or force an interaction.

**Try it:** remove `@MockitoSettings` from `LenientStubbingTest` and run the class. `unknownClient` fails, because it calls `clients.findById("ghost")` while the stub expects `"app"`, and strict stubs treat that as a probable mistake. Restore the annotation afterwards.

## Deliberately Failing Tests

In this module, three classes fail on purpose: [`ResolutionFailuresDemoTest`](src/test/java/com/example/module_06/section_01/ResolutionFailuresDemoTest.java), [`CompetingResolversDemoTest`](src/test/java/com/example/module_06/section_02/CompetingResolversDemoTest.java) and [`MockitoFailuresDemoTest`](src/test/java/com/example/module_06/section_03/MockitoFailuresDemoTest.java). Other modules have similar classes, listed in the [root README](../README.md#deliberately-failing-tests).

### Why the failures are kept

The point of these classes is to show you real failure output, so you can recognize these injection and Mockito errors when your own tests produce them. A class can't show a failure without failing, so its tests can't simply be corrected.

Left in the normal build, though, they would make every `mvn test` end in `BUILD FAILURE`, and a real failure would be hard to spot. `@Disabled` wouldn't help, because the tests would never run and there would be no output to read. A tag solves both problems: the tests stay out of the normal build, but can still be run when you ask for them.

### How they are excluded

Two pieces work together:

1. Each class is labelled with a tag:

   ```java
   @Tag("expected-failure-tests")
   class ResolutionFailuresDemoTest {
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
| Run the resolution failure demo | `mvn test -pl module-06 -Dtest=ResolutionFailuresDemoTest -DexcludedGroups=none` |
| Run the competing resolvers demo | `mvn test -pl module-06 -Dtest=CompetingResolversDemoTest -DexcludedGroups=none` |
| Run the Mockito failure demo | `mvn test -pl module-06 -Dtest=MockitoFailuresDemoTest -DexcludedGroups=none` |
| Run the whole module, including the demos | `mvn test -pl module-06 -DexcludedGroups=none` |
| Run only the tests with this tag | `mvn test -pl module-06 -Dgroups=expected-failure-tests -DexcludedGroups=none` |
| Run a demo without failing the build | `mvn test -pl module-06 -Dtest=MockitoFailuresDemoTest -DexcludedGroups=none -Dmaven.test.failure.ignore=true` |

The exclusion has to be cleared even when you select the class or tag by name. These two commands report `BUILD SUCCESS` with `Tests run: 0`, because the exclusion still applies:

```bash
mvn test -pl module-06 -Dtest=MockitoFailuresDemoTest
mvn test -pl module-06 -Dgroups=expected-failure-tests
```

The exclusion is part of the Maven build, so running the class directly from your IDE normally ignores it, unless the IDE is set to run tests through Maven.

Keep the override on the command line. Removing the tags or the POM property would make every normal build fail.

### What to expect

Each demo ends in `BUILD FAILURE`, which is the intended result.

`ResolutionFailuresDemoTest` ends with this summary:

```text
Tests run: 2, Failures: 0, Errors: 2, Skipped: 0
```

| Test | What it shows | Failure message |
| --- | --- | --- |
| `repetitionInfoOutsideARepeatedTest` | `RepetitionInfo` is only available in a repeated test | `No ParameterResolver registered for parameter [org.junit.jupiter.api.RepetitionInfo info]...` |
| `noResolverForAString` | No built-in resolver supplies a `String` | `No ParameterResolver registered for parameter [java.lang.String clientId]...` |

`CompetingResolversDemoTest` ends with this summary:

```text
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0
```

| Test | What it shows | Failure message |
| --- | --- | --- |
| `whichClock` | Two resolvers claim the same `Clock` parameter | `Discovered multiple competing ParameterResolvers for parameter [java.time.Clock clock]...` |

`MockitoFailuresDemoTest` ends with this summary:

```text
Tests run: 3, Failures: 1, Errors: 2, Skipped: 0
```

| Test | What it shows | Counted by Maven as | Failure message |
| --- | --- | --- | --- |
| `unnecessaryStubbing` | A stub the test never uses | Error | `UnnecessaryStubbingException`: `Unnecessary stubbings detected.` |
| `wantedButNotInvoked` | `verify` expecting a call that never happened | Failure | `Wanted but not invoked: tokenGenerator.newTokenValue();` |
| `stubbedWithDifferentArguments` | A stubbed method called with different arguments | Error | `PotentialStubbingProblem`: `Strict stubbing argument mismatch.` |

In all three classes, Maven counts a failed assertion or `verify` as a failure and any other exception as an error. A parameter that can't be resolved stops the test before it starts, so the test body never runs.
