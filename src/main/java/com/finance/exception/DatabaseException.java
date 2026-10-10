package com.finance.exception;

/**
 * ============================================================================
 * [OOP Rubric: Custom Exception Hierarchy - Database Exception]
 * Wraps low-level SQLExceptions into domain-friendly errors to keep
 * DAOs and service layers decoupled from raw JDBC errors.
 * ============================================================================
 */
public class DatabaseException extends AppException {

    public DatabaseException(String message) {
        super("DATABASE_ERROR", message);
    }

    public DatabaseException(String message, Throwable cause) {
        super("DATABASE_ERROR", message, cause);
    }
}
