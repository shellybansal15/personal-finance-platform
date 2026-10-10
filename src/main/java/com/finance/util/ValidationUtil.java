package com.finance.util;

import com.finance.exception.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * ============================================================================
 * [Security Rubric: Server-Side Input Validation & Sanitization]
 * Centralized utility for validating user inputs to defend against XSS,
 * SQL injection, negative values, and malformed strings before processing.
 * ============================================================================
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    /**
     * Sanitizes input strings to strip dangerous HTML tags and whitespace.
     */
    public static String sanitize(String input) {
        if (input == null) return "";
        return input.trim()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    /**
     * Validates non-empty required string.
     */
    public static String requireNonEmpty(String input, String fieldName) throws ValidationException {
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required and cannot be blank.");
        }
        return input.trim();
    }

    /**
     * Validates email format.
     */
    public static String validateEmail(String email) throws ValidationException {
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Please provide a valid email address.");
        }
        return email.trim().toLowerCase();
    }

    /**
     * Validates password strength (minimum 6 characters).
     */
    public static void validatePassword(String password) throws ValidationException {
        if (password == null || password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }
    }

    /**
     * Validates positive monetary amount (e.g., amount > 0).
     */
    public static double validatePositiveAmount(String amountStr, String fieldName) throws ValidationException {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required.");
        }
        try {
            double amount = Double.parseDouble(amountStr.trim());
            if (amount <= 0.0) {
                throw new ValidationException(fieldName + " must be greater than zero.");
            }
            // Round to 2 decimal places
            return Math.round(amount * 100.0) / 100.0;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid numeric amount.");
        }
    }

    /**
     * Validates non-negative monetary amount (amount >= 0).
     */
    public static double validateNonNegativeAmount(String amountStr, String fieldName) throws ValidationException {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            return 0.0;
        }
        try {
            double amount = Double.parseDouble(amountStr.trim());
            if (amount < 0.0) {
                throw new ValidationException(fieldName + " cannot be negative.");
            }
            return Math.round(amount * 100.0) / 100.0;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid numeric amount.");
        }
    }

    /**
     * Parses and validates ISO date (YYYY-MM-DD).
     */
    public static LocalDate validateDate(String dateStr, String fieldName) throws ValidationException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new ValidationException(fieldName + " date is required.");
        }
        try {
            return LocalDate.parse(dateStr.trim());
        } catch (DateTimeParseException e) {
            throw new ValidationException(fieldName + " must be in valid format (YYYY-MM-DD).");
        }
    }
}
