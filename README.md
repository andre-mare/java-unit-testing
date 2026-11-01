# Java Unit Testing with JUnit 6

[![Java 26](https://img.shields.io/badge/Java-26-blue)](https://openjdk.org/projects/jdk/26/)
[![JUnit 6.1.3](https://img.shields.io/badge/JUnit-6.1.3-25A162)](https://docs.junit.org/6.1.3/)
[![Maven 3.9](https://img.shields.io/badge/Maven-3.9-C71A36)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

Learn unit testing in Java by reading, running and breaking real tests. In Java, the standard framework for writing them is JUnit, and JUnit 6 is its current major version, with Java 17 as its minimum. This repository teaches JUnit 6 by example. Every concept is a test you can run, read and change.

The course has nine Maven modules, from a first `@Test` in Module 1 to test templates, dynamic tests and parallel execution in Modules 8 and 9. Each module has its own small production domain from banking and identity, such as bank accounts, IBAN validation, OAuth 2.0 tokens and audit logs, and the tests for each section are in their own package. The modules share no code, so you can work through them in order or start with the one you need.

## Table of Contents
- [Java Unit Testing with JUnit 6](#java-unit-testing-with-junit-6)
  - [Table of Contents](#table-of-contents)
  - [Getting started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Build and test](#build-and-test)
  - [Course modules](#course-modules)
  - [Project structure](#project-structure)
  - [Running tests](#running-tests)
    - [Tags and Module 5 profiles](#tags-and-module-5-profiles)
    - [Classes that need an explicit run](#classes-that-need-an-explicit-run)
  - [Deliberately failing tests](#deliberately-failing-tests)
  - [Parallel execution](#parallel-execution)
  - [Technology stack](#technology-stack)
  - [Project status](#project-status)
  - [Resources](#resources)
  - [License](#license)

## Getting started

### Prerequisites

- **JDK 26 or later.** The build compiles with `--release 26`, so an older JDK fails even though JUnit 6 itself only needs Java 17.
- **Maven 3.9 or later.** The project has no Maven Wrapper, so Maven must be installed. It was tested with Maven 3.9.16.

### Build and test

```bash
git clone https://github.com/andre-mare/java-unit-testing.git
cd java-unit-testing
mvn test
```

The build should end with `BUILD SUCCESS` after running 886 tests, 25 of them skipped. The skips are intentional: they demonstrate `@Disabled`, assumptions and conditional execution, so the exact number can vary with your operating system and environment variables.

## Course modules

Work through the modules in order. They build on each other conceptually, but their code is independent, so you can also run any module on its own.

| Module | Level | Topics | Domain under test |
| --- | --- | --- | --- |
| [1. Foundations](module-01/) | Beginner | First tests, test outcomes, class and method rules, lifecycle methods, the test tree, composed annotations | Calculator |
| [2. Assertions, assumptions and exceptions](module-02/) | Beginner | `Assertions`, AssertJ, `Assumptions`, `assertThrows` | Bank account |
| [3. Readable and skippable tests](module-03/) | Beginner | `@DisplayName`, display name generators, `@Disabled` | Password policy |
| [4. Structuring test suites](module-04/) | Intermediate | Test instance lifecycle, `@Nested`, test interfaces and contract tests | Bounded stack, money |
| [5. Controlling what runs, in what order, and for how long](module-05/) | Intermediate | Conditional execution, tags, execution order, `@Timeout` | App config, onboarding flow, rate limiter |
| [6. Dependency injection](module-06/) | Intermediate | Built-in and custom `ParameterResolver`s, Mockito | OAuth 2.0 client-credentials service |
| [7. Data-driven tests](module-07/) | Intermediate to advanced | `@RepeatedTest`, `@ParameterizedTest`, argument sources, conversion and aggregation, `@ParameterizedClass` | IBANs, payments, token validation |
| [8. Templates and dynamic tests](module-08/) | Advanced | `@TestTemplate`, `@ClassTemplate`, `@TestFactory` | Currency conversion, consents, primes, OAuth scopes |
| [9. Built-in extensions and parallel execution](module-09/) | Advanced | `@TempDir`, `@AutoClose`, `@DefaultLocale`, `@DefaultTimeZone`, system properties, parallel execution | Audit log, amount formatting, session expiry, issuer settings |

The [course outline](docs/JUnit%206%20Course%20Outline.md) lists the APIs and labs for each section, with links to the matching User Guide pages. It was written before the code, so where they differ, such as the Java version, package names and some lab classes, the code is correct.

## Project structure

```text
java-unit-testing/
├── pom.xml                    # Parent POM: Java version, JUnit BOM, plugins, default tag filter
├── docs/
│   └── JUnit 6 Course Outline.md
├── module-01/                 # One directory per course module, module-01 to module-09
│   ├── pom.xml                # Module name and any extra test dependencies
│   └── src/
│       ├── main/java/com/example/<domain>/       # Production code under test
│       └── test/java/com/example/module_01/
│           ├── section_01/                       # One package per course section
│           └── support/                          # Shared test helpers, in some modules
└── ...
```

Section packages match the outline's numbering, so section 7.3 is `module-07/src/test/java/com/example/module_07/section_03`. Test data files are in `src/test/resources`.

## Running tests

Run these commands from the repository root.

| Goal | Command |
| --- | --- |
| Run everything | `mvn test` |
| Run one module | `mvn test -pl module-07` |
| Run one class | `mvn test -pl module-04 -Dtest=BoundedStackNestedTest` |
| Run one tag across all modules | `mvn test -Dgroups=fast` |

### Tags and Module 5 profiles

Module 5 defines Maven profiles for its tags:

```bash
mvn test -pl module-05 -Pfast
mvn test -pl module-05 -Psecurity
mvn test -pl module-05 -Pno-slow       # everything except slow tests
```

The `integration` profile selects tests that are skipped unless `APP_ENV=integration` is set. They also need a region:

```bash
APP_ENV=integration APP_REGION=eu-west-1 mvn test -pl module-05 -Pintegration
```

### Classes that need an explicit run

Surefire only discovers classes named `Test*`, `*Test`, `*Tests` or `*TestCase` by default. The two sentence-named classes in Module 3 don't match, so `mvn test` skips them. Run them by name:

```bash
mvn test -pl module-03 -Dtest='A_*'
```

## Deliberately failing tests

Some classes fail on purpose so you can read real failure output. They carry the `expected-failure-tests` tag, which the parent POM excludes by default. To run one, clear the filter and name the class:

```bash
mvn test -pl module-04 -DexcludedGroups=none -Dtest=PerClassStateLeakDemoTest
```

Maven reports `BUILD FAILURE` for these runs. That is the expected result.

| Module | Classes |
| --- | --- |
| 1 | `TestOutcomesTest` |
| 2 | `AssertionFailuresDemoTest`, `ExceptionFailuresDemoTest` |
| 4 | `PerClassStateLeakDemoTest` |
| 5 | `HiddenCouplingDemoTest`, `TimeoutFailuresDemoTest` |
| 6 | `ResolutionFailuresDemoTest`, `CompetingResolversDemoTest`, `MockitoFailuresDemoTest` |
| 7 | `FlakyHuntDemoTest`, `ArgumentCountFailureDemoTest` |
| 8 | `TemplateFailureDemoTest`, `DynamicFailureDemoTest` |

## Parallel execution

Tests run sequentially by default. To try parallel mode without changing any files, use:

```bash
mvn test \
  -Djunit.jupiter.execution.parallel.enabled=true \
  -Djunit.jupiter.execution.parallel.mode.default=concurrent \
  -Djunit.jupiter.execution.parallel.mode.classes.default=concurrent
```

**This build currently fails, by design of the course.** Exposing and fixing tests that aren't safe to run in parallel is the Module 9.5 lab, and the first failure is `RepetitionInfoTest` in Module 6. Add `-Dmaven.test.failure.ignore=true` to see the results of every module, and see [`module9-parallel.properties`](module-09/src/test/resources/module9-parallel.properties) for the settings and a list of common causes.

`AuditTrailRaceDemoTest` in Module 9 shows the same kind of race in isolation. It passes when run sequentially, including in the default build, and fails intermittently in parallel mode.

## Technology stack

| Tool | Version | Used for | Scope |
| --- | --- | --- | --- |
| [JUnit Jupiter](https://junit.org/) | 6.1.3, through `junit-bom` | Test framework | All modules |
| [Maven Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/) | 3.15.0 | Compiles with `-parameters` for readable parameterized test names | All modules |
| [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/) | 3.5.5 | Runs the tests | All modules |
| [AssertJ](https://assertj.github.io/doc/) | 3.26.0 | Fluent assertions | Modules 2, 8, 9 |
| [Mockito](https://site.mockito.org/) | 5.24.0 | Mocking collaborators | Module 6 |
| [Jimfs](https://github.com/google/jimfs) | 1.3.0 | In-memory file system for `@TempDir` | Module 9 |

## Project status

Modules 1 to 9 are implemented. The capstone described in the [course outline](docs/JUnit%206%20Course%20Outline.md), a payment-initiation test suite, hasn't been built yet.

## Resources

- [JUnit 6.1.3 User Guide](https://docs.junit.org/6.1.3/)
- [Writing Tests](https://docs.junit.org/6.1.3/writing-tests/annotations.html), the section this course follows
- [Maven Surefire: using JUnit Platform](https://maven.apache.org/surefire/maven-surefire-plugin/examples/junit-platform.html)

## License

Released under the [MIT License](LICENSE). Copyright (c) 2026 André Maré.
