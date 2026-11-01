package com.example.primes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Prime numbers, Module 8 production code: a simple subject for generated tests.
 * Two independent algorithms (trial division and a sieve) let tests compare them.
 */
public final class Primes {

    private Primes() {
    }

    /** Trial division up to the square root. Numbers below 2 are not prime. */
    public static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        if (n % 2 == 0) {
            return n == 2;
        }
        for (int d = 3; (long) d * d <= n; d += 2) {
            if (n % d == 0) {
                return false;
            }
        }
        return true;
    }

    /** Sieve of Eratosthenes: all primes from 2 to {@code limit}, ascending. */
    public static List<Integer> upTo(int limit) {
        if (limit < 2) {
            return List.of();
        }
        boolean[] composite = new boolean[limit + 1];
        for (int i = 2; (long) i * i <= limit; i++) {
            if (!composite[i]) {
                for (int j = i * i; j <= limit; j += i) {
                    composite[j] = true;
                }
            }
        }
        return IntStream.rangeClosed(2, limit).filter(i -> !composite[i]).boxed().toList();
    }

    /** Prime factors in ascending order, with repeats: 12 gives [2, 2, 3]. */
    public static List<Integer> factorize(int n) {
        if (n < 2) {
            throw new IllegalArgumentException("Only numbers from 2 have prime factors: " + n);
        }
        List<Integer> factors = new ArrayList<>();
        int rest = n;
        for (int d = 2; (long) d * d <= rest; d++) {
            while (rest % d == 0) {
                factors.add(d);
                rest /= d;
            }
        }
        if (rest > 1) {
            factors.add(rest);
        }
        return List.copyOf(factors);
    }
}
