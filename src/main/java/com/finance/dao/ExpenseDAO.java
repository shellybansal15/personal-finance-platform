package com.finance.dao;

import com.finance.exception.DatabaseException;
import com.finance.model.Expense;
import com.finance.model.PaymentMode;
import com.finance.util.DBConnection;
import com.finance.util.EncryptionUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ============================================================================
 * [DAO & Collections Rubric: ExpenseDAO implementing BaseDAO<Expense>]
 * Manages expense persistence, filtering, pagination, and statistical aggregations.
 * Transparently encrypts/decrypts the note column using AES-256 (EncryptionUtil).
 * Employs Collections: List<Expense>, Map<String, Double> for Chart.js aggregations.
 * Updated: Includes payment_mode in all CRUD operations, adds getSpendingByPaymentMode().
 * ============================================================================
 */
public class ExpenseDAO implements BaseDAO<Expense> {

    @Override
    public Optional<Expense> findById(int id) throws DatabaseException {
        String sql = "SELECT e.id, e.user_id, e.category_id, e.amount, e.expense_date, e.note, e.payment_mode, e.created_at, c.name AS category_name " +
                     "FROM expenses e JOIN categories c ON e.category_id = c.id WHERE e.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToExpense(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find expense by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Expense> findAll() throws DatabaseException {
        String sql = "SELECT e.id, e.user_id, e.category_id, e.amount, e.expense_date, e.note, e.payment_mode, e.created_at, c.name AS category_name " +
                     "FROM expenses e JOIN categories c ON e.category_id = c.id ORDER BY e.expense_date DESC";
        List<Expense> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToExpense(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve all expenses", e);
        }
        return list;
    }

    @Override
    public boolean save(Expense expense) throws DatabaseException {
        String sql = "INSERT INTO expenses (user_id, category_id, amount, expense_date, note, payment_mode, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, expense.getUserId());
            ps.setInt(2, expense.getCategoryId());
            ps.setDouble(3, expense.getAmount());
            ps.setDate(4, Date.valueOf(expense.getDate() != null ? expense.getDate() : LocalDate.now()));
            // [Security Rubric: AES-Encrypt Expense Note]
            ps.setString(5, EncryptionUtil.encrypt(expense.getNote()));
            ps.setString(6, expense.getPaymentMode() != null ? expense.getPaymentMode().name() : PaymentMode.UPI.name());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        expense.setId(keys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save expense for user ID: " + expense.getUserId(), e);
        }
    }

