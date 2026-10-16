package com.finance.util;

import com.finance.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Ensures the live database has the tables/columns the app expects.
 * Cloud MySQL instances (Aiven / Render) often retain an older schema after
 * code updates — without these ALTER/CREATE steps every SELECT that references
 * {@code occupation} or {@code payment_mode} fails with
 * {@code Failed to find user by email}.
 */
public final class SchemaMigrator {

    private SchemaMigrator() {
    }

    /**
     * Idempotent: safe to call on every demo login and app start.
     */
    public static void ensureSchema() throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            createTablesIfMissing(stmt);
            ensureColumn(conn, stmt, "users", "occupation",
                    "ALTER TABLE users ADD COLUMN occupation ENUM("
                            + "'Student','Salaried Employee','Self-Employed',"
                            + "'Business Owner','Homemaker','Retired','Other'"
                            + ") NULL DEFAULT NULL AFTER status");
            ensureColumn(conn, stmt, "expenses", "payment_mode",
                    "ALTER TABLE expenses ADD COLUMN payment_mode ENUM("
                            + "'UPI','NET_BANKING','CREDIT_CARD','DEBIT_CARD',"
                            + "'CASH','WALLET','CHEQUE','OTHER'"
                            + ") NOT NULL DEFAULT 'UPI' AFTER note");

            System.out.println("[SchemaMigrator] Schema check complete.");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to ensure database schema: " + e.getMessage(), e);
        }
    }

    private static void createTablesIfMissing(Statement stmt) throws SQLException {
        stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(150) NOT NULL UNIQUE,
                    password_hash VARCHAR(255) NOT NULL,
                    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
                    status ENUM('ACTIVE', 'BLOCKED') NOT NULL DEFAULT 'ACTIVE',
                    occupation ENUM(
                        'Student', 'Salaried Employee', 'Self-Employed',
                        'Business Owner', 'Homemaker', 'Retired', 'Other'
                    ) NULL DEFAULT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    INDEX idx_user_email (email),
                    INDEX idx_user_role_status (role, status)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        stmt.execute("""
                CREATE TABLE IF NOT EXISTS categories (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL UNIQUE,
                    type ENUM('EXPENSE', 'INCOME') NOT NULL DEFAULT 'EXPENSE',
                    INDEX idx_category_type (type)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        stmt.execute("""
                CREATE TABLE IF NOT EXISTS expenses (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    category_id INT NOT NULL,
                    amount DECIMAL(10, 2) NOT NULL,
                    expense_date DATE NOT NULL,
                    note TEXT,
                    payment_mode ENUM(
                        'UPI', 'NET_BANKING', 'CREDIT_CARD', 'DEBIT_CARD',
                        'CASH', 'WALLET', 'CHEQUE', 'OTHER'
                    ) NOT NULL DEFAULT 'UPI',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT chk_expense_amount CHECK (amount > 0),
                    INDEX idx_expenses_user_date (user_id, expense_date),
                    INDEX idx_expenses_category (category_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        stmt.execute("""
                CREATE TABLE IF NOT EXISTS budgets (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    category_id INT NOT NULL,
                    amount DECIMAL(10, 2) NOT NULL,
                    start_date DATE NOT NULL,
                    end_date DATE NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT chk_budget_amount CHECK (amount > 0),
                    INDEX idx_budgets_user (user_id),
                    INDEX idx_budgets_dates (start_date, end_date)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        stmt.execute("""
                CREATE TABLE IF NOT EXISTS goals (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    description VARCHAR(255) NOT NULL,
                    target_amount DECIMAL(10, 2) NOT NULL,
                    saved_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
                    deadline DATE NOT NULL,
                    status ENUM('IN_PROGRESS', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'IN_PROGRESS',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT chk_goal_target_amount CHECK (target_amount > 0),
                    CONSTRAINT chk_goal_saved_amount CHECK (saved_amount >= 0),
                    INDEX idx_goals_user_status (user_id, status)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        stmt.execute("""
                CREATE TABLE IF NOT EXISTS security_logs (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NULL,
                    action VARCHAR(255) NOT NULL,
                    ip_address VARCHAR(45) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    INDEX idx_logs_created_at (created_at),
                    INDEX idx_logs_user (user_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        stmt.execute("""
                CREATE TABLE IF NOT EXISTS system_settings (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    setting_key VARCHAR(100) NOT NULL UNIQUE,
                    setting_value VARCHAR(255) NOT NULL,
                    updated_by INT NULL,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_settings_key (setting_key)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);
    }

    private static void ensureColumn(Connection conn, Statement stmt,
                                     String table, String column, String alterSql)
            throws SQLException {
        if (columnExists(conn, table, column)) {
            return;
        }
        stmt.execute(alterSql);
        System.out.println("[SchemaMigrator] Added column " + table + "." + column);
    }

    private static boolean columnExists(Connection conn, String table, String column)
            throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        String catalog = conn.getCatalog();
        Set<String> matches = new HashSet<>();
        try (ResultSet rs = meta.getColumns(catalog, null, table, null)) {
            while (rs.next()) {
                matches.add(rs.getString("COLUMN_NAME").toLowerCase(Locale.ROOT));
            }
        }
        // Some drivers are case-sensitive on table name — retry uppercase/lowercase.
        if (matches.isEmpty()) {
            try (ResultSet rs = meta.getColumns(catalog, null, table.toUpperCase(Locale.ROOT), null)) {
                while (rs.next()) {
                    matches.add(rs.getString("COLUMN_NAME").toLowerCase(Locale.ROOT));
                }
            }
        }
        if (matches.isEmpty()) {
            try (ResultSet rs = meta.getColumns(catalog, null, table.toLowerCase(Locale.ROOT), null)) {
                while (rs.next()) {
                    matches.add(rs.getString("COLUMN_NAME").toLowerCase(Locale.ROOT));
                }
            }
        }
        return matches.contains(column.toLowerCase(Locale.ROOT));
    }
}
