# Module 2: Assertions, Assumptions and Exceptions

This module covers how a test decides its result: how to check values with assertions, how to stop a test that can't run in the current environment with assumptions, and how to test code that should or shouldn't throw an exception.

## Table of Contents

- [Module 2: Assertions, Assumptions and Exceptions](#module-2-assertions-assumptions-and-exceptions)
  - [Table of Contents](#table-of-contents)
  - [The Domain: a Bank Account](#the-domain-a-bank-account)
    - [Production classes](#production-classes)
    - [Test helpers](#test-helpers)
  - [Running the Module](#running-the-module)
  - [Sections](#sections)
    - [Section 1: Assertions](#section-1-assertions)
    - [Section 2: Assumptions](#section-2-assumptions)
    - [Section 3: Exception Handling](#section-3-exception-handling)
  - [Deliberately Failing Tests](#deliberately-failing-tests)
    - [Why the failures are kept](#why-the-failures-are-kept)
    - [How they are excluded](#how-they-are-excluded)
    - [How to override the exclusion](#how-to-override-the-exclusion)
    - [What to expect](#what-to-expect)

## The Domain: a Bank Account

The main class under test is [`BankAccount`](src/main/java/com/example/bank/BankAccount.java). It holds an IBAN, an owner, a balance, a status and a list of transactions. Each of its rules gives the assertions in this module something real to check.

| Behaviour | Rule | If the rule is broken |
| --- | --- | --- |
| Opening an account | The IBAN and owner must not be blank, and the opening balance must not be negative | Throws `IllegalArgumentException`, or `NullPointerException` for a `null` value |
| `getBalance` | Always returns scale 2, so `100` is stored as `100.00` | Not applicable |
| `deposit`, `withdraw` | The account must be active and the amount must be positive. Both methods return the account, so calls can be chained | Throws `AccountNotActiveException` or `IllegalArgumentException`, or `NullPointerException` for a `null` amount |
| `withdraw` | The amount must not exceed the balance | Throws `InsufficientFundsException` |
| `freeze`, `unfreeze` | A closed account can't be frozen, and only a frozen account can be unfrozen | Throws `AccountNotActiveException` or `IllegalStateException` |
| `close` | The balance must be exactly zero | Throws `IllegalStateException` |
| `statement` | Returns the account history as lines of text | Not applicable |

### Production classes

| Class | Purpose |
| --- | --- |
| [`BankAccount`](src/main/java/com/example/bank/BankAccount.java) | The account and its rules |
| [`AccountStatus`](src/main/java/com/example/bank/AccountStatus.java) | `ACTIVE`, `FROZEN` or `CLOSED`. Only an active account accepts deposits and withdrawals |
| [`Transaction`](src/main/java/com/example/bank/Transaction.java) | A record of one deposit or withdrawal: its type, its amount and the balance afterwards |
| [`AccountException`](src/main/java/com/example/bank/AccountException.java) | The abstract parent of the two account exceptions below |
| [`InsufficientFundsException`](src/main/java/com/example/bank/InsufficientFundsException.java) | Reports the balance, the requested amount and the shortfall |
| [`AccountNotActiveException`](src/main/java/com/example/bank/AccountNotActiveException.java) | Reports the status that blocked the operation |

### Test helpers

These classes are in the [`support`](src/test/java/com/example/module_02/support) package. They aren't test classes, so JUnit doesn't run them.

| Class | Purpose |
| --- | --- |
| [`TestAccounts`](src/test/java/com/example/module_02/support/TestAccounts.java) | Creates test data, such as `withHundred()` for an account holding 100.00 and `eur("50")` for an amount |
| [`MoneyAssertions`](src/test/java/com/example/module_02/support/MoneyAssertions.java) | Provides `assertAmountEquals`, a custom assertion that compares amounts and ignores scale |
| [`BasicArithmetic`](src/test/java/com/example/module_02/support/BasicArithmetic.java) | A copy of Module 1's calculator, used to test its error paths, because modules share no code |

## Running the Module

Run these commands from the repository root. The results are for macOS and Linux, with `ENV` not set. On Windows, `notOnWindows` is also aborted.

| Goal | Command | Result |
| --- | --- | --- |
| Run the module | `mvn test -pl module-02` | 42 tests: 39 pass and 3 are skipped. The two failure demos are excluded |
| Run one class | `mvn test -pl module-02 -Dtest=BigDecimalAssertionsTest` | 3 tests pass |
| Run the assumptions as a CI server would | `ENV=CI mvn test -pl module-02 -Dtest=AssumptionsTest` | 5 tests: 4 pass and 1 is skipped |
| Run the class-level assumption tests | `mvn test -pl module-02 -Dtest=ClassLevelAssumptionTest -Dbank.sandbox=on` | 2 tests pass |
| Run a failure demo | `mvn test -pl module-02 -Dtest=AssertionFailuresDemoTest -DexcludedGroups=none` | `BUILD FAILURE`, as intended |

## Sections

Each section has its own package under [`src/test/java/com/example/module_02`](src/test/java/com/example/module_02).

### Section 1: Assertions

**What it tests:** the state of a `BankAccount` after deposits and withdrawals, using each kind of assertion that JUnit provides, along with AssertJ as an alternative. Start with `BasicAssertionsTest`.

| Class | What it tests |
| --- | --- |
| [`BasicAssertionsTest`](src/test/java/com/example/module_02/section_01/BasicAssertionsTest.java) | Single-value checks on a new account: its status, owner, IBAN and transactions, and that `deposit` returns the same object |
| [`BigDecimalAssertionsTest`](src/test/java/com/example/module_02/section_01/BigDecimalAssertionsTest.java) | That `BigDecimal.equals` treats `10.0` and `10.00` as different, that the balance is always scale 2, and that `assertAmountEquals` ignores scale |
| [`FailureMessagesTest`](src/test/java/com/example/module_02/section_01/FailureMessagesTest.java) | A failure message given as a `String`, and one given as a `Supplier` that is never called while the assertion passes |
| [`GroupedAssertionsTest`](src/test/java/com/example/module_02/section_01/GroupedAssertionsTest.java) | Several properties of an account checked as one group, including nested groups that depend on an earlier check |
| [`CollectionAssertionsTest`](src/test/java/com/example/module_02/section_01/CollectionAssertionsTest.java) | Transaction amounts as an array, transaction types as a list, and statement lines matched exactly and by regular expression |
| [`TimeoutAssertionsTest`](src/test/java/com/example/module_02/section_01/TimeoutAssertionsTest.java) | That account operations finish in time, that a timeout assertion returns the result, and that a preemptive timeout runs code on a different thread |
| [`AssertJAssertionsTest`](src/test/java/com/example/module_02/section_01/AssertJAssertionsTest.java) | Similar value, collection and exception checks, written with AssertJ |
| [`AssertionFailuresDemoTest`](src/test/java/com/example/module_02/section_01/AssertionFailuresDemoTest.java) | Nothing passes: all nine tests [fail on purpose](#deliberately-failing-tests) so you can read each failure message |

**JUnit concepts**

- **`assertEquals` and `assertNotEquals`** compare two values using `equals`.
- **`assertTrue` and `assertFalse`** check a condition.
- **`assertNull` and `assertNotNull`** check whether a value is `null`.
- **`assertSame` and `assertNotSame`** check whether two references point to the same object, not merely equal ones.
- **`assertInstanceOf`** checks a value's type and returns it cast to that type.
- **`fail()`** fails the test directly, which is useful in a branch that should never be reached.
- **`assertArrayEquals` and `assertIterableEquals`** compare collections element by element, in order.
- **`assertLinesMatch`** compares lists of text lines. Each expected line is tried as an exact match and then as a regular expression, and a marker such as `>> transactions >>` skips lines until the next match.
- **`assertAll`** runs every assertion in a group and reports all the failures together. Without it, the first failure stops the test and hides the rest.
- **Failure messages** are the last argument of an assertion. A `Supplier<String>` is only called if the assertion fails, so an expensive message costs nothing while the test passes.
- **`assertTimeout`** runs the code on the same thread, waits for it to finish, and then fails if it took too long.
- **`assertTimeoutPreemptively`** runs the code on a separate thread and stops it when the time is up. Anything tied to the test thread, such as a `ThreadLocal`, isn't visible to that code.
- **Custom assertions**, such as `assertAmountEquals`, wrap a check in a named method so tests stay readable and failure messages stay clear.
- **Third-party assertion libraries:** JUnit works with libraries such as AssertJ, whose fluent `assertThat` style chains checks together. Its `isEqualByComparingTo` also ignores `BigDecimal` scale.

### Section 2: Assumptions

**What it tests:** that a test stops without failing when its environment isn't suitable. The results of these classes change depending on where and how you run them.

| Class | What it tests |
| --- | --- |
| [`AssumptionsTest`](src/test/java/com/example/module_02/section_02/AssumptionsTest.java) | Five ways to make a test depend on the environment, using the `ENV` variable, the operating system and the `bank.export.dir` property |
| [`ClassLevelAssumptionTest`](src/test/java/com/example/module_02/section_02/ClassLevelAssumptionTest.java) | That an assumption in `@BeforeAll` stops the whole class unless `bank.sandbox` is set to `on` |

Each test in `AssumptionsTest` needs something different to run in full:

| Test | Assumption | Runs in full when |
| --- | --- | --- |
| `onlyOnCiServer` | `assumeTrue` | `ENV=CI` is set |
| `notOnDeveloperWorkstation` | `assumeTrue` with a `Supplier` message | `ENV=CI` is set |
| `notOnWindows` | `assumeFalse` | The operating system isn't Windows |
| `inAllEnvironmentsWithExtraChecksOnCi` | `assumingThat` | Always runs, and its extra check runs only when `ENV=CI` is set |
| `abortExplicitly` | `abort` | `-Dbank.export.dir=<folder>` is set |

`ClassLevelAssumptionTest` is reported differently from a test that aborts on its own. Without the property, Maven shows `Tests run: 0` for the class rather than two skipped tests, because the assumption stops the class before either test starts.

**JUnit concepts**

- **Assumptions and assertions:** a failed assertion means the code is wrong, so the test fails. A failed assumption means the test can't run meaningfully here, so it is aborted. It throws `TestAbortedException`, and Maven counts the test as skipped.
- **`assumeTrue` and `assumeFalse`** abort the test if the condition isn't met.
- **`assumingThat`** runs a block of code only if the condition is met, and the rest of the test always runs. A failed assertion inside the block still fails the test.
- **`abort()`** aborts the test directly, for when your own code has already made the decision.
- **Assumptions in `@BeforeAll`** apply to the whole class. Module 5 covers declarative alternatives such as `@EnabledIfSystemProperty`.

### Section 3: Exception Handling

**What it tests:** the error paths of `BankAccount`, and that a failed operation leaves the account unchanged.

| Class | What it tests |
| --- | --- |
| [`ExpectedExceptionsTest`](src/test/java/com/example/module_02/section_03/ExpectedExceptionsTest.java) | Withdrawing too much, using a frozen account, invalid amounts, invalid construction and closing an account that holds money, as well as the `BasicArithmetic` error paths. It also checks exception messages and fields |
| [`NoExceptionExpectedTest`](src/test/java/com/example/module_02/section_03/NoExceptionExpectedTest.java) | Operations that must succeed: withdrawing the exact balance, depositing, and freezing then unfreezing. It also shows that a `throws` clause isn't an expectation |
| [`ExceptionFailuresDemoTest`](src/test/java/com/example/module_02/section_03/ExceptionFailuresDemoTest.java) | Nothing passes: all five tests [fail on purpose](#deliberately-failing-tests) to show each way an exception check can fail |

**JUnit concepts**

- **`assertThrows(Type, code)`** passes if the code throws that type or one of its subtypes, and returns the exception so you can check its message and fields.
- **`assertThrowsExactly(Type, code)`** passes only for exactly that type. A subtype fails the test.
- **`assertDoesNotThrow(code)`** fails if the code throws, and can return the code's result. Any uncaught exception would fail the test anyway, so its main value is stating the intent.
- **A `throws` clause on a test** only satisfies the compiler. It doesn't mean the test expects that exception, and the test passes if nothing is thrown.
- **Checking the state afterwards:** after a rejected operation, also assert that nothing changed. `withdrawMoreThanBalance` does this by checking the balance and transactions.

## Deliberately Failing Tests

In this module, two classes fail on purpose: [`AssertionFailuresDemoTest`](src/test/java/com/example/module_02/section_01/AssertionFailuresDemoTest.java) and [`ExceptionFailuresDemoTest`](src/test/java/com/example/module_02/section_03/ExceptionFailuresDemoTest.java). Other modules have similar classes, listed in the [root README](../README.md#deliberately-failing-tests).

### Why the failures are kept

The point of these classes is to show you real failure output, so you can recognize a message when your own test produces it. A class can't show a failure without failing, so its tests can't simply be corrected.

Left in the normal build, though, they would make every `mvn test` end in `BUILD FAILURE`, and a real failure would be hard to spot. `@Disabled` wouldn't help, because the tests would never run and there would be no output to read. A tag solves both problems: the tests stay out of the normal build, but can still be run when you ask for them.

### How they are excluded

Two pieces work together:

1. Each class is labelled with a tag:

   ```java
   @Tag("expected-failure-tests")
   class AssertionFailuresDemoTest {
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
| Run the assertion failure demo | `mvn test -pl module-02 -Dtest=AssertionFailuresDemoTest -DexcludedGroups=none` |
| Run the exception failure demo | `mvn test -pl module-02 -Dtest=ExceptionFailuresDemoTest -DexcludedGroups=none` |
| Run the whole module, including both demos | `mvn test -pl module-02 -DexcludedGroups=none` |
| Run only the tests with this tag | `mvn test -pl module-02 -Dgroups=expected-failure-tests -DexcludedGroups=none` |
| Run a demo without failing the build | `mvn test -pl module-02 -Dtest=ExceptionFailuresDemoTest -DexcludedGroups=none -Dmaven.test.failure.ignore=true` |

The exclusion has to be cleared even when you select the class or tag by name. These two commands report `BUILD SUCCESS` with `Tests run: 0`, because the exclusion still applies:

```bash
mvn test -pl module-02 -Dtest=AssertionFailuresDemoTest
mvn test -pl module-02 -Dgroups=expected-failure-tests
```

The exclusion is part of the Maven build, so running the class directly from your IDE normally ignores it, unless the IDE is set to run tests through Maven.

Keep the override on the command line. Removing the tags or the POM property would make every normal build fail.

### What to expect

Each demo ends in `BUILD FAILURE`, which is the intended result. Before you run one, try to predict each message.

`AssertionFailuresDemoTest` ends with this summary:

```text
Tests run: 9, Failures: 9, Errors: 0, Skipped: 0
```

| Test | What it shows | Failure message |
| --- | --- | --- |
| `plainMismatch` | A wrong expected value | `expected: <150.00> but was: <100.00>` |
| `scaleTrap` | Two `BigDecimal` values that look equal but have different scales | `expected: <100> but was: <100.00>` |
| `customMoneyAssertion` | The message from `assertAmountEquals` | `amounts differ ==> expected: <99.99> but was: <100.00>` |
| `lazyMessageIsBuiltOnFailure` | A `Supplier` message, built because the assertion failed | `expected a negative balance; statement:`, followed by the statement lines |
| `groupedFailuresAreAllReported` | `assertAll` reporting more than one failure | `account (2 failures)`, followed by each failure |
| `iterableMismatchShowsTheIndex` | The position of the first difference | `iterable contents differ at index [1], expected: <b> but was: <x>` |
| `linesMismatch` | The line that didn't match | `expected line #2 doesn't match actual line #2` |
| `timeoutExceededAfterCompletion` | `assertTimeout` waiting for the code to finish | `execution exceeded timeout of 10 ms by`, followed by the overrun, which varies |
| `timeoutExceededPreemptively` | `assertTimeoutPreemptively` stopping code that would otherwise wait forever | `execution timed out after 10 ms` |

`ExceptionFailuresDemoTest` ends with this summary:

```text
Tests run: 5, Failures: 4, Errors: 1, Skipped: 0
```

| Test | What it shows | Counted by Maven as | Failure message |
| --- | --- | --- | --- |
| `uncaughtExceptionFailsTheTest` | An exception that nothing catches | Error | `Insufficient funds on NL91ABNA0417164300: balance 100.00, requested 500.00` |
| `nothingWasThrown` | `assertThrows` when the code succeeds | Failure | `Expected com.example.bank.AccountException to be thrown, but nothing was thrown.` |
| `wrongTypeWasThrown` | `assertThrows` with an unrelated type | Failure | `Unexpected exception type thrown, expected: <...AccountNotActiveException> but was: <...InsufficientFundsException>` |
| `exactlyRejectsASubtype` | `assertThrowsExactly` rejecting a subtype | Failure | `Unexpected exception type thrown, expected: <...AccountException> but was: <...InsufficientFundsException>` |
| `assertDoesNotThrowReportsTheException` | `assertDoesNotThrow` when the code throws | Failure | `Unexpected exception thrown:`, followed by the exception |

The first and last tests in this table throw the same exception but are counted differently. Maven reports a failed assertion as a failure and an unexpected exception as an error. `assertDoesNotThrow` turns the exception into a failed assertion, so it counts as a failure. JUnit treats both as failed.
