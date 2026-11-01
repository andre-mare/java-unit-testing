Module 4 gets its own production code: a `BoundedStack<T>` interface with two implementations (the outline already lists it for Module 4), plus a `Money` value object for the equals and compareTo contract tests. I'm creating the folders.

Now 4.1, test instance lifecycle.

Now 4.2, nested tests.

Now 4.3. Surefire runs any class whose name starts or ends with `Test`, so I'm naming the interfaces to avoid that. For example, the JUnit guide's `Testable` becomes `ValueFactory` here.

`sharesOneInstanceAcrossTests` is too weak to prove `PER_CLASS`. I'll replace it with the same instance count used in 4.1.

Next are the `package-info` files. Then I'll compile the production classes and check every value the tests assert, since I can't run JUnit here.

That edit split the formatting oddly (code style on "B", italics on the rest), so I'm rewriting the line cleanly.

The outline line now reads correctly. Packaging only the Module 4 files:

Module 4 is in `junit6-module4.zip`. It contains only Module 4 files, with no Module 1–3 code, `pom.xml` or `junit-platform.properties`, and it needs no build changes. Unzip it into the folder that contains `unit-testing-examples` and it merges with your project.

I compiled the production classes and ran 29 checks covering every value the tests expect, on both stack implementations; all passed. I couldn't run JUnit itself because Maven Central is blocked here, so please run `mvn test` on your machine.

**Production classes** (new, unrelated to Modules 1–3)

- **`com.example.stack`:** `BoundedStack<T>`, a fixed-capacity last-in-first-out stack, with two implementations, `ArrayBoundedStack` and `LinkedBoundedStack`.
- **`com.example.money`:** `Money`, a value object. `Money.of("10", "EUR")` equals `Money.of("10.00", "EUR")`, comparing EUR with USD throws, and too many decimals for the currency throws instead of rounding.

**4.1 `s01_instance_lifecycle`**

| Class | Shows |
| --- | --- |
| `PerMethodLifecycleTest` | One instance per test, so no state leaks between tests |
| `PerClassLifecycleTest` | One shared instance, with non-static `@BeforeAll`/`@AfterAll` and a reset in `@BeforeEach` |
| `PerClassStateLeakDemoTest` | Fails on purpose: what happens without that reset (tagged `failure-demo`) |
| `DefaultLifecycleTest` | Exercise: change the default with `-Djunit.jupiter.testinstance.lifecycle.default=per_class` |

**4.2 `s02_nested_tests`**

| Class | Shows |
| --- | --- |
| `BoundedStackNestedTest` | Three levels: when new, then after pushing an element, then when filled to capacity |
| `NestedLifecycleOrderTest` | Checks the exact order of before and after methods across levels |
| `BoundedStackCapacityTest` | `@Nested` inside a `@ParameterizedClass`, a preview of Module 7 |

**4.3 `s03_test_interfaces`**

| Class | Shows |
| --- | --- |
| `BoundedStackContract` | 10 shared tests that run against both `ArrayBoundedStackTest` and `LinkedBoundedStackTest` |
| `EqualsContract`, `ComparableContract` | Reusable contract tests for equality and ordering, applied to `Money` in `MoneyTest` |
| `LifecycleLogging`, `Timed` | Interfaces that give a class lifecycle methods, `PER_CLASS`, a tag and an extension, verified in `InterfaceMixinTest` |

**Things to know**

- **Naming:** the interfaces avoid names starting or ending in `Test`, otherwise Surefire would try to run them. That's why the JUnit guide's `Testable` is called `ValueFactory` here.
- **Lifecycle default:** don't set the per-class default permanently in `junit-platform.properties`. Module 1's `LifecycleDemoTest` expects one instance per test and would fail.
- **Course outline:** I updated the 4.2 lab line to describe the stack example instead of `BankAccountTest`.