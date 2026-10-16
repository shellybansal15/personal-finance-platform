package com.finance.dao;

import com.finance.exception.DatabaseException;
import com.finance.model.SecurityLog;
import com.finance.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * [Security & Multithreading Rubric: SecurityLogDAO implementing BaseDAO<SecurityLog>]
 * Manages persistence and retrieval of security events and audit trails.
 * Invoked by the thread-safe synchronized AuditLogger.
 * ============================================================================
 */
public class SecurityLogDAO implements BaseDAO<SecurityLog> {

    @Override
    public Optional<SecurityLog> findById(int id) throws DatabaseException {
        String sql = "SELECT s.id, s.user_id, s.action, s.ip_address, s.created_at, u.email AS user_email " +
                     "FROM security_logs s LEFT JOIN users u ON s.user_id = u.id WHERE s.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSecurityLog(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find security log by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<SecurityLog> findAll() throws DatabaseException {
        return getRecentLogs(100);
    }

    @Override
    public boolean save(SecurityLog log) throws DatabaseException {
        return saveLog(log.getUserId(), log.getAction(), log.getIpAddress());
    }

    public boolean saveLog(Integer userId, String action, String ipAddress) throws DatabaseException {
        String sql = "INSERT INTO security_logs (user_id, action, ip_address, created_at) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (userId != null) {
                ps.setInt(1, userId);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, action);
            ps.setString(3, ipAddress != null ? ipAddress : "127.0.0.1");

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to write security log: " + action, e);
        }
    }

    public List<SecurityLog> getRecentLogs(int limit) throws DatabaseException {
        String sql = "SELECT s.id, s.user_id, s.action, s.ip_address, s.created_at, u.email AS user_email " +
                     "FROM security_logs s LEFT JOIN users u ON s.user_id = u.id " +
                     "ORDER BY s.id DESC LIMIT ?";
        List<SecurityLog> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSecurityLog(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve recent security logs", e);
        }
        return list;
    }

    public int countLogs() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM security_logs";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count security logs", e);
        }
        return 0;
    }

    @Override
    public boolean update(SecurityLog entity) throws DatabaseException {
        // Audit logs are strictly immutable and cannot be updated
        throw new UnsupportedOperationException("Security audit logs are append-only and cannot be modified.");
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM security_logs WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete security log ID: " + id, e);
        }
    }

    public List<SecurityLog> getFilteredLogs(String userFilter, String actionFilter, int offset, int limit) throws DatabaseException {
        StringBuilder sql = new StringBuilder(
                "SELECT s.id, s.user_id, s.action, s.ip_address, s.created_at, u.email AS user_email " +
                "FROM security_logs s LEFT JOIN users u ON s.user_id = u.id WHERE 1=1 "
        );
        List<Object> params = new ArrayList<>();

        if (userFilter != null && !userFilter.trim().isEmpty()) {
            sql.append("AND (u.email LIKE ? OR u.name LIKE ?) ");
            String term = "%" + userFilter.trim() + "%";
            params.add(term);
            params.add(term);
        }
        if (actionFilter != null && !actionFilter.trim().isEmpty()) {
            sql.append("AND s.action LIKE ? ");
            params.add("%" + actionFilter.trim() + "%");
        }
        sql.append("ORDER BY s.id DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        List<SecurityLog> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSecurityLog(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve filtered security logs", e);
        }
        return list;
    }

    public int countFilteredLogs(String userFilter, String actionFilter) throws DatabaseException {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM security_logs s LEFT JOIN users u ON s.user_id = u.id WHERE 1=1 "
        );
        List<Object> params = new ArrayList<>();

        if (userFilter != null && !userFilter.trim().isEmpty()) {
            sql.append("AND (u.email LIKE ? OR u.name LIKE ?) ");
            String term = "%" + userFilter.trim() + "%";
            params.add(term);
            params.add(term);
        }
        if (actionFilter != null && !actionFilter.trim().isEmpty()) {
            sql.append("AND s.action LIKE ? ");
            params.add("%" + actionFilter.trim() + "%");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count filtered security logs", e);
        }
        return 0;
    }

    private SecurityLog mapResultSetToSecurityLog(ResultSet rs) throws SQLException {
        SecurityLog log = new SecurityLog();
        log.setId(rs.getInt("id"));
        int uid = rs.getInt("user_id");
        if (!rs.wasNull()) {
            log.setUserId(uid);
        }
        log.setUserEmail(rs.getString("user_email"));
        log.setAction(rs.getString("action"));
        log.setIpAddress(rs.getString("ip_address"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            log.setCreatedAt(ts.toLocalDateTime());
        }
        return log;
    }
}
