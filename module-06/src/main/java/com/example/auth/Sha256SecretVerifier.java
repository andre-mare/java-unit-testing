package com.example.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * A {@link SecretVerifier} using SHA-256, compared in constant time.
 * <p>
 * Good enough for this course. Real systems should use a slow, salted hash
 * such as bcrypt, scrypt or Argon2 for stored secrets.
 */
public final class Sha256SecretVerifier implements SecretVerifier {

    @Override
    public boolean matches(CharSequence rawSecret, String secretHash) {
        byte[] actual = hash(rawSecret).getBytes(StandardCharsets.US_ASCII);
        byte[] expected = secretHash.getBytes(StandardCharsets.US_ASCII);
        return MessageDigest.isEqual(actual, expected);
    }

    /** Lower-case hex SHA-256 of the secret, as stored in {@link Client#secretHash()}. */
    public static String hash(CharSequence rawSecret) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(rawSecret.toString().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is always available", e);
        }
    }
}
