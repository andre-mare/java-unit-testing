package com.example.password;

import java.util.Set;

/**
 * The outcome of {@link PasswordPolicy#validate(CharSequence)}: every rule the
 * password breaks, all at once, so a user can fix them in one go.
 *
 * @param violations the broken rules; empty when the password is valid
 */
public record ValidationResult(Set<Violation> violations) {

    public ValidationResult {
        violations = Set.copyOf(violations);
    }

    public boolean isValid() {
        return violations.isEmpty();
    }

    public boolean has(Violation violation) {
        return violations.contains(violation);
    }
}
