package com.example.module_06.section_01;

import static com.example.module_06.support.AuthFixtures.FIXED_CLOCK;
import static com.example.module_06.support.AuthFixtures.SECRET;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.example.auth.AccessToken;
import com.example.auth.ClientCredentialsService;
import com.example.auth.InMemoryClientRepository;
import com.example.auth.RandomTokenGenerator;
import com.example.auth.Sha256SecretVerifier;
import com.example.module_06.support.AuthFixtures;
import com.example.module_06.support.RecordingAuditLog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;

/**
 * Module 6.1 lab – {@link TestReporter}: publish extra information about a test run.
 * <p>
 * Use it instead of {@code System.out}: entries are attached to the test they
 * belong to, so IDEs and reports can show them next to that test (IntelliJ shows
 * them in the test's output). Files and directories go to JUnit's reporting
 * output directory, configurable with {@code junit.platform.reporting.output.dir}.
 * <p>
 * Never publish secrets: reports are often stored and shared. The receipt test
 * proves the token value stays out.
 */
class TestReporterTest {

    private final RecordingAuditLog auditLog = new RecordingAuditLog();
    private ClientCredentialsService service;

    @BeforeEach
    void createService() {
        InMemoryClientRepository clients = new InMemoryClientRepository();
        clients.save(AuthFixtures.client("reporting-app", "payments:read", "accounts:read"));
        service = new ClientCredentialsService(clients, new Sha256SecretVerifier(), new RandomTokenGenerator(),
            auditLog, FIXED_CLOCK);
    }

    @Test
    void publishesASingleValue(TestReporter reporter) {
        reporter.publishEntry("token service started");
    }

    @Test
    void publishesAKeyValuePair(TestReporter reporter) {
        reporter.publishEntry("token lifetime", ClientCredentialsService.TOKEN_LIFETIME.toString());
    }

    @Test
    void publishesATokenReceiptWithoutTheSecret(TestReporter reporter) { // lab
        AccessToken token = service.issue("reporting-app", SECRET, Set.of("payments:read"));

        Map<String, String> receipt = new LinkedHashMap<>();
        receipt.put("client", token.clientId());
        receipt.put("scopes", String.join(" ", new TreeSet<>(token.scopes())));
        receipt.put("expiresAt", token.expiresAt().toString());
        reporter.publishEntry(receipt);

        assertFalse(receipt.containsValue(token.value()), "the token value must never reach a report");
    }

    @Test
    void publishesAFile(TestReporter reporter) {
        service.issue("reporting-app", SECRET, Set.of());

        reporter.publishFile("audit.txt", MediaType.TEXT_PLAIN_UTF_8,
            file -> Files.writeString(file, auditLog.last().toString()));
    }

    @Test
    void publishesADirectory(TestReporter reporter) {
        service.issue("reporting-app", SECRET, Set.of("accounts:read"));

        reporter.publishDirectory("audit", dir -> {
            Files.writeString(dir.resolve("event-type.txt"), auditLog.last().type().name());
            Files.writeString(dir.resolve("event-detail.txt"), auditLog.last().detail());
        });
    }
}
