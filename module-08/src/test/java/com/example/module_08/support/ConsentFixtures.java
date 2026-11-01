package com.example.module_08.support;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/** Module 8 shared test data for consents: a start time, parties, permissions and predictable ids. */
public final class ConsentFixtures {

    public static final Instant START = Instant.parse("2026-11-02T09:00:00Z");

    public static final String CLIENT = "tpp-budget-app";
    public static final String OTHER_CLIENT = "tpp-tax-advisor";
    public static final String PSU = "psu-andre";
    public static final String OTHER_PSU = "psu-maria";

    public static final String READ_ACCOUNTS = "ReadAccountsBasic";
    public static final String READ_BALANCES = "ReadBalances";
    public static final String READ_TRANSACTIONS = "ReadTransactionsDetail";

    private ConsentFixtures() {
    }

    /** Ids {@code consent-1}, {@code consent-2}, ... so tests can name them. */
    public static Supplier<String> sequentialIds() {
        AtomicInteger next = new AtomicInteger();
        return () -> "consent-" + next.incrementAndGet();
    }
}
