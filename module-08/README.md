I'll start by pulling Module 8's submodules from the course outline, then build the domain and tests.

Module 8 is test templates, class templates and dynamic tests. Let me confirm the JUnit 6.1 APIs for these, especially the newer per-node execution mode.

Writing the production code: currency conversion (8.1), PSD2-style consents with two repositories (8.2), and primes plus OAuth scopes (8.3).

Now the Module 8 test support classes and the 8.1 test templates.

Now 8.2, class templates running the consent suite against both repositories.

Now 8.3, dynamic tests, plus the scope test-vector file.

Adding package-info files, then running the checks.

`RepositoryUnderTest` matches Surefire's `*Test` pattern, so I'll rename it to `RepositoryFixture`.

Every production-code expectation passes. Now a syntax pass over the test sources: without JUnit on hand, only missing-symbol errors are acceptable.

Updating the outline's 8.2 lab line, which still names the old `TransferServiceTest`.

I've built Module 8 (test templates, class templates and dynamic tests) and sent it as **junit6-module8.zip**. It has 45 files and shares no code with Modules 1–7. The tests haven't been run, because JUnit's download site is still blocked from this workspace. I ran every expected value in the tests against the production code in plain Java, and all matched.

**Production code (new for Module 8)**
- `com.example.fx.CurrencyConverter` converts between EUR, USD, GBP, CHF and JPY. It uses fixed rates and rounds half-even to each currency's decimals (none for JPY).
- `com.example.consent` covers PSD2-style account-access consents:
  - `Consent` and `ConsentStatus` hold the consent and its status.
  - `ConsentService` handles request, authorise, reject, revoke and expiry, with a maximum validity of 180 days.
  - The `ConsentRepository` interface has two implementations: `InMemoryConsentRepository` and `FileConsentRepository`.
- `com.example.primes.Primes` has `isPrime`, a sieve (`upTo`) and `factorize`.
- `com.example.scope.ScopeSet` parses an OAuth 2.0 scope string strictly, following RFC 6749 §3.3.

**Tests per submodule**

| Package | Classes | What it shows |
|---|---|---|
| `s01_test_templates` | `FirstTestTemplateTest`, `CurrencyConverterTemplateTest` (lab), `BuiltInTemplatesTest`, `TemplateFailureDemoTest`*, plus `@ForEachCurrency` and `CurrencyInvocationContextProvider` | A home-made `@RepeatedTest` that runs each test once per currency. It covers display names, a per-invocation parameter resolver, and `mayReturnZero…`. It also proves `@RepeatedTest` and `@ParameterizedTest` are test templates. |
| `s02_class_templates` | `ConsentServiceTest` (lab), `FirstClassTemplateTest`, plus `ConsentRepositoryProvider`, `RepositoryAware` and `RepositoryFixture` | The whole consent suite, including a `@Nested` class, runs as `[1] in-memory` and `[2] file-backed`. Temp folders are cleaned up through the extension store. `FirstClassTemplateTest` checks its own lifecycle order and proves `@ParameterizedClass` is a class template. |
| `s03_dynamic_tests` | `FirstDynamicTestsTest`, `PrimeContainersTest` (lab), `DynamicTestStreamTest`, `DynamicLifecycleTest`, `ScopeVectorsTest`, `DynamicExecutionModeTest`, `DynamicFailureDemoTest`* | Every factory return type. The numbers 1–100 are split into `primes (25)` and `non-primes (75)`. It covers the `DynamicTest.stream` variants (`Named`, `NamedExecutable`) and proves that `@BeforeEach` runs once per factory. It also has tests built from a file, using `classpath:`, `method:` and `class:` test sources, and the JUnit 6.1 per-node execution mode builder. |

\* Tagged `failure-demo`, so normal builds skip them. Run them with `mvn test -DexcludedGroups=none -Dtest=<ClassName>`.

The shared helpers are in `support` (`MutableClock`, `ConsentFixtures`). The 15 OAuth scope test cases are in `src/test/resources/scope-vectors.txt`; add one by adding a line there, with no Java change. The pom needs no changes. I renamed one helper to `RepositoryFixture` because `RepositoryUnderTest` matched Surefire's `*Test` name pattern.

**Check these first when you run it**
- **`DynamicExecutionModeTest`:** it uses the configuration-callback builder (`dynamicTest(config -> …)`), which is new and experimental in 6.1. I wrote it from the 6.1 API docs, so it is the class most likely to need a small fix.
- **`ConsentServiceTest`:** for the `@Nested` tests, the provider must also set up the outer test instance. JUnit's docs don't say it does; I'm assuming it. If those tests fail with a null `storage`, that's the cause.
- **Lifecycle order:** `FirstClassTemplateTest` assumes `@BeforeAll` and `@AfterAll` run once around both runs of the class, the same as for parameterized classes.

I also updated the 8.2 and 8.3 lab lines in the course outline.

Sources:
- [Dynamic Tests (JUnit 6.1.2 user guide)](https://docs.junit.org/6.1.2/writing-tests/dynamic-tests.html)
- [DynamicContainer.Configuration (API 6.1.1)](https://docs.junit.org/6.1.1/api/org.junit.jupiter.api/org/junit/jupiter/api/DynamicContainer.Configuration.html)
- [Providing Invocation Contexts for Class Templates (6.1.2)](https://docs.junit.org/6.1.2/extensions/providing-invocation-contexts-for-class-templates.html)