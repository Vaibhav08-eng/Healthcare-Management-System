package com.healthcare.dao;

import com.healthcare.model.Session;

import java.util.Optional;

/**
 * DAO for authentication sessions.
 */
public interface SessionDao {

    void insert(Session session);

    Optional<Session> findById(String sessionId);

    void touch(String sessionId);

    void revoke(String sessionId);

    void revokeAllForUser(int userId);
}


