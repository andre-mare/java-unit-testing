package com.example.consent;

import java.util.List;
import java.util.Optional;

/**
 * Stores consents. Two implementations ({@link InMemoryConsentRepository},
 * {@link FileConsentRepository}) must behave the same, which is what a class
 * template tests in Module 8.2.
 */
public interface ConsentRepository {

    /** Inserts or replaces the consent with the same id. */
    void save(Consent consent);

    Optional<Consent> findById(String id);

    /** All consents of one account holder, in no particular order. */
    List<Consent> findByPsu(String psuId);

    int count();
}
