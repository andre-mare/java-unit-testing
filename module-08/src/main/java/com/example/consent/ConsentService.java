package com.example.consent;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Manages account-access consents, Module 8 production code.
 * <p>
 * Rules (simplified from PSD2): a consent is requested, then authorised or rejected
 * by the account holder, and may be revoked later. Validity is at most
 * {@link #MAX_VALIDITY} (180 days). Expiry is not a stored status: a consent is
 * active while it is authorised and the clock is before {@code expiresAt}.
 */
public final class ConsentService {

    public static final Duration MAX_VALIDITY = Duration.ofDays(180);

    private final ConsentRepository repository;
    private final Clock clock;
    private final Supplier<String> ids;

    public ConsentService(ConsentRepository repository, Clock clock, Supplier<String> ids) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.ids = Objects.requireNonNull(ids, "ids");
    }

    /** Records a new consent awaiting authorisation; returns it. */
    public Consent request(String clientId, String psuId, Set<String> permissions, Duration validity) {
        if (validity.isNegative() || validity.isZero() || validity.compareTo(MAX_VALIDITY) > 0) {
            throw new IllegalArgumentException("Validity must be more than 0 and at most " + MAX_VALIDITY.toDays() + " days");
        }
        Consent consent = new Consent(ids.get(), clientId, psuId, permissions,
            clock.instant().plus(validity), ConsentStatus.AWAITING_AUTHORISATION);
        repository.save(consent);
        return consent;
    }

    /** The account holder approves. Only a consent awaiting authorisation that has not expired. */
    public Consent authorise(String consentId) {
        Consent consent = find(consentId);
        if (consent.status() != ConsentStatus.AWAITING_AUTHORISATION) {
            throw new IllegalStateException("Cannot authorise a consent that is " + consent.status());
        }
        if (!clock.instant().isBefore(consent.expiresAt())) {
            throw new IllegalStateException("Cannot authorise an expired consent");
        }
        return update(consent.withStatus(ConsentStatus.AUTHORISED));
    }

    /** The account holder refuses a consent awaiting authorisation. */
    public Consent reject(String consentId) {
        Consent consent = find(consentId);
        if (consent.status() != ConsentStatus.AWAITING_AUTHORISATION) {
            throw new IllegalStateException("Cannot reject a consent that is " + consent.status());
        }
        return update(consent.withStatus(ConsentStatus.REJECTED));
    }

    /** The account holder withdraws an authorised consent. Revoking twice is allowed and changes nothing. */
    public Consent revoke(String consentId) {
        Consent consent = find(consentId);
        return switch (consent.status()) {
            case AUTHORISED -> update(consent.withStatus(ConsentStatus.REVOKED));
            case REVOKED -> consent;
            default -> throw new IllegalStateException("Cannot revoke a consent that is " + consent.status());
        };
    }

    /** True if the consent is active now and includes the permission. */
    public boolean allows(String consentId, String permission) {
        Consent consent = find(consentId);
        return consent.isActiveAt(clock.instant()) && consent.permissions().contains(permission);
    }

    /** The account holder's consents that are active now. */
    public List<Consent> activeConsentsFor(String psuId) {
        Instant now = clock.instant();
        return repository.findByPsu(psuId).stream().filter(c -> c.isActiveAt(now)).toList();
    }

    private Consent find(String consentId) {
        return repository.findById(consentId)
            .orElseThrow(() -> new NoSuchElementException("Unknown consent: " + consentId));
    }

    private Consent update(Consent consent) {
        repository.save(consent);
        return consent;
    }
}
