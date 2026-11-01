I'll write the Module 2 production classes, the tests in one package per submodule, and the `pom.xml` changes. First I'll check the current AssertJ version, since 2.1 includes third-party assertions.

The newest AssertJ version I can find is 3.27.7. I'll use that, and you can bump it if Maven reports something newer. Now the production classes.

Now the shared test helpers, then the 2.1 assertion tests.

Now 2.2 (assumptions) and 2.3 (exception handling).

Now the `package-info` files, the shared `failure-demo` tag (I'm renaming Module 1's `outcomes-demo` tag to it so one setting excludes every deliberately failing test), and the AssertJ dependency.

I can't run JUnit here, but I'll compile the new production classes and check the outputs the tests rely on (statement lines, messages, scale).

Every value the tests expect matches what the production code produces. Packaging now.

Module 2 is in `junit6-modules-1-2.zip`, which contains the whole project so far, with Module 1 included. I compiled the new production classes and checked every value the tests expect: statement lines, exception messages, scales and regexes. I couldn't run JUnit itself because Maven Central is blocked here, so please run `mvn test` locally.

**Production classes** (`src/main/java/com/example/bank`)

| Class | Purpose |
| --- | --- |
| `BankAccount` | IBAN, owner, `BigDecimal` balance at scale 2, status, transaction history; `deposit`/`withdraw` (chainable), `freeze`, `unfreeze`, `close`, `statement()` |
| `AccountStatus` | `ACTIVE`, `FROZEN`, `CLOSED` |
| `Transaction` | Record: type, amount, balance after |
| `AccountException` | Abstract base, so you can compare `assertThrows` with `assertThrowsExactly` |
| `InsufficientFundsException` | Balance, requested amount, shortfall |
| `AccountNotActiveException` | The blocking status |

**Shared test helpers** (`src/test/java/com/example/testsupport`)

- **`TestAccounts`** creates test data: `withHundred()`, `empty()`, `eur("50")`.
- **`MoneyAssertions.assertAmountEquals`** is the custom assertion the 2.1 lab asks for.

**Test classes** (`com.example.m02_assertions`)

| Package | Classes, in reading order |
| --- | --- |
| `s01_assertions` | `BasicAssertionsTest`, `BigDecimalAssertionsTest`, `FailureMessagesTest`, `GroupedAssertionsTest`, `CollectionAssertionsTest`, `TimeoutAssertionsTest`, `AssertJAssertionsTest`, `AssertionFailuresDemoTest` |
| `s02_assumptions` | `AssumptionsTest`, `ClassLevelAssumptionTest` |
| `s03_exception_handling` | `ExpectedExceptionsTest`, `NoExceptionExpectedTest`, `ExceptionFailuresDemoTest` |

A few tests are worth looking at first:

- **`BigDecimalAssertionsTest`** shows why `assertEquals(new BigDecimal("10.0"), new BigDecimal("10.00"))` fails.
- **`TimeoutAssertionsTest`** uses a `ThreadLocal` to show that `assertTimeoutPreemptively` runs your code on a different thread.
- **`AssumptionsTest`** behaves differently depending on how you run it. Try it plain, then with `ENV=CI mvn test -Dtest=AssumptionsTest`.

**Changes to existing files**

- **`failure-demo` tag:** Module 1's `outcomes-demo` tag is now `failure-demo`, so one `excludedGroups` setting keeps every deliberately failing class out of the normal build. To run those classes, add `-DexcludedGroups=none`.
- **AssertJ:** `pom.xml` now has `assertj-core` 3.27.7 as a test dependency, the newest version I could find (from [ecosyste.ms](https://dependabot.ecosyste.ms/packages/maven/org.assertj:assertj-core)). Your comment in the course outline asking whether to keep AssertJ is still unanswered. If you'd rather stay pure JUnit, delete `AssertJAssertionsTest` and that dependency; nothing else uses it.