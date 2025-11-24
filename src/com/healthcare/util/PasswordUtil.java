package com.healthcare.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Very small helper to hash and verify passwords.
 * For real systems use a stronger algorithm (bcrypt/argon2).
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : hash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString().toUpperCase(); // normalize to match MySQL SHA2 output
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public static boolean verifyPassword(String plainPassword, String hashFromDb) {
        if (plainPassword == null || hashFromDb == null) {
            return false;
        }
        return hashPassword(plainPassword).equalsIgnoreCase(hashFromDb);
    }
}

