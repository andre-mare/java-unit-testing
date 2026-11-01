Module 6 needs Mockito for 6.3, so I'm checking its current version. I'm also confirming two JUnit 6 APIs the tests use: where `MediaType` lives for `TestReporter.publishFile`, and what `TestReporter` needs for file output.

Now the Module 6 support classes: two hand-written fakes and the shared test data.

Now 6.1, the built-in resolvers.

Now 6.2, custom parameter resolvers.

Now 6.3, mocking with Mockito.

Next: `package-info` files and a `pom.xml` snippet for Mockito. Then I'll compile the production classes and exercise every path of the token service.

All 15 checks pass. I'll add a `serialVersionUID` to clear the one compiler warning, then update the Module 6 lab lines in the outline, which still describe the old banking examples.

The outline is updated. Packaging only the Module 6 files, plus the Mockito snippet:

Module 6 is in `junit6-module6.zip`, with no Module 1–5 code and no `pom.xml`. Unzip it into the folder that contains `unit-testing-examples`. Then copy the Mockito dependency from `module6-pom-dependencies.xml` (in the zip) into your `pom.xml`, or 6.3 won't compile.

I compiled the production classes and checked every path through the token service; all 15 checks passed. I couldn't compile the test classes or run JUnit here because Maven Central is blocked, so please run `mvn test` on your machine.

**Production code** (`com.example.auth`, new)

The domain is an OAuth 2.0 client-credentials token service, since that's close to your IAM work.

- **`ClientCredentialsService`** issues `AccessToken`s, refuses requests with `invalid_client`, `unauthorized_client` or `invalid_scope`, and writes every outcome to an `AuditLog`.
- **Collaborators:** `ClientRepository` (plus an in-memory version), `SecretVerifier` (plus a SHA-256 version), `TokenGenerator` (plus a random version) and `AuditLog`. All four are injected through the constructor, so tests can pass real objects, fakes or mocks.

**Shared within Module 6** (`support`)

- **`AuthFixtures`:** test data, including a fixed clock and the test secret.
- **Fakes:** `RecordingAuditLog` and `SequentialTokenGenerator`.

**6.1 `s01_builtin_resolvers`**

| Class | Covers |
| --- | --- |
| `TestInfoTest` | `TestInfo` in the constructor, `@BeforeAll`, `@BeforeEach` and tests. The lab logs each test's name and tags. |
| `RepetitionInfoTest` | All four `RepetitionInfo` values |
| `TestReporterTest` | Report entries, files and directories. The lab publishes a token receipt and checks the token value never reaches the report. |
| `ResolutionFailuresDemoTest` | Fails on purpose: parameters nothing can resolve |

**6.2 `s02_custom_resolvers`**

| Class | Covers |
| --- | --- |
| `FixedClockResolver` + `TypeBasedResolverTest` | The simplest resolver, registered with `@ExtendWith`, used in a constructor |
| `ClientFixtureExtension` + `ClientWithScopes` + `ClientFixtureExtensionTest` | The lab: injects a registered client, configured by annotation, plus a fully wired service. One set per test, shared with `@BeforeEach`, via `ExtensionContext.Store`. |
| `TokenServiceFixture` + `RegisterExtensionTest` | `@RegisterExtension`: an extension the test configures and calls directly |
| `CompetingResolversDemoTest` | Fails on purpose: two resolvers for the same parameter |

**6.3 `s03_mocking`**

| Class | Covers |
| --- | --- |
| `ClientCredentialsServiceTest` | The lab: `@Mock`, `when`, `verify`, `never`, `ArgumentCaptor`, `argThat`, `InOrder`, `doThrow` |
| `InjectMocksTest` | `@InjectMocks`, and why it forces even the `Clock` to be a mock |
| `MockParameterInjectionTest` | `@Mock` as a test-method parameter (MockitoExtension is also a parameter resolver) |
| `LenientStubbingTest` | Shared stubs in `@BeforeEach` versus strict stubs |
| `FakesInsteadOfMocksTest` | The same scenarios with fakes, and when to choose which |
| `MockitoFailuresDemoTest` | Fails on purpose: the three most common Mockito errors |

The three demo classes that fail on purpose are tagged `failure-demo`; add `-DexcludedGroups=none` to run them. I also updated the three Module 6 lab lines in the course outline.

**If something fails, check these first.** They come from documentation rather than a test run:

- **`TestReporterTest`:** the file and directory tests need JUnit's reporting output directory, which Surefire 3.5.5 should provide.
- **Mockito:** 5.24.0 is the current version on Maven Central. I'm assuming its `MockitoExtension` works with JUnit 6.1.
- **Java 21+ warning:** Mockito may print a "self-attaching" warning. It's harmless; the snippet shows how to remove it.

Sources:
- [mockito-junit-jupiter on Maven Central](https://central.sonatype.com/artifact/org.mockito/mockito-junit-jupiter)
- [TestReporter API, JUnit 6.1](https://docs.junit.org/6.1.1/api/org.junit.jupiter.api/org/junit/jupiter/api/TestReporter.html)
- [ExtensionContext.Store API, JUnit 6.1](https://docs.junit.org/6.1.2/api/org.junit.jupiter.api/org/junit/jupiter/api/extension/ExtensionContext.Store.html)