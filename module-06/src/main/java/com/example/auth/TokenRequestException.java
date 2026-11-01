package com.example.auth;

/** A refused token request, carrying an OAuth 2.0 error code (RFC 6749, section 5.2). */
public class TokenRequestException extends RuntimeException {

    public enum Error {
        INVALID_CLIENT("invalid_client"),
        UNAUTHORIZED_CLIENT("unauthorized_client"),
        INVALID_SCOPE("invalid_scope");

        private final String code;

        Error(String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }
    }

    private static final long serialVersionUID = 1L;

    private final Error error;

    public TokenRequestException(Error error) {
        super(error.code());
        this.error = error;
    }

    public Error error() {
        return error;
    }
}
