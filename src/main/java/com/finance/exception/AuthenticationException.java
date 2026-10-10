package com.finance.exception;

/**
 * ============================================================================
 * [OOP Rubric: Custom Exception Hierarchy - Authentication Exception]
 * Thrown when user credentials are invalid, accounts are locked,
 * or session has expired.
 * ============================================================================
 */
public class AuthenticationException extends AppException {

    public AuthenticationException(String message) {
        super("AUTH_ERROR", message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super("AUTH_ERROR", message, cause);
    }
}
