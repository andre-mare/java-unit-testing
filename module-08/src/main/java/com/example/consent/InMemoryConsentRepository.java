package com.example.consent;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** A {@link ConsentRepository} backed by a map. Fast; forgets everything when dropped. */
public final class InMemoryConsentRepository implements ConsentRepository {

    private final Map<String, Consent> consents = new ConcurrentHashMap<>();

    @Override
    public void save(Consent consent) {
        consents.put(consent.id(), consent);
    }

    @Override
    public Optional<Consent> findById(String id) {
        return Optional.ofNullable(consents.get(id));
    }

    @Override
    public List<Consent> findByPsu(String psuId) {
        return consents.values().stream().filter(c -> c.psuId().equals(psuId)).toList();
    }

    @Override
    public int count() {
        return consents.size();
    }
}
