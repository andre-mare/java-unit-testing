package com.example.module_02.section_02;

import static com.example.module_02.support.MoneyAssertions.assertAmountEquals;
import static com.example.module_02.support.TestAccounts.eur;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.abort;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.example.bank.BankAccount;
import com.example.module_02.support.TestAccounts;

/**
 * Module 2.2 – assumptions: abort a test (not fail it) when the environment is wrong.
 * <p>
 * A failed assumption throws {@code org.opentest4j.TestAbortedException}. The IDE
 * shows the test as aborted/skipped, and Maven counts it under "Skipped".
 * <p>
 * Run it twice and compare the results:
 * <pre>
 * mvn test -Dtest=AssumptionsTest            // CI-only tests are aborted
 * ENV=CI mvn test -Dtest=AssumptionsTest     // CI-only tests run   (Windows: set ENV=CI)
 * </pre>
 */
class AssumptionsTest {

    private final BankAccount account = TestAccounts.withHundred();

    @Test
    void onlyOnCiServer() {
        assumeTrue("CI".equals(System.getenv("ENV")));
        // Everything below runs only when ENV=CI.
        assertAmountEquals("100.00", account.getBalance());
    }

    @Test
    void notOnDeveloperWorkstation() {
        assumeTrue("CI".equals(System.getenv("ENV")),
            () -> "Aborting: ENV is " + System.getenv("ENV") + ", not CI");
        assertTrue(account.getTransactions().isEmpty());
    }

    @Test
    void notOnWindows() {
        assumeFalse(System.getProperty("os.name").toLowerCase().contains("win"),
            "file permission checks behave differently on Windows");
        assertTrue(Files.isReadable(Path.of(System.getProperty("java.home"))));
    }

    @Test
    void inAllEnvironmentsWithExtraChecksOnCi() {
        account.deposit(eur("25"));

        assumingThat("CI".equals(System.getenv("ENV")), () -> {
            // Only on CI. A failing assertion here still FAILS the test;
            // a false condition simply skips this block.
            assertEquals(4, account.statement().size(), "header, opening, one deposit, closing");
        });

        // Always runs, on every machine.
        assertAmountEquals("125.00", account.getBalance());
    }

    @Test
    void abortExplicitly() {
        // abort() is the explicit form, for when your own code has already decided.
        String exportDir = System.getProperty("bank.export.dir");
        if (exportDir == null) {
            abort("set -Dbank.export.dir=<folder> to run the export check");
        }
        assertTrue(Files.isDirectory(Path.of(exportDir)));
    }
}
