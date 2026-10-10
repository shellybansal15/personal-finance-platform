package com.finance.exception;

/**
 * ============================================================================
 * [OOP Rubric: Custom Exception Hierarchy - Resource Not Found]
 * Thrown when an expense, budget, goal, or user cannot be located by ID.
 * ============================================================================
 */
public class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String message) {
        super("NOT_FOUND", message);
    }
}
