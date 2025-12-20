package com.healthcare.dao;

import com.healthcare.model.Session;
import com.healthcare.util.DataSourceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * JDBC implementation of SessionDao.
 */
public class SessionDaoImpl implements SessionDao {

    @Override
    public void insert(Session session) {
        String sql = "INSERT INTO sessions (session_id, user_id, created_at, last_access_at, expires_at, revoked, ip_address, user_agent) "
                + "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, session.getSessionId());
            ps.setInt(2, session.getUserId());
            ps.setTimestamp(3, toTimestamp(session.getCreatedAt()));
            ps.setTimestamp(4, toTimestamp(session.getLastAccessAt()));
            ps.setTimestamp(5, toTimestamp(session.getExpiresAt()));
            ps.setBoolean(6, session.isRevoked());
            ps.setString(7, session.getIpAddress());
            ps.setString(8, session.getUserAgent());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert session", e);
        }
    }

    @Override
    public Optional<Session> findById(String sessionId) {
        String sql = "SELECT session_id, user_id, created_at, last_access_at, expires_at, revoked, ip_address, user_agent "
                + "FROM sessions WHERE session_id=?";
        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load session", e);
        }
        return Optional.empty();
    }

    @Override
    public void touch(String sessionId) {
        String sql = "UPDATE sessions SET last_access_at=CURRENT_TIMESTAMP WHERE session_id=? AND revoked=FALSE";
        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to touch session", e);
        }
    }

    @Override
    public void revoke(String sessionId) {
        String sql = "UPDATE sessions SET revoked=TRUE WHERE session_id=?";
        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to revoke session", e);
        }
    }

    @Override
    public void revokeAllForUser(int userId) {
        String sql = "UPDATE sessions SET revoked=TRUE WHERE user_id=?";
        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to revoke sessions for user", e);
        }
    }

    private Session mapRow(ResultSet rs) throws SQLException {
        Session s = new Session();
        s.setSessionId(rs.getString("session_id"));
        s.setUserId(rs.getInt("user_id"));
        s.setCreatedAt(toLocalDateTime(rs.getTimestamp("created_at")));
        s.setLastAccessAt(toLocalDateTime(rs.getTimestamp("last_access_at")));
        s.setExpiresAt(toLocalDateTime(rs.getTimestamp("expires_at")));
        s.setRevoked(rs.getBoolean("revoked"));
        s.setIpAddress(rs.getString("ip_address"));
        s.setUserAgent(rs.getString("user_agent"));
        return s;
    }

    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}


