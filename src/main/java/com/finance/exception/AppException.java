package com.finance.exception;

/**
 * ============================================================================
 * [OOP Rubric: Custom Exception Hierarchy - Base Exception]
 * AppException is the root checked/runtime exception for the application.
 * All domain-specific errors inherit from this class to allow centralized
 * error handling across controllers and views.
 * ============================================================================
 */
public class AppException extends RuntimeException {

    private final String errorCode;

    public AppException(String message) {
        super(message);
        this.errorCode = "APP_ERROR";
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "APP_ERROR";
    }

    public AppException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AppException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
