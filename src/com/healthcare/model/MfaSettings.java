package com.healthcare.model;

/**
 * Stores TOTP MFA configuration per user.
 */
public class MfaSettings {
    private int userId;
    private boolean enabled;
    private String totpSecret;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTotpSecret() {
        return totpSecret;
    }

    public void setTotpSecret(String totpSecret) {
        this.totpSecret = totpSecret;
    }
}


