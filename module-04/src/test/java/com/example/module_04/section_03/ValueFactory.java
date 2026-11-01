package com.example.module_04.section_03;

/**
 * Module 4.3 – the root of the contract interfaces: supplies the value under test.
 * <p>
 * The JUnit User Guide calls this {@code Testable}. It is renamed here because
 * Surefire runs every class named {@code Test*}, and an interface is not a test class.
 *
 * @param <T> the type whose contract is tested
 */
interface ValueFactory<T> {

    /** A fresh value for each call. */
    T createValue();
}
