Module 7 is the largest module, with eight submodules. It gets a fresh payments domain, because IBANs, amounts and transfers are naturally data-driven:

- **IBANs:** an `Iban` value object, `IbanValidator` (mod-97 check) and `IbanGenerator`.
- **Payments:** `Amount`, a `PaymentScheme` enum and `Transfer`.
- **Tokens:** a `TokenValidator` that checks JWT-style claims against a `Clock`.

I'll start with the production code and check the IBAN arithmetic before writing any tests.

I'll verify the IBAN arithmetic against well-known example IBANs before building any tests on top of it.

All the standard example IBANs validate, every broken one is rejected, and 10,000 generated IBANs all pass. Now the Module 7 test support: shared IBAN and token data, plus two CSV files.

Now 7.1, repeated tests.

Now 7.3, every built-in argument source.

I've built Module 7 (data-driven tests) and sent it as **junit6-module7.zip**. It has 68 files, with nothing from Modules 1–6 in the code or the zip. I couldn't run JUnit here, because the download site for JUnit's libraries is blocked from this workspace. So the tests haven't run yet. I checked all the test data against the production code in plain Java, and every value matched.

**Production code (new for Module 7)**
- `com.example.iban`: `IbanValidator` checks an IBAN's length per country and its checksum. `Iban` is a value object whose only way in is `parse(String)`. `IbanGenerator` makes repeatable Dutch IBANs from a seeded random generator.
- `com.example.payments`:
  - `Amount` is a record of value plus currency.
  - `PaymentScheme` is an enum of the four SEPA/SWIFT schemes, each with its own rules.
  - `Transfer` is a six-field record that checks itself when created.
- `com.example.tokens`: `Claims`, `ClaimProblem` and `TokenValidator`, which checks access tokens, with a clock and allowed clock skew.

**Tests per submodule**

| Package | Classes | What it shows |
|---|---|---|
| `s01_repeated_tests` | `RepeatedIbanGenerationTest`, `FlakyHuntDemoTest`* | `@RepeatedTest`, `RepetitionInfo`, custom and long names, `failureThreshold` |
| `s02_consuming_arguments` | `FirstParameterizedTest`, `ParameterOrderTest`, `AutoCloseArgumentsTest` | A first parameterized test, the parameter order rule, `autoCloseArguments` |
| `s03_argument_sources` | `ValueSourceTest`, `NullAndEmptySourcesTest`, `EnumSourceTest`, `MethodSourceTest`, `FieldSourceTest`, `CsvSourceTest`, `CsvFileSourceTest`, `ArgumentsSourceTest` and `RandomIbansProvider` | Every built-in source, a custom provider, and combining or repeating sources |
| `s04_argument_count_validation` | `ArgumentCountValidationTest`, `ArgumentCountFailureDemoTest`* | Lenient vs `STRICT` counts, `allowZeroInvocations` |
| `s05_argument_conversion` | `ImplicitConversionTest`, `FallbackConversionTest`, `ExplicitConversionTest`, plus `HumanDurationConverter`, `@HumanDuration` and `AmountConverter` | Automatic conversion, conversion through a factory method, and custom converters |
| `s06_argument_aggregation` | `ArgumentsAccessorTest`, `AggregatorTest`, plus `TransferAggregator` and `@CsvToTransfer` | One CSV row from `transfers.csv` becomes one `Transfer` |
| `s07_display_names` | `InvocationDisplayNamesTest` | Each test checks its own display name: placeholders, JUnit 6 quoting, `''`, `Named`, `argumentSet` |
| `s08_parameterized_classes` | `SepaSchemeTest`, `TokenValidatorSuiteTest` (a record), `ConstructorInjectionTest`, `ParameterizedClassLifecycleTest` | Field and constructor injection, `@Nested`, and the lifecycle order checked in `@AfterAll` |

\* These two are tagged `failure-demo`, so normal builds skip them. Run them with `mvn test -DexcludedGroups=none -Dtest=<ClassName>`.

The shared test data is in `support` (`SampleIbans`, `TokenScenarios`) and in `src/test/resources/m07` (`ibans.csv`, `transfers.csv`). There are no pom changes; `junit-jupiter` already includes the parameterized-test library.

**Two changes you should know about**
- **Fallback conversion:** `Amount` has two static `String` factories (`parse` and `euro`). JUnit ignores a type's factories when there is more than one, so it can't convert text to `Amount` by itself. I made this a teaching point: `Iban` converts automatically, and `Amount` uses an explicit `AmountConverter`. I corrected the `Amount` Javadoc to say so.
- **Renamed class:** `TestIbans` is now `SampleIbans`. The `Test*` name would have matched Maven's test-class pattern.

**Check these first when you run it**
- `InvocationDisplayNamesTest` compares exact display-name strings. That includes JUnit 6's new quoting, for example `'!' isn't valid`, so it's the most sensitive class.
- `ImplicitConversionTest` relies on `0x1F` converting to an `int` and `nl-NL` converting to a `Locale`. The user guide says both work.
- `ParameterizedClassLifecycleTest` assumes `@BeforeAll` and `@AfterAll` run once around all invocations, as the guide states.

I also updated the Module 7 lab lines in the course outline, which still named classes from older modules.

Sources:
- [Parameterized Classes and Tests (JUnit user guide)](https://docs.junit.org/current/writing-tests/parameterized-classes-and-tests)
- [ArgumentCountValidationMode (API docs)](https://docs.junit.org/current/api/org.junit.jupiter.params/org/junit/jupiter/params/ArgumentCountValidationMode.html)