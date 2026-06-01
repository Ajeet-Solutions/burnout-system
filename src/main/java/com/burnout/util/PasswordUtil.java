package com.burnout.util;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * Utility class for secure password hashing and verification using SHA-256 algorithm.
 */
public class PasswordUtil {
    
	/**
     * Converts a plain-text password into a secure cryptographic SHA-256 hash string.
     * * @param password The raw password input from the user.
     * @return The encrypted Base64 encoded hash string
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            // Updated to professional error handling message
            throw new RuntimeException("Error occurred during password encryption process", e);
        }
    }

    /**
     * Verifies if the provided plain-text password matches the stored encrypted hash.
     * * @param plainPassword The raw password provided during authentication.
     * @param hashedPassword The existing secure hash from the database.
     * @return true if the credentials match, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return hashPassword(plainPassword).equals(hashedPassword);
    }
}