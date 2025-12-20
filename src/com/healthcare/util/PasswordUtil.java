package com.healthcare.util;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Strong password hashing and verification utilities using BCrypt.
 */
public final class PasswordUtil {

    private static final int BCRYPT_COST = 12;
    private static final String STRONG_PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{12,}$";

    private PasswordUtil() {
    }

    /**
     * Hash a plaintext password server-side using BCrypt.
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        if (!plainPassword.matches(STRONG_PASSWORD_REGEX)) {
            throw new IllegalArgumentException("Password does not meet strength requirements");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_COST));
    }

    /**
     * Verify a plaintext password against a BCrypt hash.
     */
    public static boolean verifyPassword(String plainPassword, String hashFromDb) {
        if (plainPassword == null || hashFromDb == null) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashFromDb);
    }

    /**
     * Computes a legacy SHA-256 hex hash. Used only to support transparent
     * migration of old demo users that were seeded with SHA2(...) in SQL.
     */
    public static String sha256Hex(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}

