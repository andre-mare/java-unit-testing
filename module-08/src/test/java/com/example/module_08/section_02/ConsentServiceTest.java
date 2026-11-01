package com.example.module_08.section_02;

import static com.example.module_08.support.ConsentFixtures.CLIENT;
import static com.example.module_08.support.ConsentFixtures.OTHER_PSU;
import static com.example.module_08.support.ConsentFixtures.PSU;
import static com.example.module_08.support.ConsentFixtures.READ_ACCOUNTS;
import static com.example.module_08.support.ConsentFixtures.READ_BALANCES;
import static com.example.module_08.support.ConsentFixtures.READ_TRANSACTIONS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.Set;

import com.example.consent.Consent;
import com.example.consent.ConsentRepository;
import com.example.consent.ConsentService;
import com.example.consent.ConsentStatus;
import com.example.module_08.support.ConsentFixtures;
import com.example.module_08.support.MutableClock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassTemplate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Module 8.2 lab – one consent test suite, run against every repository.
 * <p>
 * {@code @ClassTemplate} plus the provider make the whole class (including the
 * {@code @Nested} class) run twice: {@code [1] in-memory} and {@code [2] file-backed}.
 * If the file implementation loses a field or a status, the second run fails and
 * the first passes, which points straight at the repository.
 */
@ClassTemplate
@ExtendWith(ConsentRepositoryProvider.class)
class ConsentServiceTest implements RepositoryAware {

    private static final Duration NINETY_DAYS = Duration.ofDays(90);

    private RepositoryFixture storage;
    private MutableClock clock;
    private ConsentService service;

    @Override
    public void use(RepositoryFixture repository) {
        this.storage = repository;
    }

    @BeforeEach
    void createService() {
        clock = new MutableClock(ConsentFixtures.START);
        service = new ConsentService(storage.repository(), clock, ConsentFixtures.sequentialIds());
    }

    private Consent requestBalances() {
        return service.request(CLIENT, PSU, Set.of(READ_ACCOUNTS, READ_BALANCES), NINETY_DAYS);
    }

    @Test
    void startsEmpty() {
        assertEquals(0, storage.repository().count(), storage.name() + " should start empty");
    }

    @Test
    void aRequestAwaitsAuthorisation() {
        Consent consent = requestBalances();
        assertEquals("consent-1", consent.id());
        assertEquals(ConsentStatus.AWAITING_AUTHORISATION, consent.status());
        assertEquals(ConsentFixtures.START.plus(NINETY_DAYS), consent.expiresAt());
        assertFalse(service.allows(consent.id(), READ_BALANCES));
    }

    @Test
    void storedConsentsSurviveAReopen() {
        Consent consent = requestBalances();
        service.authorise(consent.id());

        ConsentRepository reopened = storage.reopen().get();
        Consent stored = reopened.findById(consent.id()).orElseThrow();
        assertEquals(consent.withStatus(ConsentStatus.AUTHORISED), stored, "every field must round-trip");
    }

    @Test
    void validityIsLimited() {
        assertThrows(IllegalArgumentException.class,
            () -> service.request(CLIENT, PSU, Set.of(READ_ACCOUNTS), ConsentService.MAX_VALIDITY.plusDays(1)));
        assertThrows(IllegalArgumentException.class,
            () -> service.request(CLIENT, PSU, Set.of(READ_ACCOUNTS), Duration.ZERO));
        assertEquals(0, storage.repository().count(), "rejected requests are not stored");
    }

    @Test
    void anUnknownIdIsReported() {
        assertThrows(NoSuchElementException.class, () -> service.authorise("consent-404"));
    }

    @Test
    void aRejectedConsentCannotBeAuthorised() {
        Consent consent = requestBalances();
        service.reject(consent.id());
        assertThrows(IllegalStateException.class, () -> service.authorise(consent.id()));
    }

    @Test
    void anExpiredRequestCannotBeAuthorised() {
        Consent consent = requestBalances();
        clock.advance(NINETY_DAYS);
        assertThrows(IllegalStateException.class, () -> service.authorise(consent.id()));
    }

    @Nested
    class OnceAuthorised {

        private Consent consent;

        @BeforeEach
        void authorise() {
            consent = service.authorise(requestBalances().id());
        }

        @Test
        void grantsOnlyItsPermissions() {
            assertTrue(service.allows(consent.id(), READ_BALANCES));
            assertFalse(service.allows(consent.id(), READ_TRANSACTIONS));
        }

        @Test
        void endsWhenItExpires() {
            clock.advance(NINETY_DAYS.minusSeconds(1));
            assertTrue(service.allows(consent.id(), READ_BALANCES));
            clock.advance(Duration.ofSeconds(1));
            assertFalse(service.allows(consent.id(), READ_BALANCES));
        }

        @Test
        void revokingEndsAccessAndCanBeRepeated() {
            service.revoke(consent.id());
            assertFalse(service.allows(consent.id(), READ_BALANCES));
            assertEquals(ConsentStatus.REVOKED, service.revoke(consent.id()).status());
        }

        @Test
        void listsOnlyActiveConsentsOfThatPerson() {
            Consent pending = requestBalances();
            Consent otherPerson = service.authorise(
                service.request(CLIENT, OTHER_PSU, Set.of(READ_ACCOUNTS), NINETY_DAYS).id());

            assertThat(service.activeConsentsFor(PSU)).extracting(Consent::id)
                .containsExactly(consent.id())
                .doesNotContain(pending.id(), otherPerson.id());
            assertEquals(3, storage.repository().count());
        }
    }
}
