package com.example.auth;

import java.security.SecureRandom;
import java.util.Base64;

/** 256 random bits, Base64url-encoded without padding (43 characters). */
public final class RandomTokenGenerator implements TokenGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String newTokenValue() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
