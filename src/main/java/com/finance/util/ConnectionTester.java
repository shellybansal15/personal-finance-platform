package com.finance.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 * Simple standalone runner to verify database connectivity and utility functions.
 * Run this directly from command line or IDE to verify stage (b).
 */
public class ConnectionTester {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" Finance Platform - Stage (b) Verification Test  ");
        System.out.println("==================================================");

        // 1. Test BCrypt Password Hashing
        String adminPlain = "Admin@123";
        String adminHash = PasswordUtil.hashPassword(adminPlain);
        System.out.println("[BCrypt] Admin password: " + adminPlain);
        System.out.println("[BCrypt] Generated Hash: " + adminHash);
        boolean adminCheck = PasswordUtil.checkPassword(adminPlain, adminHash);
        System.out.println("[BCrypt] Verification status: " + (adminCheck ? "SUCCESS" : "FAILED"));

        String userPlain = "User@123";
        String userHash = PasswordUtil.hashPassword(userPlain);
        System.out.println("[BCrypt] User password: " + userPlain);
        System.out.println("[BCrypt] Generated Hash: " + userHash);
        boolean userCheck = PasswordUtil.checkPassword(userPlain, userHash);
        System.out.println("[BCrypt] Verification status: " + (userCheck ? "SUCCESS" : "FAILED"));

        // 2. Test AES Encryption for Expense Notes
        String sampleNote = "Weekly Grocery shopping at Supermarket - Fresh veggies";
        String encrypted = EncryptionUtil.encrypt(sampleNote);
        String decrypted = EncryptionUtil.decrypt(encrypted);
        System.out.println("\n[AES-256] Sample Note: " + sampleNote);
        System.out.println("[AES-256] Encrypted Ciphertext: " + encrypted);
        System.out.println("[AES-256] Decrypted Plaintext:  " + decrypted);
        System.out.println("[AES-256] Encryption match: " + (sampleNote.equals(decrypted) ? "SUCCESS" : "FAILED"));

        // 3. Test Database Connection
        System.out.println("\n[JDBC] Testing MySQL connection using DBConnection.getConnection()...");
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("[JDBC] SUCCESS! Connected to: " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
            } else {
                System.out.println("[JDBC] FAILED: Connection returned null or closed.");
            }
        } catch (Exception e) {
            System.out.println("[JDBC] Note: MySQL server may not be running yet on localhost:3306.");
            System.out.println("[JDBC] Error details: " + e.getMessage());
            System.out.println("[JDBC] (This is expected if MySQL is not started yet. Run sql/schema.sql and sql/seed.sql once MySQL is running)");
        }

        System.out.println("==================================================");
    }
}
