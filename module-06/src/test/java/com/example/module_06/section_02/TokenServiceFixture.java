package com.example.module_06.section_02;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import com.example.auth.Client;
import com.example.auth.ClientCredentialsService;
import com.example.auth.InMemoryClientRepository;
import com.example.auth.RandomTokenGenerator;
import com.example.auth.Sha256SecretVerifier;
import com.example.module_06.support.AuthFixtures;
import com.example.module_06.support.RecordingAuditLog;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * Module 6.2 – an extension meant for {@code @RegisterExtension}.
 * <p>
 * Unlike {@link ClientFixtureExtension}, it takes constructor arguments (here
 * the moment "now"), and tests can call its methods directly. Both are only
 * possible because the test class creates the instance itself.
 * <p>
 * It also implements {@link BeforeEachCallback}, a lifecycle callback, to give
 * each test a fresh repository and audit log. Lifecycle callbacks belong to the
 * extension model, beyond this course's scope; this one just shows the idea.
 * Keeping state in fields like this is fine for sequential tests, not for
 * parallel ones (Module 9.5).
 */
public class TokenServiceFixture implements ParameterResolver, BeforeEachCallback {

    private final Clock clock;
    private InMemoryClientRepository repository;
    private RecordingAuditLog auditLog;

    public TokenServiceFixture(Instant now) {
        this.clock = Clock.fixed(now, ZoneOffset.UTC);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        repository = new InMemoryClientRepository();
        auditLog = new RecordingAuditLog();
    }

    /** Registers an enabled client with the test secret. */
    public Client register(String clientId, String... scopes) {
        Client client = AuthFixtures.client(clientId, scopes);
        repository.save(client);
        return client;
    }

    public RecordingAuditLog auditLog() {
        return auditLog;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType() == ClientCredentialsService.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return new ClientCredentialsService(repository, new Sha256SecretVerifier(), new RandomTokenGenerator(),
            auditLog, clock);
    }
}
