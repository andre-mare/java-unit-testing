package com.example.module_02.support;


public class BasicArithmetic {

    /** Adds two ints; throws {@link ArithmeticException} on overflow. */
    public int add(int a, int b) {
        return Math.addExact(a, b);
    }

    /** Subtracts b from a; throws {@link ArithmeticException} on overflow. */
    public int subtract(int a, int b) {
        return Math.subtractExact(a, b);
    }

    /** Multiplies two ints; throws {@link ArithmeticException} on overflow. */
    public int multiply(int a, int b) {
        return Math.multiplyExact(a, b);
    }

    /** Integer division; throws {@link ArithmeticException} ("/ by zero") when b is 0. */
    public int divide(int a, int b) {
        return a / b;
    }

    /** Square root; throws {@link IllegalArgumentException} for negative input. */
    public double sqrt(double x) {
        if (x < 0) {
            throw new IllegalArgumentException("Cannot take the square root of a negative number: " + x);
        }
        return Math.sqrt(x);
    }

    /**
     * n! for 0 <= n <= 20.
     * Throws {@link IllegalArgumentException} for n < 0 and
     * {@link ArithmeticException} for n > 20 (result no longer fits in a long).
     */
    public long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must not be negative: " + n);
        }
        if (n > 20) {
            throw new ArithmeticException("n! overflows a long for n > 20: " + n);
        }
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    /** True when n is a prime number. Numbers below 2 are not prime. */
    public boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        if (n % 2 == 0) {
            return n == 2;
        }
        for (int i = 3; (long) i * i <= n; i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
    

}
