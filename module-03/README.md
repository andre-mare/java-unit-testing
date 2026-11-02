# Module 3: Readable and Skippable Tests

This module covers how tests appear in reports and how to switch them off responsibly: how to give tests readable names, and how to disable a test while leaving a clear record of why.

## Table of Contents

- [Module 3: Readable and Skippable Tests](#module-3-readable-and-skippable-tests)
  - [Table of Contents](#table-of-contents)
  - [The Domain: a Password Policy](#the-domain-a-password-policy)
    - [Production classes](#production-classes)
    - [Test helpers](#test-helpers)
  - [Running the Module](#running-the-module)
  - [Sections](#sections)
    - [Section 1: Display Names](#section-1-display-names)
    - [Section 2: Disabling Tests](#section-2-disabling-tests)
  - [Deliberately Failing Tests](#deliberately-failing-tests)
    - [Why this module has none](#why-this-module-has-none)
    - [What the usual override does here](#what-the-usual-override-does-here)
    - [How to override @Disabled](#how-to-override-disabled)
    - [What to expect](#what-to-expect)

## The Domain: a Password Policy

The main class under test is [`PasswordPolicy`](src/main/java/com/example/password/PasswordPolicy.java), which checks a password against a set of rules. Its rules read naturally as sentences, such as "a password is rejected when it has no digit", which makes them good subjects for readable test names.

`PasswordPolicy.standard()` creates the policy that most tests use. `validate` checks every rule and reports all the broken ones together:

| Rule | Standard policy | Violation if broken |
| --- | --- | --- |
| Minimum length | At least 12 characters | `TOO_SHORT` |
| Maximum length | At most 128 characters | `TOO_LONG` |
| Uppercase letter | At least one | `MISSING_UPPERCASE` |
| Lowercase letter | At least one | `MISSING_LOWERCASE` |
| Digit | At least one | `MISSING_DIGIT` |
| Symbol | At least one. Any character that isn't a letter, digit or whitespace counts, including an emoji | `MISSING_SYMBOL` |
| Whitespace | Not allowed | `CONTAINS_WHITESPACE` |

Length is counted in Unicode code points, so an emoji counts as one character.

`isKnownBreached` isn't implemented yet, and always throws `UnsupportedOperationException`. The code refers to this as ticket `SEC-142`, which gives Section 2 a realistic reason to disable a test.

### Production classes

| Class | Purpose |
| --- | --- |
| [`PasswordPolicy`](src/main/java/com/example/password/PasswordPolicy.java) | The policy and its rules. A builder can switch each rule on or set its limits |
| [`ValidationResult`](src/main/java/com/example/password/ValidationResult.java) | A record of every rule a password breaks, with `isValid()` and `has(violation)` |
| [`Violation`](src/main/java/com/example/password/Violation.java) | An enum of the seven rules above, each with a readable description |

### Test helpers

These classes aren't test classes, so JUnit doesn't run them directly.

| Class | Purpose |
| --- | --- |
| [`SamplePasswords`](src/test/java/com/example/module_03/support/SamplePasswords.java) | Example passwords, including one valid example and examples that break a specific rule |
| [`DisplayNameTestSupport`](src/test/java/com/example/module_03/section_01/DisplayNameTestSupport.java) | An abstract base class that lets a test assert its own display name, so a passing test proves the name is correct |
| [`SentenceCaseDisplayNameGenerator`](src/test/java/com/example/module_03/section_01/SentenceCaseDisplayNameGenerator.java) | A custom generator that turns `rejectsPasswordWithoutDigit` into "Rejects password without digit" |

## Running the Module

Run these commands from the repository root.

| Goal | Command | Result |
| --- | --- | --- |
| Run the module | `mvn test -pl module-03` | 33 tests: 26 pass and 7 are skipped. The two sentence-named classes aren't included |
| Run one class | `mvn test -pl module-03 -Dtest=DisabledMethodTest` | 4 tests: 2 pass and 2 are skipped |
| Run the sentence-named classes | `mvn test -pl module-03 -Dtest='A_*'` | 7 tests pass |
| Run every class | `mvn test -pl module-03 -Dtest='*Test,A_*'` | 40 tests: 33 pass and 7 are skipped |

Maven only finds classes named `Test*`, `*Test`, `*Tests` or `*TestCase` by default. `A_password_is_rejected` and `A_valid_password` don't match, so a plain `mvn test` leaves them out. Run them by name, or from your IDE.

Display names are easiest to read in your IDE's result tree. Maven only shows some of them: for example, its output lists `DisplayNameAnnotationTest` as `Standard password policy`.

## Sections

Each section has its own package under [`src/test/java/com/example/module_03`](src/test/java/com/example/module_03).

### Section 1: Display Names

**What it tests:** the rules of the standard policy, while also checking the name JUnit gives each test. Start with `DisplayNameAnnotationTest`.

| Class | What it tests |
| --- | --- |
| [`DisplayNameAnnotationTest`](src/test/java/com/example/module_03/section_01/DisplayNameAnnotationTest.java) | Names set with `@DisplayName` on a class and on methods, including punctuation, another language and an emoji |
| [`ControlCharactersTest`](src/test/java/com/example/module_03/section_01/ControlCharactersTest.java) | How line feeds, carriage returns and tabs in a display name are replaced, using passwords that contain the same characters |
| [`StandardGeneratorTest`](src/test/java/com/example/module_03/section_01/StandardGeneratorTest.java) | Names from the `Standard` generator, such as `acceptsValidPassword()` |
| [`SimpleGeneratorTest`](src/test/java/com/example/module_03/section_01/SimpleGeneratorTest.java) | Names from the `Simple` generator, such as `acceptsValidPassword` |
| [`A_password_is_rejected`](src/test/java/com/example/module_03/section_01/A_password_is_rejected.java) | Names from the `ReplaceUnderscores` generator, such as `when it has no digit`, for four rejected passwords |
| [`A_valid_password`](src/test/java/com/example/module_03/section_01/A_valid_password.java) | Full sentences from `@IndicativeSentencesGeneration`, such as `A valid password -> has at least 12 characters` |
| [`PasswordPolicySentencesTest`](src/test/java/com/example/module_03/section_01/PasswordPolicySentencesTest.java) | The same sentence style with normal Java names, built with `@SentenceFragment` |
| [`DisplayNamePrecedenceTest`](src/test/java/com/example/module_03/section_01/DisplayNamePrecedenceTest.java) | That `@DisplayName` takes priority over a class-level generator |
| [`PasswordPolicyRulesTest`](src/test/java/com/example/module_03/section_01/PasswordPolicyRulesTest.java) | Names from the custom `SentenceCaseDisplayNameGenerator`, such as `Rejects 129 characters` |
| [`DefaultGeneratorTest`](src/test/java/com/example/module_03/section_01/DefaultGeneratorTest.java) | Declares no generator and prints its test names, so you can see the effect of changing the default |

**JUnit concepts**

- **Display name:** the name shown in your IDE and in reports. It replaces the class or method name for the reader, but doesn't change the code.
- **`@DisplayName`** sets the name directly on a class or method. It can contain spaces, punctuation, other languages and emoji.
- **Control characters** would break a report line, so they are made visible: a line feed becomes `<LF>`, a carriage return becomes `<CR>`, and other control characters, such as a tab, become `�`.
- **`@DisplayNameGeneration`** sets a generator that creates names for a class and its tests, so you don't need an annotation on every method.
- **`Standard`** is JUnit's default generator: the class name, and the method name with its parameter list.
- **`Simple`** is the same as `Standard`, but drops the empty `()` from methods without parameters.
- **`ReplaceUnderscores`** also turns underscores into spaces, so a class and its methods can read as a specification.
- **`IndicativeSentences`**, used through `@IndicativeSentencesGeneration`, joins the class and method names into one sentence. This helps in flat reports where the class name isn't shown next to the test. You can set the separator and the underlying generator.
- **`@SentenceFragment`** supplies each part of that sentence explicitly, so the Java names can stay conventional.
- **Custom generators** implement `DisplayNameGenerator`. They need a public no-argument constructor, because JUnit creates them by reflection.
- **Precedence:** when several sources could name a test, JUnit uses the first of these that applies:

  1. `@DisplayName` on the element
  2. `@DisplayNameGeneration` on the class or an enclosing class
  3. The default generator set by `junit.jupiter.displayname.generator.default`
  4. `DisplayNameGenerator.Standard`

**Try it:** `DefaultGeneratorTest` prints its names, so compare these two runs:

```bash
mvn test -pl module-03 -Dtest=DefaultGeneratorTest
mvn test -pl module-03 -Dtest=DefaultGeneratorTest '-Djunit.jupiter.displayname.generator.default=org.junit.jupiter.api.DisplayNameGenerator$ReplaceUnderscores'
```

The first prints `rejects_a_password_with_a_space()` and the second prints `rejects a password with a space`.

To make the second setting permanent for this module, create `src/test/resources/junit-platform.properties` containing this line:

```properties
junit.jupiter.displayname.generator.default=org.junit.jupiter.api.DisplayNameGenerator$ReplaceUnderscores
```

### Section 2: Disabling Tests

**What it tests:** what JUnit does and doesn't run when a test or class is disabled. The tests use counters and `fail()` calls to prove it.

| Class | What it tests |
| --- | --- |
| [`DisabledMethodTest`](src/test/java/com/example/module_03/section_02/DisabledMethodTest.java) | Two tests run and two are disabled, one with a ticket reason and one without. Counters confirm which steps still ran. Run the whole class, because filtering to one method changes the counts |
| [`DisabledClassTest`](src/test/java/com/example/module_03/section_02/DisabledClassTest.java) | That nothing in a disabled class runs. Its constructor, lifecycle methods and tests all call `fail()`, so the build would fail if any of them ran |
| [`DisabledBaseTest`](src/test/java/com/example/module_03/section_02/DisabledBaseTest.java) | A disabled class with one test, used as the parent of the next two classes |
| [`EnabledSubclassTest`](src/test/java/com/example/module_03/section_02/EnabledSubclassTest.java) | Extends the disabled class without repeating `@Disabled`, so its own test and the inherited test both run |
| [`StillDisabledSubclassTest`](src/test/java/com/example/module_03/section_02/StillDisabledSubclassTest.java) | Declares `@Disabled` again, so the inherited test is skipped |
| [`DisabledWithDisplayNameTest`](src/test/java/com/example/module_03/section_02/DisabledWithDisplayNameTest.java) | That a skipped test keeps its readable name, with its reason shown next to it, when you view it in a report |

What runs depends on where `@Disabled` is placed:

| Step | `@Disabled` on a method | `@Disabled` on a class |
| --- | --- | --- |
| `@BeforeAll`, `@AfterAll` | Runs once for the class | Doesn't run |
| Constructor | Runs for each test, including disabled tests | Doesn't run |
| `@BeforeEach`, `@AfterEach` | Doesn't run for the disabled test | Doesn't run |
| The test method | Doesn't run | Doesn't run |

**JUnit concepts**

- **`@Disabled`** skips a test method or a whole class. The tests are still reported, as skipped.
- **The reason:** `@Disabled` accepts a reason, which appears in the report. Include a ticket number, such as `SEC-142`, so that someone can trace why the test was switched off and when to enable it again. Without a reason, JUnit generates one that explains nothing.
- **Not inherited:** a subclass of a disabled class runs unless it declares `@Disabled` itself.
- **`@Disabled` and failing tests:** a disabled test doesn't run, so a broken test can stay hidden. Treat it as a short-term measure, with a ticket to remove it.

## Deliberately Failing Tests

Unlike Modules 1 and 2, this module has no classes tagged `expected-failure-tests`, so nothing here fails on purpose. Modules that do have them are listed in the [root README](../README.md#deliberately-failing-tests).

### Why this module has none

Modules 1 and 2 tag their failure demos because the point of those classes is to show failure output. This module is about names and skipped tests, which it can show in a passing build.

`DisabledClassTest` and `DisabledMethodTest` do contain `fail()` calls, but those aren't demonstrations of failure. They are guards: `@Disabled` stops them from running, and a failure would mean that a disabled test had run.

### What the usual override does here

The [parent POM](../pom.xml) excludes the `expected-failure-tests` tag from every module, and `-DexcludedGroups=none` clears that exclusion. Because no test in this module has the tag, the override changes nothing:

| Command | Result |
| --- | --- |
| `mvn test -pl module-03 -DexcludedGroups=none` | The same as a normal run: 33 tests, with 7 skipped |
| `mvn test -pl module-03 -Dgroups=expected-failure-tests -DexcludedGroups=none` | `BUILD SUCCESS` with `Tests run: 0` |

### How to override @Disabled

The override that matters in this module is a different one. `@Disabled` isn't a tag, so `excludedGroups` has no effect on it. To run disabled tests anyway, deactivate JUnit's `DisabledCondition` for one run:

| Goal | Command |
| --- | --- |
| Run the module with `@Disabled` ignored | `mvn test -pl module-03 '-Djunit.jupiter.conditions.deactivate=org.junit.*DisabledCondition'` |
| Do the same for one class | `mvn test -pl module-03 -Dtest=DisabledWithDisplayNameTest '-Djunit.jupiter.conditions.deactivate=org.junit.*DisabledCondition'` |

Keep the quotes, because some shells would otherwise treat the `*` as a file pattern. Use this setting to see why a test was disabled, and don't leave it on in a normal build.

### What to expect

With `@Disabled` ignored, the whole module ends with this summary and `BUILD FAILURE`:

```text
Tests run: 33, Failures: 1, Errors: 4, Skipped: 0
```

That result is expected, because several disabled tests were switched off for a reason:

| Class | What happens | Why |
| --- | --- | --- |
| `DisabledMethodTest` | `rejectsBreachedPassword` throws `UnsupportedOperationException` | `isKnownBreached` isn't implemented yet |
| `DisabledMethodTest` | `disabledWithoutReason` fails with `never executed` | Its guard `fail()` call has now been reached |
| `DisabledMethodTest` | `@AfterAll` fails with `only the enabled tests run ==> expected: <2> but was: <4>` | The counters now include the disabled tests |
| `DisabledClassTest` | `@BeforeAll` fails with `@BeforeAll does not run for a disabled class` | The guard in `@BeforeAll` stops the class before its tests start |
| `DisabledWithDisplayNameTest` | `blocks_passwords_from_known_breaches` throws `UnsupportedOperationException` | `isKnownBreached` isn't implemented yet |
| `DisabledBaseTest`, `StillDisabledSubclassTest` | The inherited whitespace test passes | Nothing was wrong with this test. It was only disabled to show how `@Disabled` is inherited |
