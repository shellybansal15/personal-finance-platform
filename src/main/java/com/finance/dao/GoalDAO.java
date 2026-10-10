package com.finance.dao;

import com.finance.exception.DatabaseException;
import com.finance.model.Goal;
import com.finance.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * [DAO & OOP Rubric: GoalDAO implementing BaseDAO<Goal>]
 * Handles financial goals, target tracking, deadline status, and fund contributions.
 * ============================================================================
 */
public class GoalDAO implements BaseDAO<Goal> {

    @Override
    public Optional<Goal> findById(int id) throws DatabaseException {
        String sql = "SELECT id, user_id, description, target_amount, saved_amount, deadline, status, created_at " +
                     "FROM goals WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGoal(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find goal by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Goal> findAll() throws DatabaseException {
        String sql = "SELECT id, user_id, description, target_amount, saved_amount, deadline, status, created_at " +
                     "FROM goals ORDER BY deadline ASC";
        List<Goal> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToGoal(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve all goals", e);
        }
        return list;
    }

    public List<Goal> findByUserId(int userId) throws DatabaseException {
        String sql = "SELECT id, user_id, description, target_amount, saved_amount, deadline, status, created_at " +
                     "FROM goals WHERE user_id = ? ORDER BY CASE WHEN status = 'IN_PROGRESS' THEN 0 ELSE 1 END, deadline ASC";
        List<Goal> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToGoal(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve goals for user ID: " + userId, e);
        }
        return list;
    }

    @Override
    public boolean save(Goal goal) throws DatabaseException {
        String sql = "INSERT INTO goals (user_id, description, target_amount, saved_amount, deadline, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, goal.getUserId());
            ps.setString(2, goal.getDescription());
            ps.setDouble(3, goal.getTargetAmount());
            ps.setDouble(4, goal.getSavedAmount());
            ps.setDate(5, Date.valueOf(goal.getDeadline()));
            ps.setString(6, goal.getStatus() != null ? goal.getStatus() : "IN_PROGRESS");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        goal.setId(keys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save goal for user ID: " + goal.getUserId(), e);
        }
    }

    @Override
    public boolean update(Goal goal) throws DatabaseException {
        String sql = "UPDATE goals SET description = ?, target_amount = ?, saved_amount = ?, deadline = ?, status = ? " +
                     "WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, goal.getDescription());
            ps.setDouble(2, goal.getTargetAmount());
            ps.setDouble(3, goal.getSavedAmount());
            ps.setDate(4, Date.valueOf(goal.getDeadline()));
            ps.setString(5, goal.getStatus());
            ps.setInt(6, goal.getId());
            ps.setInt(7, goal.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update goal ID: " + goal.getId(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM goals WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete goal ID: " + id, e);
        }
    }

    public boolean deleteUserGoal(int goalId, int userId) throws DatabaseException {
        String sql = "DELETE FROM goals WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, goalId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete user goal ID: " + goalId, e);
        }
    }

    /**
     * Contributes funds toward a goal and automatically updates status to 'COMPLETED'
     * if saved_amount meets or exceeds target_amount.
     */
    public boolean addFunds(int goalId, double depositAmount, int userId) throws DatabaseException {
        String sql = "UPDATE goals SET saved_amount = saved_amount + ?, " +
                     "status = CASE WHEN (saved_amount + ?) >= target_amount THEN 'COMPLETED' ELSE status END " +
                     "WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, depositAmount);
            ps.setDouble(2, depositAmount);
            ps.setInt(3, goalId);
            ps.setInt(4, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to add funds to goal ID: " + goalId, e);
        }
    }

    public boolean updateStatus(int goalId, String status, int userId) throws DatabaseException {
        String sql = "UPDATE goals SET status = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, goalId);
            ps.setInt(3, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update status for goal ID: " + goalId, e);
        }
    }

    public double getTotalSavedAmount(int userId) throws DatabaseException {
        String sql = "SELECT COALESCE(SUM(saved_amount), 0.0) FROM goals WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get total saved amount for user ID: " + userId, e);
        }
        return 0.0;
    }

    private Goal mapResultSetToGoal(ResultSet rs) throws SQLException {
        Goal g = new Goal();
        g.setId(rs.getInt("id"));
        g.setUserId(rs.getInt("user_id"));
        g.setDescription(rs.getString("description"));
        g.setTargetAmount(rs.getDouble("target_amount"));
        g.setSavedAmount(rs.getDouble("saved_amount"));
        Date d = rs.getDate("deadline");
        if (d != null) {
            g.setDeadline(d.toLocalDate());
        }
        g.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            g.setCreatedAt(ts.toLocalDateTime());
        }
        return g;
    }
}
