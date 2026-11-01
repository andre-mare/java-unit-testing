package com.example.module_06.section_03;

import static com.example.module_06.support.AuthFixtures.NOW;
import static com.example.module_06.support.AuthFixtures.SECRET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.util.Optional;
import java.util.Set;

import com.example.auth.AccessToken;
import com.example.auth.AuditLog;
import com.example.auth.Client;
import com.example.auth.ClientCredentialsService;
import com.example.auth.ClientRepository;
import com.example.auth.SecretVerifier;
import com.example.auth.TokenGenerator;
import com.example.auth.TokenRequestException;
import com.example.module_06.support.AuthFixtures;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Module 6.3 – {@code @InjectMocks}: let Mockito build the object under test.
 * <p>
 * Mockito calls the biggest constructor of {@link ClientCredentialsService} and
 * passes the {@code @Mock} fields by type. Any constructor argument without a
 * matching mock gets {@code null}, which is why even the {@link Clock} has to be
 * a mock here, with {@code instant()} stubbed.
 * <p>
 * Compare with {@link ClientCredentialsServiceTest}, which calls the constructor
 * itself and passes a real fixed clock. That is one line more, but the wiring is
 * visible and checked by the compiler. Many teams prefer it for that reason.
 * <p>
 * Also note: a method you did not stub returns an "empty" value, so
 * {@code clients.findById(...)} returns {@code Optional.empty()} in
 * {@code unknownClient()} without any stubbing.
 */
@ExtendWith(MockitoExtension.class)
class InjectMocksTest {

    private static final Client APP = AuthFixtures.client("app", "read");

    @Mock
    ClientRepository clients;

    @Mock
    SecretVerifier secretVerifier;

    @Mock
    TokenGenerator tokenGenerator;

    @Mock
    AuditLog auditLog;

    @Mock
    Clock clock;

    @InjectMocks
    ClientCredentialsService service;

    @Test
    void mocksWereInjectedThroughTheConstructor() {
        when(clock.instant()).thenReturn(NOW);
        when(clients.findById("app")).thenReturn(Optional.of(APP));
        when(secretVerifier.matches(SECRET, APP.secretHash())).thenReturn(true);
        when(tokenGenerator.newTokenValue()).thenReturn("tok");

        AccessToken token = service.issue("app", SECRET, Set.of());

        assertEquals("tok", token.value());
        assertEquals(NOW.plus(ClientCredentialsService.TOKEN_LIFETIME), token.expiresAt());
    }

    @Test
    void unknownClient() {
        when(clock.instant()).thenReturn(NOW); // used for the audit event of the denial

        TokenRequestException e = assertThrows(TokenRequestException.class,
            () -> service.issue("nobody", SECRET, Set.of()));
        assertEquals(TokenRequestException.Error.INVALID_CLIENT, e.error());
    }
}
