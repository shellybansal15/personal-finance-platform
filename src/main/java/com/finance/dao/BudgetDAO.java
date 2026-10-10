package com.finance.dao;

import com.finance.exception.DatabaseException;
import com.finance.model.Budget;
import com.finance.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * [DAO & OOP Rubric: BudgetDAO implementing BaseDAO<Budget>]
 * Handles budget storage and joins with expenses to compute dynamic spentAmount
 * for the Trackable interface progress calculations.
 * ============================================================================
 */
public class BudgetDAO implements BaseDAO<Budget> {

    @Override
    public Optional<Budget> findById(int id) throws DatabaseException {
        String sql = "SELECT b.id, b.user_id, b.category_id, b.amount, b.start_date, b.end_date, b.created_at, " +
                     "c.name AS category_name, " +
                     "(SELECT COALESCE(SUM(e.amount), 0.0) FROM expenses e " +
                     " WHERE e.user_id = b.user_id AND e.category_id = b.category_id " +
                     "   AND e.expense_date BETWEEN b.start_date AND b.end_date) AS spent_amount " +
                     "FROM budgets b JOIN categories c ON b.category_id = c.id WHERE b.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBudget(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find budget by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Budget> findAll() throws DatabaseException {
        String sql = "SELECT b.id, b.user_id, b.category_id, b.amount, b.start_date, b.end_date, b.created_at, " +
                     "c.name AS category_name, " +
                     "(SELECT COALESCE(SUM(e.amount), 0.0) FROM expenses e " +
                     " WHERE e.user_id = b.user_id AND e.category_id = b.category_id " +
                     "   AND e.expense_date BETWEEN b.start_date AND b.end_date) AS spent_amount " +
                     "FROM budgets b JOIN categories c ON b.category_id = c.id ORDER BY b.id DESC";
        List<Budget> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToBudget(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve all budgets", e);
        }
        return list;
    }

    public List<Budget> findByUserId(int userId) throws DatabaseException {
        String sql = "SELECT b.id, b.user_id, b.category_id, b.amount, b.start_date, b.end_date, b.created_at, " +
                     "c.name AS category_name, " +
                     "(SELECT COALESCE(SUM(e.amount), 0.0) FROM expenses e " +
                     " WHERE e.user_id = b.user_id AND e.category_id = b.category_id " +
                     "   AND e.expense_date BETWEEN b.start_date AND b.end_date) AS spent_amount " +
                     "FROM budgets b JOIN categories c ON b.category_id = c.id " +
                     "WHERE b.user_id = ? ORDER BY b.end_date ASC, b.id DESC";
        List<Budget> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToBudget(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve budgets for user ID: " + userId, e);
        }
        return list;
    }

    @Override
    public boolean save(Budget budget) throws DatabaseException {
        String sql = "INSERT INTO budgets (user_id, category_id, amount, start_date, end_date, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, budget.getUserId());
            ps.setInt(2, budget.getCategoryId());
            ps.setDouble(3, budget.getAmount());
            ps.setDate(4, Date.valueOf(budget.getStartDate()));
            ps.setDate(5, Date.valueOf(budget.getEndDate()));

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        budget.setId(keys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save budget for user ID: " + budget.getUserId(), e);
        }
    }

    @Override
    public boolean update(Budget budget) throws DatabaseException {
        String sql = "UPDATE budgets SET category_id = ?, amount = ?, start_date = ?, end_date = ? " +
                     "WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, budget.getCategoryId());
            ps.setDouble(2, budget.getAmount());
            ps.setDate(3, Date.valueOf(budget.getStartDate()));
            ps.setDate(4, Date.valueOf(budget.getEndDate()));
            ps.setInt(5, budget.getId());
            ps.setInt(6, budget.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update budget ID: " + budget.getId(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM budgets WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete budget ID: " + id, e);
        }
    }

    public boolean deleteUserBudget(int budgetId, int userId) throws DatabaseException {
        String sql = "DELETE FROM budgets WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, budgetId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete user budget ID: " + budgetId, e);
        }
    }

    public double getTotalBudgetThisMonth(int userId) throws DatabaseException {
        String sql = "SELECT COALESCE(SUM(amount), 0.0) FROM budgets " +
                     "WHERE user_id = ? AND CURRENT_DATE BETWEEN start_date AND end_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get total active budget for user ID: " + userId, e);
        }
        return 0.0;
    }

    private Budget mapResultSetToBudget(ResultSet rs) throws SQLException {
        Budget b = new Budget();
        b.setId(rs.getInt("id"));
        b.setUserId(rs.getInt("user_id"));
        b.setCategoryId(rs.getInt("category_id"));
        b.setCategoryName(rs.getString("category_name"));
        b.setAmount(rs.getDouble("amount"));
        b.setSpentAmount(rs.getDouble("spent_amount"));
        Date sDate = rs.getDate("start_date");
        if (sDate != null) {
            b.setStartDate(sDate.toLocalDate());
        }
        Date eDate = rs.getDate("end_date");
        if (eDate != null) {
            b.setEndDate(eDate.toLocalDate());
        }
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            b.setCreatedAt(ts.toLocalDateTime());
        }
        return b;
    }
}
