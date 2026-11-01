package com.example.scope;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * An OAuth 2.0 scope value (RFC 6749, section 3.3), Module 8 production code.
 * <p>
 * A scope string is a list of scope tokens separated by single spaces. Tokens are
 * case-sensitive, order does not matter, and each character must be printable ASCII
 * except space, {@code "} and {@code \} (%x21 / %x23-5B / %x5D-7E).
 * This class is strict: no leading, trailing or double spaces.
 */
public final class ScopeSet {

    private final Set<String> tokens;

    private ScopeSet(Set<String> tokens) {
        this.tokens = tokens;
    }

    /**
     * Parses a scope string.
     *
     * @throws IllegalArgumentException when the string is empty or breaks the grammar
     */
    public static ScopeSet parse(String scope) {
        Objects.requireNonNull(scope, "scope");
        if (scope.isEmpty()) {
            throw new IllegalArgumentException("Scope must not be empty");
        }
        Set<String> tokens = new TreeSet<>();
        for (String token : scope.split(" ", -1)) {
            if (token.isEmpty()) {
                throw new IllegalArgumentException("Scope tokens are separated by exactly one space: '" + scope + "'");
            }
            for (char c : token.toCharArray()) {
                if (!isScopeChar(c)) {
                    throw new IllegalArgumentException("Character not allowed in a scope token: '" + c + "'");
                }
            }
            tokens.add(token);
        }
        return new ScopeSet(Set.copyOf(tokens));
    }

    public static ScopeSet of(String... tokens) {
        return parse(String.join(" ", tokens));
    }

    static boolean isScopeChar(char c) {
        return c == 0x21 || (c >= 0x23 && c <= 0x5B) || (c >= 0x5D && c <= 0x7E);
    }

    public int size() {
        return tokens.size();
    }

    public boolean contains(String token) {
        return tokens.contains(token);
    }

    /** True if every token of {@code other} is also in this set. */
    public boolean containsAll(ScopeSet other) {
        return tokens.containsAll(other.tokens);
    }

    /** The tokens in both sets, or empty if there are none. Used to narrow a requested scope. */
    public Set<String> intersect(Collection<String> allowed) {
        Set<String> result = new TreeSet<>(tokens);
        result.retainAll(allowed);
        return Set.copyOf(result);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ScopeSet other && tokens.equals(other.tokens);
    }

    @Override
    public int hashCode() {
        return tokens.hashCode();
    }

    /** The tokens sorted alphabetically, separated by spaces. */
    @Override
    public String toString() {
        return String.join(" ", new TreeSet<>(tokens));
    }

    /** Helper for log output and tests: the tokens as a sorted array. */
    public String[] toArray() {
        String[] array = tokens.toArray(String[]::new);
        Arrays.sort(array);
        return array;
    }
}
