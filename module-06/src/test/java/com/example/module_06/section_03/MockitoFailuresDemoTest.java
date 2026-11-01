package com.example.module_06.section_03;

import static com.example.module_06.support.AuthFixtures.FIXED_CLOCK;
import static com.example.module_06.support.AuthFixtures.SECRET;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import com.example.auth.AuditLog;
import com.example.auth.Client;
import com.example.auth.ClientCredentialsService;
import com.example.auth.ClientRepository;
import com.example.auth.SecretVerifier;
import com.example.auth.TokenGenerator;
import com.example.module_06.support.AuthFixtures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Module 6.3 – every test FAILS on purpose with a typical Mockito error. Read each message.
 * <ul>
 *   <li>{@code UnnecessaryStubbingException}: a stub the test never used;</li>
 *   <li>{@code WantedButNotInvoked}: {@code verify} expected a call that never happened;</li>
 *   <li>{@code PotentialStubbingProblem}: a stubbed method was called with different
 *       arguments, which strict stubs treat as a probable bug in the test.</li>
 * </ul>
 * Tagged {@code expected-failure-tests} and excluded from the normal build. Run it with:
 * <pre>mvn test -Dtest=MockitoFailuresDemoTest -DexcludedGroups=none</pre>
 */
@Tag("expected-failure-tests")
@ExtendWith(MockitoExtension.class)
class MockitoFailuresDemoTest {

    private static final Client APP = AuthFixtures.client("app", "read");

    @Mock
    ClientRepository clients;

    @Mock
    SecretVerifier secretVerifier;

    @Mock
    TokenGenerator tokenGenerator;

    @Mock
    AuditLog auditLog;

    private ClientCredentialsService service;

    @BeforeEach
    void createService() {
        service = new ClientCredentialsService(clients, secretVerifier, tokenGenerator, auditLog, FIXED_CLOCK);
    }

    @Test
    void unnecessaryStubbing() {
        when(tokenGenerator.newTokenValue()).thenReturn("never used"); // the unknown client never gets that far
        try {
            service.issue("ghost", SECRET, Set.of());
        } catch (RuntimeException expected) {
            // invalid_client, as intended
        }
    }

    @Test
    void wantedButNotInvoked() {
        try {
            service.issue("ghost", SECRET, Set.of());
        } catch (RuntimeException expected) {
            // invalid_client
        }
        verify(tokenGenerator).newTokenValue(); // wrong expectation: no token is made for an unknown client
    }

    @Test
    void stubbedWithDifferentArguments() {
        when(clients.findById("app")).thenReturn(Optional.of(APP));
        when(secretVerifier.matches("the-right-secret", APP.secretHash())).thenReturn(true);

        service.issue("app", SECRET, Set.of()); // calls matches(SECRET, ...) instead
    }
}
