package com.finance.dao;

import com.finance.exception.DatabaseException;
import com.finance.model.SystemSetting;
import com.finance.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * [DAO & Admin Rubric: SystemSettingDAO implementing BaseDAO<SystemSetting>]
 * Reads and persists administrative platform parameters and feature toggles.
 * ============================================================================
 */
public class SystemSettingDAO implements BaseDAO<SystemSetting> {

    @Override
    public Optional<SystemSetting> findById(int id) throws DatabaseException {
        String sql = "SELECT id, setting_key, setting_value, updated_by, updated_at FROM system_settings WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSetting(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find setting by ID: " + id, e);
        }
        return Optional.empty();
    }

    public Optional<SystemSetting> findByKey(String key) throws DatabaseException {
        String sql = "SELECT id, setting_key, setting_value, updated_by, updated_at FROM system_settings WHERE setting_key = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSetting(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find setting by key: " + key, e);
        }
        return Optional.empty();
    }

    public String getSettingValue(String key, String defaultValue) {
        try {
            Optional<SystemSetting> setting = findByKey(key);
            return setting.map(SystemSetting::getSettingValue).orElse(defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public boolean setSettingValue(String key, String value, Integer updatedBy) throws DatabaseException {
        String sql = "INSERT INTO system_settings (setting_key, setting_value, updated_by, updated_at) " +
                     "VALUES (?, ?, ?, NOW()) " +
                     "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value), " +
                     "updated_by = VALUES(updated_by), updated_at = NOW()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key);
            ps.setString(2, value);
            if (updatedBy != null) {
                ps.setInt(3, updatedBy);
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save or update system setting: " + key, e);
        }
    }

    @Override
    public List<SystemSetting> findAll() throws DatabaseException {
        String sql = "SELECT id, setting_key, setting_value, updated_by, updated_at FROM system_settings ORDER BY setting_key ASC";
        List<SystemSetting> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToSetting(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve all system settings", e);
        }
        return list;
    }

    @Override
    public boolean save(SystemSetting setting) throws DatabaseException {
        return setSettingValue(setting.getSettingKey(), setting.getSettingValue(), setting.getUpdatedBy());
    }

    @Override
    public boolean update(SystemSetting setting) throws DatabaseException {
        return setSettingValue(setting.getSettingKey(), setting.getSettingValue(), setting.getUpdatedBy());
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM system_settings WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete system setting ID: " + id, e);
        }
    }

    private SystemSetting mapResultSetToSetting(ResultSet rs) throws SQLException {
        SystemSetting s = new SystemSetting();
        s.setId(rs.getInt("id"));
        s.setSettingKey(rs.getString("setting_key"));
        s.setSettingValue(rs.getString("setting_value"));
        int updatedBy = rs.getInt("updated_by");
        if (!rs.wasNull()) {
            s.setUpdatedBy(updatedBy);
        }
        Timestamp ts = rs.getTimestamp("updated_at");
        if (ts != null) {
            s.setUpdatedAt(ts.toLocalDateTime());
        }
        return s;
    }
}
