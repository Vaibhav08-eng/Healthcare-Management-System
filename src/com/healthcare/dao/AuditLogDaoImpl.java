package com.healthcare.dao;

import com.healthcare.model.AuditLogEntry;
import com.healthcare.util.DataSourceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * JDBC implementation of AuditLogDao.
 */
public class AuditLogDaoImpl implements AuditLogDao {

    @Override
    public void insert(AuditLogEntry entry) {
        String sql = "INSERT INTO audit_log (user_id, session_id, action_type, resource_type, resource_id, details, ip_address, created_at) "
                + "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (entry.getUserId() != null) {
                ps.setInt(1, entry.getUserId());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setString(2, entry.getSessionId());
            ps.setString(3, entry.getActionType());
            ps.setString(4, entry.getResourceType());
            ps.setString(5, entry.getResourceId());
            ps.setString(6, entry.getDetails());
            ps.setString(7, entry.getIpAddress());
            ps.setTimestamp(8, toTimestamp(entry.getCreatedAt()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert audit log entry", e);
        }
    }

    private Timestamp toTimestamp(java.time.LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }
}