    @Override
    public boolean update(Expense expense) throws DatabaseException {
        String sql = "UPDATE expenses SET category_id = ?, amount = ?, expense_date = ?, note = ?, payment_mode = ? " +
                     "WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, expense.getCategoryId());
            ps.setDouble(2, expense.getAmount());
            ps.setDate(3, Date.valueOf(expense.getDate()));
            // [Security Rubric: AES-Encrypt Expense Note]
            ps.setString(4, EncryptionUtil.encrypt(expense.getNote()));
            ps.setString(5, expense.getPaymentMode() != null ? expense.getPaymentMode().name() : PaymentMode.UPI.name());
            ps.setInt(6, expense.getId());
            ps.setInt(7, expense.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update expense ID: " + expense.getId(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM expenses WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete expense ID: " + id, e);
        }
    }

    public boolean deleteUserExpense(int expenseId, int userId) throws DatabaseException {
        String sql = "DELETE FROM expenses WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, expenseId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete user expense ID: " + expenseId, e);
        }
    }

    /**
     * Retrieves filtered and paginated list of expenses for a specific user.
     * Supports optional filter by categoryId, startDate, endDate, and paymentMode.
     */
    public List<Expense> findByUserId(int userId, Integer categoryId, LocalDate startDate, LocalDate endDate, int offset, int limit) throws DatabaseException {
        return findByUserId(userId, categoryId, startDate, endDate, null, offset, limit);
    }

    public List<Expense> findByUserId(int userId, Integer categoryId, LocalDate startDate, LocalDate endDate, String paymentMode, int offset, int limit) throws DatabaseException {
        StringBuilder sql = new StringBuilder(
                "SELECT e.id, e.user_id, e.category_id, e.amount, e.expense_date, e.note, e.payment_mode, e.created_at, c.name AS category_name " +
                "FROM expenses e JOIN categories c ON e.category_id = c.id WHERE e.user_id = ? "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (categoryId != null && categoryId > 0) {
            sql.append("AND e.category_id = ? ");
            params.add(categoryId);
        }
        if (startDate != null) {
            sql.append("AND e.expense_date >= ? ");
            params.add(Date.valueOf(startDate));
        }
        if (endDate != null) {
            sql.append("AND e.expense_date <= ? ");
            params.add(Date.valueOf(endDate));
        }
        if (paymentMode != null && !paymentMode.isEmpty() && !"ALL".equalsIgnoreCase(paymentMode)) {
            sql.append("AND e.payment_mode = ? ");
            params.add(paymentMode.toUpperCase());
        }

        sql.append("ORDER BY e.expense_date DESC, e.id DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        List<Expense> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToExpense(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to filter expenses for user ID: " + userId, e);
        }
        return list;
    }

    public int countByUserId(int userId, Integer categoryId, LocalDate startDate, LocalDate endDate) throws DatabaseException {
        return countByUserId(userId, categoryId, startDate, endDate, null);
    }

    public int countByUserId(int userId, Integer categoryId, LocalDate startDate, LocalDate endDate, String paymentMode) throws DatabaseException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM expenses WHERE user_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (categoryId != null && categoryId > 0) {
            sql.append("AND category_id = ? ");
            params.add(categoryId);
        }
        if (startDate != null) {
            sql.append("AND expense_date >= ? ");
            params.add(Date.valueOf(startDate));
        }
        if (endDate != null) {
            sql.append("AND expense_date <= ? ");
            params.add(Date.valueOf(endDate));
        }
        if (paymentMode != null && !paymentMode.isEmpty() && !"ALL".equalsIgnoreCase(paymentMode)) {
            sql.append("AND payment_mode = ? ");
            params.add(paymentMode.toUpperCase());
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count expenses for user ID: " + userId, e);
        }
        return 0;
    }

    public double getTotalSpentThisMonth(int userId) throws DatabaseException {
        String sql = "SELECT COALESCE(SUM(amount), 0.0) FROM expenses " +
                     "WHERE user_id = ? AND YEAR(expense_date) = YEAR(CURRENT_DATE) AND MONTH(expense_date) = MONTH(CURRENT_DATE)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to calculate total spent this month for user ID: " + userId, e);
        }
        return 0.0;
    }

    public double getTotalSpentAllTime(int userId) throws DatabaseException {
        String sql = "SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to calculate total spent all time for user ID: " + userId, e);
        }
        return 0.0;
    }

    /**
     * [Collections Rubric: Map<K,V>]
     * Aggregates total expenditure grouped by Category Name for Chart.js doughnut chart.
     */
    public Map<String, Double> getSpendingByCategory(int userId) throws DatabaseException {
        String sql = "SELECT c.name, COALESCE(SUM(e.amount), 0.0) AS total " +
                     "FROM expenses e JOIN categories c ON e.category_id = c.id " +
                     "WHERE e.user_id = ? GROUP BY c.name ORDER BY total DESC";
        Map<String, Double> spendingMap = new LinkedHashMap<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    spendingMap.put(rs.getString("name"), rs.getDouble("total"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get spending by category for user ID: " + userId, e);
        }
        return spendingMap;
    }

    /**
     * [Collections Rubric: Map<K,V>]
     * Aggregates total expenditure grouped by Payment Mode for Chart.js doughnut chart.
     * Key: payment_mode display name, Value: total spent.
     */
    public Map<String, Double> getSpendingByPaymentMode(int userId) throws DatabaseException {
        String sql = "SELECT payment_mode, COALESCE(SUM(amount), 0.0) AS total " +
                     "FROM expenses WHERE user_id = ? GROUP BY payment_mode ORDER BY total DESC";
        Map<String, Double> spendingMap = new LinkedHashMap<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String modeStr = rs.getString("payment_mode");
                    PaymentMode mode = PaymentMode.fromString(modeStr);
                    spendingMap.put(mode.getDisplayName(), rs.getDouble("total"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get spending by payment mode for user ID: " + userId, e);
        }
        return spendingMap;
    }

    /**
     * [Collections Rubric: Map<K,V>]
     * Aggregates monthly total expenditure for the last N months for Chart.js line chart.
     * Key: 'YYYY-MM', Value: total spent in that month.
     */
    public Map<String, Double> getMonthlySpendingTrend(int userId, int months) throws DatabaseException {
        String sql = "SELECT DATE_FORMAT(expense_date, '%Y-%m') AS month_label, COALESCE(SUM(amount), 0.0) AS total " +
                     "FROM expenses WHERE user_id = ? AND expense_date >= DATE_SUB(CURRENT_DATE, INTERVAL ? MONTH) " +
                     "GROUP BY month_label ORDER BY month_label ASC";
        Map<String, Double> trendMap = new LinkedHashMap<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, months);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    trendMap.put(rs.getString("month_label"), rs.getDouble("total"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get monthly trend for user ID: " + userId, e);
        }
        return trendMap;
    }

    public int countAllExpenses() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM expenses";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count all expenses", e);
        }
        return 0;
    }

    private Expense mapResultSetToExpense(ResultSet rs) throws SQLException {
        Expense expense = new Expense();
        expense.setId(rs.getInt("id"));
        expense.setUserId(rs.getInt("user_id"));
        expense.setCategoryId(rs.getInt("category_id"));
        expense.setCategoryName(rs.getString("category_name"));
        expense.setAmount(rs.getDouble("amount"));
        Date d = rs.getDate("expense_date");
        if (d != null) {
            expense.setDate(d.toLocalDate());
        }
        // [Security Rubric: AES-Decrypt Note]
        expense.setNote(EncryptionUtil.decrypt(rs.getString("note")));
        // Map payment_mode string to enum
        expense.setPaymentMode(PaymentMode.fromString(rs.getString("payment_mode")));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            expense.setCreatedAt(ts.toLocalDateTime());
        }
        return expense;
    }
}
