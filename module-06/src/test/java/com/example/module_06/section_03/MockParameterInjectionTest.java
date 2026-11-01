package com.example.module_06.section_03;

import static com.example.module_06.support.AuthFixtures.FIXED_CLOCK;
import static com.example.module_06.support.AuthFixtures.SECRET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import com.example.auth.AuditEvent;
import com.example.auth.AuditLog;
import com.example.auth.ClientCredentialsService;
import com.example.auth.ClientRepository;
import com.example.auth.Sha256SecretVerifier;
import com.example.module_06.support.AuthFixtures;
import com.example.module_06.support.SequentialTokenGenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Module 6.3 – {@code MockitoExtension} is also a {@code ParameterResolver}.
 * <p>
 * This ties 6.2 and 6.3 together: annotate a test-method parameter with
 * {@code @Mock} and the extension resolves it, side by side with JUnit's
 * built-in resolvers such as {@link TestInfo}. Mocks scoped to one test need no field.
 * <p>
 * The test also mixes mocks with real objects: only the repository and the audit
 * log are mocked, while the secret check and the token generator are real. Mock
 * what you need to control or observe, nothing more.
 */
@ExtendWith(MockitoExtension.class)
class MockParameterInjectionTest {

    @Test
    void mocksAsTestMethodParameters(@Mock ClientRepository clients, @Mock AuditLog auditLog, TestInfo info) {
        when(clients.findById("app")).thenReturn(Optional.of(AuthFixtures.client("app", "read")));
        ClientCredentialsService service = new ClientCredentialsService(clients, new Sha256SecretVerifier(),
            new SequentialTokenGenerator(), auditLog, FIXED_CLOCK);

        assertEquals("token-1", service.issue("app", SECRET, Set.of()).value());

        verify(auditLog).append(any(AuditEvent.class));
        assertTrue(info.getDisplayName().startsWith("mocksAsTestMethodParameters"));
    }
}
