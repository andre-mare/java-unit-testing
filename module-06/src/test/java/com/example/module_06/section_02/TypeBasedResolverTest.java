package com.example.module_06.section_02;

import static com.example.module_06.support.AuthFixtures.NOW;
import static com.example.module_06.support.AuthFixtures.SECRET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Duration;
import java.util.Set;

import com.example.auth.AccessToken;
import com.example.auth.ClientCredentialsService;
import com.example.auth.InMemoryClientRepository;
import com.example.auth.Sha256SecretVerifier;
import com.example.module_06.support.AuthFixtures;
import com.example.module_06.support.RecordingAuditLog;
import com.example.module_06.support.SequentialTokenGenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Module 6.2 – registering a resolver with {@code @ExtendWith}, and constructor injection.
 * <p>
 * Start here for 6.2. {@code @ExtendWith(FixedClockResolver.class)} activates the
 * resolver for the whole class: the constructor and the test methods all receive
 * the fixed clock. Custom resolvers must always be registered; only the three
 * built-in ones from Module 6.1 are active by default.
 */
@ExtendWith(FixedClockResolver.class)
class TypeBasedResolverTest {

    private final InMemoryClientRepository clients = new InMemoryClientRepository();
    private final ClientCredentialsService service;

    TypeBasedResolverTest(Clock clock) {
        service = new ClientCredentialsService(clients, new Sha256SecretVerifier(), new SequentialTokenGenerator(),
            new RecordingAuditLog(), clock);
    }

    @Test
    void constructorReceivedTheFixedClock() {
        clients.save(AuthFixtures.client("app", "read"));
        AccessToken token = service.issue("app", SECRET, Set.of());
        assertEquals(NOW.plus(ClientCredentialsService.TOKEN_LIFETIME), token.expiresAt());
    }

    @Test
    void testMethodsCanAskForItToo(Clock clock) {
        assertEquals(NOW, clock.instant());
    }

    @Test
    void tokenExpiresExactlyAtExpiresAt(Clock clock) {
        clients.save(AuthFixtures.client("app", "read"));
        AccessToken token = service.issue("app", SECRET, Set.of());

        assertFalse(token.isExpiredAt(token.expiresAt().minus(Duration.ofNanos(1))));
        assertTrue(token.isExpiredAt(token.expiresAt()));
        assertFalse(token.isExpiredAt(clock.instant()));
    }
}
