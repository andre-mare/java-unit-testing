package com.example.module_06.section_01;

import static com.example.module_06.support.AuthFixtures.FIXED_CLOCK;
import static com.example.module_06.support.AuthFixtures.SECRET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import com.example.auth.AccessToken;
import com.example.auth.ClientCredentialsService;
import com.example.auth.InMemoryClientRepository;
import com.example.auth.Sha256SecretVerifier;
import com.example.module_06.support.AuthFixtures;
import com.example.module_06.support.RecordingAuditLog;
import com.example.module_06.support.SequentialTokenGenerator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Module 6.1 lab – {@link TestInfo}: what JUnit knows about the running test.
 * <p>
 * Start here. Declare a parameter of type {@code TestInfo} and JUnit's built-in
 * resolver supplies it, in a constructor, a lifecycle method or a test method.
 * It gives the display name, the tags, the test class and the test method.
 * It replaces JUnit 4's {@code TestName} rule.
 * <ul>
 *   <li>In {@code @BeforeAll}, it describes the class (there is no test method yet).</li>
 *   <li>In the constructor and {@code @BeforeEach}, it describes the test about to run.</li>
 * </ul>
 */
@DisplayName("TestInfo demo")
@Tag("auth")
class TestInfoTest {

    private final TestInfo constructorInfo;

    TestInfoTest(TestInfo testInfo) {
        this.constructorInfo = testInfo;
    }

    @BeforeAll
    static void describesTheClass(TestInfo classInfo) {
        assertEquals("TestInfo demo", classInfo.getDisplayName());
        assertTrue(classInfo.getTestMethod().isEmpty());
    }

    @BeforeEach
    void logsEveryTest(TestInfo info) { // lab: log the display name and tags of each test
        System.out.println("Starting [" + info.getDisplayName() + "] with tags " + info.getTags());
    }

    @Test
    @DisplayName("issues a token")
    @Tag("smoke")
    void displayNameAndTags(TestInfo info) {
        assertEquals("issues a token", info.getDisplayName());
        assertEquals(Set.of("auth", "smoke"), info.getTags(), "class tag plus method tag");
    }

    @Test
    void testClassAndMethod(TestInfo info) {
        assertEquals(TestInfoTest.class, info.getTestClass().orElseThrow());
        assertEquals("testClassAndMethod", info.getTestMethod().orElseThrow().getName());
    }

    @Test
    void constructorSawTheSameTest(TestInfo info) {
        // Per-method lifecycle: a new instance, so the constructor ran for this test.
        assertEquals(info.getDisplayName(), constructorInfo.getDisplayName());
    }

    @Test
    void uniqueTestDataPerTest(TestInfo info) {
        // A common use: derive test data from the test's own name, so tests never clash.
        String clientId = "client-" + info.getTestMethod().orElseThrow().getName();

        InMemoryClientRepository clients = new InMemoryClientRepository();
        clients.save(AuthFixtures.client(clientId, "read"));
        ClientCredentialsService service = new ClientCredentialsService(clients, new Sha256SecretVerifier(),
            new SequentialTokenGenerator(), new RecordingAuditLog(), FIXED_CLOCK);

        AccessToken token = service.issue(clientId, SECRET, Set.of());
        assertEquals("client-uniqueTestDataPerTest", token.clientId());
    }
}
