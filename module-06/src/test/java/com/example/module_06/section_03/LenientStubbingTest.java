package com.example.module_06.section_03;

import static com.example.module_06.support.AuthFixtures.FIXED_CLOCK;
import static com.example.module_06.support.AuthFixtures.SECRET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import com.example.auth.AuditLog;
import com.example.auth.Client;
import com.example.auth.ClientCredentialsService;
import com.example.auth.ClientRepository;
import com.example.auth.SecretVerifier;
import com.example.auth.TokenRequestException;
import com.example.module_06.support.AuthFixtures;
import com.example.module_06.support.SequentialTokenGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Module 6.3 – shared stubs in {@code @BeforeEach}, and relaxing strict stubs.
 * <p>
 * The {@code @BeforeEach} stubs a valid "app" client for every test. The
 * {@code unknownClient} test never touches those stubs, which strict stubs would
 * report as {@code UnnecessaryStubbingException}. Two ways out:
 * <ul>
 *   <li>{@code @MockitoSettings(strictness = Strictness.LENIENT)} for the class, as here;</li>
 *   <li>or, more precisely, mark single stubs: {@code lenient().when(...).thenReturn(...)}.</li>
 * </ul>
 * Prefer keeping strict stubs and stubbing per test; leniency hides stubs that
 * no longer matter. Remove the annotation to see the exception.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LenientStubbingTest {

    private static final Client APP = AuthFixtures.client("app", "read");

    @Mock
    ClientRepository clients;

    @Mock
    SecretVerifier secretVerifier;

    @Mock
    AuditLog auditLog;

    private ClientCredentialsService service;

    @BeforeEach
    void commonStubs() {
        when(clients.findById("app")).thenReturn(Optional.of(APP));
        when(secretVerifier.matches(SECRET, APP.secretHash())).thenReturn(true);
        service = new ClientCredentialsService(clients, secretVerifier, new SequentialTokenGenerator(), auditLog,
            FIXED_CLOCK);
    }

    @Test
    void usesBothStubs() {
        assertEquals("token-1", service.issue("app", SECRET, Set.of()).value());
    }

    @Test
    void unknownClient() {
        TokenRequestException e = assertThrows(TokenRequestException.class,
            () -> service.issue("ghost", SECRET, Set.of()));
        assertEquals(TokenRequestException.Error.INVALID_CLIENT, e.error());
    }
}
