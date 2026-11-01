package com.example.consent;

/** Lifecycle of an account-access consent, simplified from PSD2 / Berlin Group. */
public enum ConsentStatus {

    /** Requested by the third party, not yet approved by the account holder. */
    AWAITING_AUTHORISATION,

    /** Approved; valid until it expires. */
    AUTHORISED,

    /** Withdrawn by the account holder. Final. */
    REVOKED,

    /** Never approved, or refused. Final. */
    REJECTED
}
