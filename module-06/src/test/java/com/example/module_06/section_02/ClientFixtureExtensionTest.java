package com.example.module_06.section_02;

import static com.example.module_06.support.AuthFixtures.SECRET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import com.example.auth.AccessToken;
import com.example.auth.AuditEvent;
import com.example.auth.Client;
import com.example.auth.ClientCredentialsService;
import com.example.auth.InMemoryClientRepository;
import com.example.auth.TokenRequestException;
import com.example.module_06.support.RecordingAuditLog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Module 6.2 lab – tests that declare what they need and get it injected.
 * <p>
 * Each test lists the objects it needs as parameters; {@link ClientFixtureExtension}
 * builds and wires them. The test body contains only what the test is about.
 * Compare the length of these tests with {@code TestInfoTest.uniqueTestDataPerTest()}
 * in Module 6.1, which builds everything by hand.
 */
@ExtendWith(ClientFixtureExtension.class)
class ClientFixtureExtensionTest {

    private InMemoryClientRepository repositoryInBeforeEach;

    @BeforeEach
    void captureRepository(InMemoryClientRepository repository) {
        repositoryInBeforeEach = repository;
    }

    @Test
    void injectsAReadyToUseService(ClientCredentialsService service, Client client) {
        AccessToken token = service.issue(client.clientId(), SECRET, Set.of());

        assertEquals("token-1", token.value());
        assertEquals(Set.of("read"), token.scopes(), "default scope without @ClientWithScopes");
    }

    @Test
    void annotationConfiguresTheClient(ClientCredentialsService service,
            @ClientWithScopes({ "payments:read", "payments:write" }) Client client) {
        AccessToken token = service.issue(client.clientId(), SECRET, Set.of("payments:write"));
        assertEquals(Set.of("payments:write"), token.scopes());
    }

    @Test
    void disabledClientIsRefused(ClientCredentialsService service,
            @ClientWithScopes(value = "read", enabled = false) Client client, RecordingAuditLog auditLog) {
        TokenRequestException e = assertThrows(TokenRequestException.class,
            () -> service.issue(client.clientId(), SECRET, Set.of()));

        assertEquals(TokenRequestException.Error.UNAUTHORIZED_CLIENT, e.error());
        assertEquals(AuditEvent.Type.TOKEN_DENIED, auditLog.last().type(), "same audit log as the service");
    }

    @Test
    void allParametersOfOneTestShareTheFixtures(InMemoryClientRepository repository, Client first, Client second) {
        assertSame(repositoryInBeforeEach, repository, "@BeforeEach got the same instance");
        assertEquals(2, repository.size(), "both clients were saved in it");
        assertNotEquals(first.clientId(), second.clientId());
    }

    @Test
    void everyTestStartsWithAnEmptyRepository(InMemoryClientRepository repository) {
        assertEquals(0, repository.size());
    }

    @Test
    void builtInAndCustomResolversWorkTogether(TestInfo info, Client client) {
        assertTrue(client.clientId().contains(info.getTestMethod().orElseThrow().getName()));
    }
}
