package com.example.auth;

/** Checks a raw client secret against its stored hash. */
public interface SecretVerifier {

    boolean matches(CharSequence rawSecret, String secretHash);
}
