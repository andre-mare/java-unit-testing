package com.example.module_06.support;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;

import com.example.auth.Client;
import com.example.auth.Sha256SecretVerifier;

/** Module 6 test data shared by all three submodules. */
public final class AuthFixtures {

    /** The fixed "now" of every Module 6 test. */
    public static final Instant NOW = Instant.parse("2026-01-15T09:00:00Z");

    public static final Clock FIXED_CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    /** The raw secret of every test client. */
    public static final String SECRET = "s3cret-for-tests-only";

    public static final String SECRET_HASH = Sha256SecretVerifier.hash(SECRET);

    private AuthFixtures() {
    }

    /** An enabled client with the test secret and the given scopes. */
    public static Client client(String clientId, String... scopes) {
        return new Client(clientId, SECRET_HASH, Set.of(scopes), true);
    }
}
