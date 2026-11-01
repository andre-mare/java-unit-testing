package com.example.module_06.section_02;

import static com.example.module_06.support.AuthFixtures.SECRET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.Set;

import com.example.auth.AccessToken;
import com.example.auth.AuditEvent;
import com.example.auth.ClientCredentialsService;
import com.example.auth.TokenRequestException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * Module 6.2 – programmatic registration with {@code @RegisterExtension}.
 * <p>
 * The field holds the extension instance, so the test class can configure it
 * (a different "now" here) and call it ({@code fixture.register(...)},
 * {@code fixture.auditLog()}). A {@code static} field is shared by all tests and
 * can also serve class-level callbacks; an instance field gets a new extension
 * for every test.
 */
class RegisterExtensionTest {

    private static final Instant NOON = Instant.parse("2026-06-01T12:00:00Z");

    @RegisterExtension
    static final TokenServiceFixture fixture = new TokenServiceFixture(NOON);

    @Test
    void usesTheConfiguredClock(ClientCredentialsService service) {
        fixture.register("app", "read");
        AccessToken token = service.issue("app", SECRET, Set.of());
        assertEquals(Instant.parse("2026-06-01T12:05:00Z"), token.expiresAt());
    }

    @Test
    void testsCanTalkToTheExtension(ClientCredentialsService service) {
        fixture.register("app", "read");
        service.issue("app", SECRET, Set.of());
        assertEquals(AuditEvent.Type.TOKEN_ISSUED, fixture.auditLog().last().type());
    }

    @Test
    void stateIsResetBeforeEachTest(ClientCredentialsService service) {
        // "app" registered by the other tests is gone: beforeEach() made a new repository.
        TokenRequestException e = assertThrows(TokenRequestException.class,
            () -> service.issue("app", SECRET, Set.of()));
        assertEquals(TokenRequestException.Error.INVALID_CLIENT, e.error());
    }
}
