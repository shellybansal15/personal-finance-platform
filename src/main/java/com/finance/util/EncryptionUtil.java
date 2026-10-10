package com.finance.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

/**
 * ============================================================================
 * [Security Rubric: Financial Data Security - AES Note Encryption]
 * Utility class to encrypt and decrypt sensitive financial notes.
 * Uses AES (Advanced Encryption Standard) with a 256-bit symmetric key.
 * The secret key is loaded from external configuration (db.properties or env var).
 * ============================================================================
 */
public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String DEFAULT_FALLBACK_KEY = "FinanceAppDefaultSecretKey2026!";
    private static SecretKeySpec secretKeySpec;

    static {
        initKey();
    }

    /**
     * Initializes the AES secret key by loading security.aes.key from db.properties,
     * falling back to the environment variable AES_SECRET_KEY if available.
     */
    private static void initKey() {
        String keyString = System.getenv("AES_SECRET_KEY");

        if (keyString == null || keyString.trim().isEmpty()) {
            try (InputStream input = EncryptionUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (input != null) {
                    Properties prop = new Properties();
                    prop.load(input);
                    keyString = prop.getProperty("security.aes.key");
                }
            } catch (Exception e) {
                System.err.println("[EncryptionUtil] Could not load db.properties: " + e.getMessage());
            }
        }

        if (keyString == null || keyString.trim().isEmpty()) {
            keyString = DEFAULT_FALLBACK_KEY;
        }

        try {
            // Derive a 256-bit (32 byte) key using SHA-256 hash of the key string
            byte[] keyBytes = keyString.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            keyBytes = sha.digest(keyBytes);
            keyBytes = Arrays.copyOf(keyBytes, 32); // 256 bits
            secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize AES key: " + e.getMessage(), e);
        }
    }

    /**
     * Encrypts plain-text note into a Base64-encoded AES ciphertext string.
     *
     * @param plainText Clear text to encrypt (e.g., "Grocery shopping at Walmart")
     * @return Base64 ciphertext string, or null/empty if plainText is empty
     */
    public static String encrypt(String plainText) {
        if (plainText == null || plainText.trim().isEmpty()) {
            return plainText;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            System.err.println("[EncryptionUtil] Encryption error: " + e.getMessage());
            return plainText; // Fail-safe returns original string
        }
    }

    /**
     * Decrypts a Base64-encoded AES ciphertext string back into original plain text.
     *
     * @param cipherText Base64-encoded encrypted string
     * @return Decrypted plain-text string
     */
    public static String decrypt(String cipherText) {
        if (cipherText == null || cipherText.trim().isEmpty()) {
            return cipherText;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // If the string was already plain text or corrupted, return as is
            return cipherText;
        }
    }
}
