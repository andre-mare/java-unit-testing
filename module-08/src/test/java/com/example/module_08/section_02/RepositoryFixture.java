package com.example.module_08.section_02;

import java.util.function.Supplier;

import com.example.consent.ConsentRepository;

/**
 * Module 8.2 – what the class template hands to each test instance.
 *
 * @param name       shown in the report, such as {@code in-memory}
 * @param repository a fresh, empty repository for this test
 * @param reopen     a second repository on the same storage, to simulate a restart
 */
public record RepositoryFixture(String name, ConsentRepository repository, Supplier<ConsentRepository> reopen) {
}
