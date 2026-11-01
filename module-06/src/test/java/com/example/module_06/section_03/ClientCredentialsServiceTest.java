package com.example.module_06.section_03;

import static com.example.module_06.support.AuthFixtures.FIXED_CLOCK;
import static com.example.module_06.support.AuthFixtures.NOW;
import static com.example.module_06.support.AuthFixtures.SECRET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import com.example.auth.AccessToken;
import com.example.auth.AuditEvent;
import com.example.auth.AuditLog;
import com.example.auth.Client;
import com.example.auth.ClientCredentialsService;
import com.example.auth.ClientRepository;
import com.example.auth.SecretVerifier;
import com.example.auth.TokenGenerator;
import com.example.auth.TokenRequestException;
import com.example.module_06.support.AuthFixtures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Module 6.3 lab – the service tested in isolation, with every collaborator mocked.
 * <p>
 * Start here for 6.3. {@code MockitoExtension} is a JUnit extension: it creates
 * the {@code @Mock} and {@code @Captor} fields before each test and checks
 * stubbing afterwards. The real {@link java.time.Clock} is passed in directly,
 * since a fixed clock is simpler and more honest than a mocked one.
 * <p>
 * The tools used:
 * <ul>
 *   <li>{@code when(...).thenReturn(...)} – stub what a collaborator returns</li>
 *   <li>{@code verify(...)} – check that a call happened; {@code never()},
 *       {@code verifyNoInteractions(...)} – check that it did NOT</li>
 *   <li>{@code ArgumentCaptor} – grab the argument of a call to inspect it</li>
 *   <li>{@code argThat(...)} – match an argument with a condition</li>
 *   <li>{@code inOrder(...)} – check the order of calls</li>
 *   <li>{@code doThrow(...).when(...)} – make a {@code void} method throw</li>
 * </ul>
 * {@code MockitoExtension} uses strict stubs: a stub the test never uses fails it
 * with {@code UnnecessaryStubbingException}. So each test stubs exactly what its
 * path through the code needs. See {@link MockitoFailuresDemoTest}.
 */
@ExtendWith(MockitoExtension.class)
class ClientCredentialsServiceTest {

    private static final Client APP = AuthFixtures.client("app", "payments:read", "payments:write");

    @Mock
    ClientRepository clients;

    @Mock
    SecretVerifier secretVerifier;

    @Mock
    TokenGenerator tokenGenerator;

    @Mock
    AuditLog auditLog;

    @Captor
    ArgumentCaptor<AuditEvent> auditEvent;

    private ClientCredentialsService service;

    @BeforeEach
    void createService() {
        service = new ClientCredentialsService(clients, secretVerifier, tokenGenerator, auditLog, FIXED_CLOCK);
    }

    private void givenAppWithValidSecret() {
        when(clients.findById("app")).thenReturn(Optional.of(APP));
        when(secretVerifier.matches(SECRET, APP.secretHash())).thenReturn(true);
    }

    @Test
    void issuesATokenForAValidClient() {
        givenAppWithValidSecret();
        when(tokenGenerator.newTokenValue()).thenReturn("tok-123");

        AccessToken token = service.issue("app", SECRET, Set.of("payments:read"));

        assertEquals("tok-123", token.value());
        assertEquals(Set.of("payments:read"), token.scopes());
        assertEquals(NOW.plus(ClientCredentialsService.TOKEN_LIFETIME), token.expiresAt());

        verify(auditLog).append(auditEvent.capture());
        AuditEvent event = auditEvent.getValue();
        assertEquals(AuditEvent.Type.TOKEN_ISSUED, event.type());
        assertEquals("payments:read", event.detail());
        assertEquals(NOW, event.at());
    }

    @Test
    void grantsAllAllowedScopesWhenNoneAreRequested() {
        givenAppWithValidSecret();
        when(tokenGenerator.newTokenValue()).thenReturn("tok");

        assertEquals(APP.allowedScopes(), service.issue("app", SECRET, Set.of()).scopes());
    }

    @Test
    void unknownClientIsRejectedWithoutCheckingTheSecret() {
        when(clients.findById("ghost")).thenReturn(Optional.empty());

        TokenRequestException e = assertThrows(TokenRequestException.class,
            () -> service.issue("ghost", SECRET, Set.of()));

        assertEquals(TokenRequestException.Error.INVALID_CLIENT, e.error());
        verify(secretVerifier, never()).matches(any(), any());
        verifyNoInteractions(tokenGenerator);
        verify(auditLog).append(argThat(event ->
            event.type() == AuditEvent.Type.TOKEN_DENIED && "invalid_client".equals(event.detail())));
    }

    @Test
    void wrongSecretGivesTheSameErrorAsAnUnknownClient() {
        when(clients.findById("app")).thenReturn(Optional.of(APP));
        when(secretVerifier.matches("wrong", APP.secretHash())).thenReturn(false);

        TokenRequestException e = assertThrows(TokenRequestException.class,
            () -> service.issue("app", "wrong", Set.of()));

        assertEquals(TokenRequestException.Error.INVALID_CLIENT, e.error(), "no hint that the client exists");
        verifyNoInteractions(tokenGenerator);
    }

    @Test
    void disabledClientIsUnauthorized() {
        when(clients.findById("app")).thenReturn(Optional.of(APP.disabled()));
        when(secretVerifier.matches(SECRET, APP.secretHash())).thenReturn(true);

        TokenRequestException e = assertThrows(TokenRequestException.class,
            () -> service.issue("app", SECRET, Set.of()));

        assertEquals(TokenRequestException.Error.UNAUTHORIZED_CLIENT, e.error());
    }

    @Test
    void scopeOutsideTheAllowedSetIsRejected() {
        givenAppWithValidSecret();

        TokenRequestException e = assertThrows(TokenRequestException.class,
            () -> service.issue("app", SECRET, Set.of("payments:read", "admin")));

        assertEquals(TokenRequestException.Error.INVALID_SCOPE, e.error());
        verifyNoInteractions(tokenGenerator);
    }

    @Test
    void checksHappenInTheRightOrder() {
        givenAppWithValidSecret();
        when(tokenGenerator.newTokenValue()).thenReturn("tok");

        service.issue("app", SECRET, Set.of());

        InOrder inOrder = inOrder(clients, secretVerifier, tokenGenerator, auditLog);
        inOrder.verify(clients).findById("app");
        inOrder.verify(secretVerifier).matches(SECRET, APP.secretHash());
        inOrder.verify(tokenGenerator).newTokenValue();
        inOrder.verify(auditLog).append(any());
    }

    @Test
    void aFailingAuditLogIsNotSwallowed() {
        givenAppWithValidSecret();
        when(tokenGenerator.newTokenValue()).thenReturn("tok");
        doThrow(new IllegalStateException("audit store down")).when(auditLog).append(any());

        IllegalStateException e = assertThrows(IllegalStateException.class,
            () -> service.issue("app", SECRET, Set.of()));
        assertEquals("audit store down", e.getMessage());
    }
}
