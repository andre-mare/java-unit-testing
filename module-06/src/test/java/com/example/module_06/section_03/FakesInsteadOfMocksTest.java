package com.example.module_06.section_03;

import static com.example.module_06.support.AuthFixtures.FIXED_CLOCK;
import static com.example.module_06.support.AuthFixtures.SECRET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Set;

import com.example.auth.AuditEvent;
import com.example.auth.ClientCredentialsService;
import com.example.auth.InMemoryClientRepository;
import com.example.auth.Sha256SecretVerifier;
import com.example.auth.TokenRequestException;
import com.example.module_06.support.AuthFixtures;
import com.example.module_06.support.RecordingAuditLog;
import com.example.module_06.support.SequentialTokenGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Module 6.3 – the same scenarios as {@link ClientCredentialsServiceTest}, with fakes.
 * <p>
 * No Mockito: simple working implementations (an in-memory repository, a
 * recording audit log, a predictable token generator) and the real secret
 * verifier. Tests check the resulting state instead of the calls made.
 * <ul>
 *   <li>Fakes: tests survive refactoring of HOW the service talks to its
 *       collaborators, and read like plain usage.</li>
 *   <li>Mocks: precise control of every return value and every call, including
 *       "was never called" and failure cases such as a broken audit log.</li>
 * </ul>
 * Both are valid; use fakes where they are cheap and mocks where you must
 * observe or force an interaction.
 */
class FakesInsteadOfMocksTest {

    private final InMemoryClientRepository clients = new InMemoryClientRepository();
    private final RecordingAuditLog auditLog = new RecordingAuditLog();
    private ClientCredentialsService service;

    @BeforeEach
    void createService() {
        clients.save(AuthFixtures.client("app", "payments:read", "payments:write"));
        service = new ClientCredentialsService(clients, new Sha256SecretVerifier(), new SequentialTokenGenerator(),
            auditLog, FIXED_CLOCK);
    }

    @Test
    void issuesATokenForAValidClient() {
        assertEquals("token-1", service.issue("app", SECRET, Set.of("payments:read")).value());
        assertEquals(List.of(AuditEvent.Type.TOKEN_ISSUED), auditLog.events().stream().map(AuditEvent::type).toList());
    }

    @Test
    void wrongSecretIsInvalidClient() {
        TokenRequestException e = assertThrows(TokenRequestException.class,
            () -> service.issue("app", "wrong", Set.of()));
        assertEquals(TokenRequestException.Error.INVALID_CLIENT, e.error());
        assertEquals("invalid_client", auditLog.last().detail());
    }

    @Test
    void scopeOutsideTheAllowedSetIsRejected() {
        TokenRequestException e = assertThrows(TokenRequestException.class,
            () -> service.issue("app", SECRET, Set.of("admin")));
        assertEquals(TokenRequestException.Error.INVALID_SCOPE, e.error());
    }
}
