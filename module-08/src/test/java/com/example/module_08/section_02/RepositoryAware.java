package com.example.module_08.section_02;

/**
 * Module 8.2 – implemented by test classes that {@link ConsentRepositoryProvider} supplies.
 * A plain interface keeps the provider free of reflection.
 */
public interface RepositoryAware {

    void use(RepositoryFixture repository);
}
