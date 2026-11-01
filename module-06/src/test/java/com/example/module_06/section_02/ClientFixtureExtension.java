package com.example.module_06.section_02;

import java.lang.reflect.Method;
import java.util.Set;

import com.example.auth.Client;
import com.example.auth.ClientCredentialsService;
import com.example.auth.InMemoryClientRepository;
import com.example.auth.Sha256SecretVerifier;
import com.example.module_06.support.AuthFixtures;
import com.example.module_06.support.RecordingAuditLog;
import com.example.module_06.support.SequentialTokenGenerator;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * Module 6.2 lab – a resolver that wires a small object graph for each test.
 * <p>
 * Supported parameter types:
 * <ul>
 *   <li>{@link InMemoryClientRepository} and {@link RecordingAuditLog}: one of each per test;</li>
 *   <li>{@link Client}: a new client with the test secret, already saved in that repository
 *       (configure it with {@link ClientWithScopes});</li>
 *   <li>{@link ClientCredentialsService}: wired to that same repository and audit log.</li>
 * </ul>
 * "One per test" works through the {@link ExtensionContext.Store}: values are kept
 * in the store of the current test's context, so every parameter of one test, and
 * of its {@code @BeforeEach} methods, gets the same instances, while the next test
 * starts with empty ones. {@code computeIfAbsent} is the JUnit 6 name; the older
 * {@code getOrComputeIfAbsent} is deprecated.
 * <p>
 * Use it on method parameters. Constructor parameters resolve in a different
 * context, so they would not share these instances.
 */
public class ClientFixtureExtension implements ParameterResolver {

    private static final Namespace NAMESPACE = Namespace.create(ClientFixtureExtension.class);

    private static final Set<Class<?>> SUPPORTED_TYPES = Set.of(
        InMemoryClientRepository.class, RecordingAuditLog.class, Client.class, ClientCredentialsService.class);

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return SUPPORTED_TYPES.contains(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> type = parameterContext.getParameter().getType();
        if (type == InMemoryClientRepository.class) {
            return repository(extensionContext);
        }
        if (type == RecordingAuditLog.class) {
            return auditLog(extensionContext);
        }
        if (type == ClientCredentialsService.class) {
            return new ClientCredentialsService(repository(extensionContext), new Sha256SecretVerifier(),
                new SequentialTokenGenerator(), auditLog(extensionContext), AuthFixtures.FIXED_CLOCK);
        }
        return registerClient(parameterContext, extensionContext);
    }

    private static Client registerClient(ParameterContext parameterContext, ExtensionContext extensionContext) {
        ClientWithScopes config = parameterContext.findAnnotation(ClientWithScopes.class).orElse(null);
        String[] scopes = config == null ? new String[] { "read" } : config.value();

        // Unique per test and per parameter: "client-<testMethod>-<parameterIndex>".
        String testName = extensionContext.getTestMethod().map(Method::getName).orElse("class");
        Client client = AuthFixtures.client("client-" + testName + "-" + parameterContext.getIndex(), scopes);
        if (config != null && !config.enabled()) {
            client = client.disabled();
        }
        repository(extensionContext).save(client);
        return client;
    }

    private static InMemoryClientRepository repository(ExtensionContext context) {
        return context.getStore(NAMESPACE)
            .computeIfAbsent("repository", key -> new InMemoryClientRepository(), InMemoryClientRepository.class);
    }

    private static RecordingAuditLog auditLog(ExtensionContext context) {
        return context.getStore(NAMESPACE)
            .computeIfAbsent("auditLog", key -> new RecordingAuditLog(), RecordingAuditLog.class);
    }
}
