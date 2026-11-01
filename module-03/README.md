I'll use a new production class for Module 3, a `PasswordPolicy` validator, because its rules read naturally as test-name sentences. I'm checking the exact `DisplayNameGenerator` API in JUnit 6.1.3 first, since the custom generator depends on it.

The `DisplayNameGenerator` API matches what I planned. I'm writing the production classes now, in a new `com.example.password` package.

Now the 3.1 display-name test classes. Each one checks its own display name, so a passing build proves the names are what the class says they are.

Now 3.2, disabling tests.

Next: `package-info` files, a `junit-platform.properties` file for the default-generator exercise, and the Surefire include for the sentence-named classes.

I'll compile `PasswordPolicy` and check every sample password and the camel-case splitting the custom generator relies on.

Module 3 is in `junit6-modules-1-3.zip`, which contains the whole project so far. It doesn't use anything from Modules 1 or 2: it has its own production class, its own test data, and plain `@Test` instead of `@UnitTest`. I compiled `PasswordPolicy` and confirmed that each sample password breaks exactly the rule it's named for. I couldn't run JUnit here because Maven Central is blocked, so please run `mvn test` locally.

**Production classes** (`src/main/java/com/example/password`)

- **`PasswordPolicy`** has a builder and a `standard()` policy: 12 to 128 characters, with an uppercase letter, a lowercase letter, a digit and a symbol, and no whitespace. Its `isKnownBreached()` method is deliberately unimplemented, which gives 3.2 a realistic reason to disable a test (ticket SEC-142).
- **`Violation`** is an enum of the seven rules.
- **`ValidationResult`** returns every broken rule at once.

**Shared within Module 3**

- **`SamplePasswords`** holds one example password per rule.
- **`DisplayNameTestSupport`** lets a test assert its own display name. Most 3.1 tests use it, so a green build proves the names are right.

**3.1 `s01_display_names`**

| Class | Covers |
| --- | --- |
| `DisplayNameAnnotationTest` | `@DisplayName` with spaces, quotes, Dutch text and an emoji |
| `ControlCharactersTest` | How `\n`, `\r` and tab appear in names (`<LF>`, `<CR>`, `�`) |
| `StandardGeneratorTest` | The `Standard` generator |
| `SimpleGeneratorTest` | The `Simple` generator |
| `A_password_is_rejected` | The `ReplaceUnderscores` generator |
| `A_valid_password` | The `IndicativeSentences` generator |
| `PasswordPolicySentencesTest` | `@SentenceFragment` |
| `DisplayNamePrecedenceTest` | Which name wins when several apply |
| `SentenceCaseDisplayNameGenerator` | A custom generator that turns `rejectsPasswordWithoutDigit` into "Rejects password without digit" |
| `PasswordPolicyRulesTest` | Uses the custom generator |
| `SentenceCaseDisplayNameGeneratorTest` | Unit tests for the custom generator itself |
| `DefaultGeneratorTest` | The global default generator, as a three-run exercise |

**3.2 `s02_disabling_tests`**

| Class | Covers |
| --- | --- |
| `DisabledMethodTest` | Two tests run and two are disabled, one with a ticket reason and one without. Counters show that `@BeforeEach` is skipped for disabled tests but the class is still created for them. |
| `DisabledClassTest` | A disabled class runs nothing at all. Every method calls `fail()`, so the build goes red if any of them runs. |
| `DisabledBaseTest`, `EnabledSubclassTest`, `StillDisabledSubclassTest` | `@Disabled` isn't inherited. A subclass has to declare it again to stay disabled. |
| `DisabledWithDisplayNameTest` | A skipped test keeps its readable name in the report. |

**Project changes**

- **`pom.xml` includes:** Surefire only picks up classes ending in `Test` by default, so the sentence-named classes like `A_password_is_rejected` wouldn't run. I added an include for `**/*_*.java`.
- **`junit-platform.properties`:** this new file in `src/test/resources` has the default-generator line commented out, ready for `DefaultGeneratorTest`'s third run.
- **Course outline:** the two Module 3 lab descriptions now describe the password examples instead of `BankAccount`.

Three expectations come from the JUnit docs rather than a test run, so check these first if anything fails:

- the `<LF>`/`<CR>` names in `ControlCharactersTest`
- the count of 4 created instances in `DisabledMethodTest`
- the extra Surefire include

Sources:
- [DisplayNameGenerator API, JUnit 6.1.3](https://docs.junit.org/current/api/org.junit.jupiter.api/org/junit/jupiter/api/DisplayNameGenerator.html)
- [JUnit 6.0.0-RC1 release notes](https://docs.junit.org/6.0.0-RC1/release-notes) (control characters in display names)