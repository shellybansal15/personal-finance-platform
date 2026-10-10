package com.finance.exception;

/**
 * ============================================================================
 * [OOP Rubric: Custom Exception Hierarchy - Validation Exception]
 * Thrown when form inputs violate business rules (e.g., negative amount,
 * invalid email format, empty required fields).
 * ============================================================================
 */
public class ValidationException extends AppException {

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }
}
