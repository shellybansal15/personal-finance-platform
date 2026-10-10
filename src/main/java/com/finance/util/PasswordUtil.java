package com.finance.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * ============================================================================
 * [Security Rubric: BCrypt Password Hashing]
 * Utility class for hashing passwords and verifying incoming credentials.
 * BCrypt automatically creates a random cryptographic salt for each password
 * and computes 10 hashing rounds (work factor), preventing rainbow-table attacks.
 * ============================================================================
 */
public class PasswordUtil {

    // Number of log rounds (work factor) for BCrypt computation
    private static final int BCRYPT_LOG_ROUNDS = 10;

    /**
     * Hashes a raw plain-text password using BCrypt with a freshly generated salt.
     *
     * @param plainPassword Raw password entered by user
     * @return 60-character BCrypt hashed string
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password to hash cannot be null or empty.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_LOG_ROUNDS));
    }

    /**
     * Verifies if a raw plain-text password matches a stored BCrypt hash.
     *
     * @param plainPassword Raw password entered during login
     * @param hashedPassword Stored password hash from database
     * @return true if password matches, false otherwise
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Returns false if hash format is invalid or corrupted
            return false;
        }
    }
}
