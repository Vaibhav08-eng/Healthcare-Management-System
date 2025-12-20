package com.healthcare.model;

import java.time.LocalDateTime;

/**
 * Lightweight session projection exposed to UI instead of full User.
 */
public class AuthSession {
    private final String token;
    private final int userId;
    private final String role;
    private final String displayName;
    private final LocalDateTime expiresAt;
    private final boolean mfaRequired;

    public AuthSession(String token, int userId, String role, String displayName,
                       LocalDateTime expiresAt, boolean mfaRequired) {
        this.token = token;
        this.userId = userId;
        this.role = role;
        this.displayName = displayName;
        this.expiresAt = expiresAt;
        this.mfaRequired = mfaRequired;
    }

    public String getToken() {
        return token;
    }

    public int getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isMfaRequired() {
        return mfaRequired;
    }
}


