package com.finance.exception;

/**
 * ============================================================================
 * [OOP Rubric: Custom Exception Hierarchy - Unauthorized Exception]
 * Thrown when a user attempts to access an admin endpoint or access
 * another user's private financial records.
 * ============================================================================
 */
public class UnauthorizedException extends AppException {

    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message);
    }
}
