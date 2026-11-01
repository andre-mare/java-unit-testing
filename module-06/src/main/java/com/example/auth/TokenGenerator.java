package com.example.auth;

/** Produces new, unguessable access token values. */
@FunctionalInterface
public interface TokenGenerator {

    String newTokenValue();
}
