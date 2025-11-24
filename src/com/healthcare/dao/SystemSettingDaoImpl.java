package com.healthcare.dao;

import com.healthcare.model.SystemSetting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO for key/value settings.
 */
public class SystemSettingDaoImpl implements SystemSettingDao {

    private static final String BASE_SELECT = "SELECT setting_key, setting_value, updated_at FROM system_settings";

    @Override
    public List<SystemSetting> findAll() {
        List<SystemSetting> settings = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(BASE_SELECT);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                settings.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load system settings", e);
        }
        return settings;
    }

    @Override
    public Optional<SystemSetting> findByKey(String key) {
        String sql = BASE_SELECT + " WHERE setting_key=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load setting", e);
        }
        return Optional.empty();
    }

    @Override
    public void saveOrUpdate(SystemSetting setting) {
        String sql = "INSERT INTO system_settings (setting_key, setting_value) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE setting_value=VALUES(setting_value)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, setting.getKey());
            ps.setString(2, setting.getValue());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save setting", e);
        }
    }

    private SystemSetting mapRow(ResultSet rs) throws SQLException {
        SystemSetting setting = new SystemSetting();
        setting.setKey(rs.getString("setting_key"));
        setting.setValue(rs.getString("setting_value"));
        java.sql.Timestamp ts = rs.getTimestamp("updated_at");
        if (ts != null) {
            setting.setUpdatedAt(ts.toLocalDateTime());
        }
        return setting;
    }
}

