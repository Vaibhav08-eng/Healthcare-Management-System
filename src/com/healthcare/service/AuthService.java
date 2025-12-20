package com.healthcare.service;

import com.healthcare.dao.AuditLogDao;
import com.healthcare.dao.AuditLogDaoImpl;
import com.healthcare.dao.SessionDao;
import com.healthcare.dao.SessionDaoImpl;
import com.healthcare.dao.UserDao;
import com.healthcare.dao.UserDaoImpl;
import com.healthcare.model.AuditLogEntry;
import com.healthcare.model.Session;
import com.healthcare.model.User;
import com.healthcare.util.PasswordUtil;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

/**
 * Handles authentication logic for the login screen.
 */
public class AuthService {

    private final UserDao userDao;
        private final SessionDao sessionDao;
        private final AuditLogDao auditLogDao;

        // 30 minutes idle timeout, 8 hours max lifetime by default
        private static final long SESSION_IDLE_MINUTES = 30;
        private static final long SESSION_MAX_HOURS = 8;
        private static final SecureRandom RANDOM = new SecureRandom();

    public AuthService() {
            this(new UserDaoImpl(), new SessionDaoImpl(), new AuditLogDaoImpl());
    }

        public AuthService(UserDao userDao) {
            this(userDao, new SessionDaoImpl(), new AuditLogDaoImpl());
        }

        public AuthService(UserDao userDao, SessionDao sessionDao, AuditLogDao auditLogDao) {
        this.userDao = userDao;
            this.sessionDao = sessionDao;
            this.auditLogDao = auditLogDao;
    }

        /**
         * Attempts to authenticate a user and create a session.
         * For compatibility, this also supports legacy SHA-256 seeded users
         * and silently migrates them to BCrypt on successful login.
         */
    public User login(String email, String password) {
        Optional<User> userOpt = userDao.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
                boolean active = "ACTIVE".equalsIgnoreCase(user.getStatus());
                boolean authenticated = false;

                if (active) {
                    String hash = user.getPasswordHash();
                    if (hash != null && hash.startsWith("$2")) {
                        // BCrypt
                        authenticated = PasswordUtil.verifyPassword(password, hash);
                    } else if (hash != null && hash.length() == 64) {
                        // Legacy SHA-256 seed, migrate on successful match
                        String candidate = PasswordUtil.sha256Hex(password);
                        if (candidate.equalsIgnoreCase(hash)) {
                            authenticated = true;
                            String newHash = PasswordUtil.hashPassword(password);
                            user.setPasswordHash(newHash);
                            userDao.update(user);
                        }
                    }
                }

                if (active && authenticated) {
                    Session session = createSession(user.getUserId(), null, null);
                    sessionDao.insert(session);
                    writeAuditLog(user.getUserId(), session.getSessionId(), "LOGIN_SUCCESS", "USER", String.valueOf(user.getUserId()),
                            "User logged in", null);
                return user;
            }
                // Failed login: log audit event
                writeAuditLog(null, null, "LOGIN_FAILURE", "USER", null,
                        "Invalid credentials or inactive user for email=" + email, null);
        }
        return null;
    }

        private Session createSession(int userId, String ipAddress, String userAgent) {
            Session session = new Session();
            session.setSessionId(generateSessionId());
            session.setUserId(userId);
            LocalDateTime now = LocalDateTime.now();
            session.setCreatedAt(now);
            session.setLastAccessAt(now);
            session.setExpiresAt(now.plusHours(SESSION_MAX_HOURS));
            session.setRevoked(false);
            session.setIpAddress(ipAddress);
            session.setUserAgent(userAgent);
            return session;
        }

        private String generateSessionId() {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        }

        private void writeAuditLog(Integer userId,
                                   String sessionId,
                                   String actionType,
                                   String resourceType,
                                   String resourceId,
                                   String details,
                                   String ipAddress) {
            AuditLogEntry entry = new AuditLogEntry();
            entry.setUserId(userId);
            entry.setSessionId(sessionId);
            entry.setActionType(actionType);
            entry.setResourceType(resourceType);
            entry.setResourceId(resourceId);
            entry.setDetails(details);
            entry.setIpAddress(ipAddress);
            entry.setCreatedAt(LocalDateTime.now());
            auditLogDao.insert(entry);
        }
}

