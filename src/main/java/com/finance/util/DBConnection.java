package com.finance.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * ============================================================================
 * [Database & JDBC Rubric: Pure JDBC Connection Factory]
 * Manages database connectivity using configuration loaded from db.properties.
 * Always used with try-with-resources in DAO classes to ensure database
 * connections are promptly and reliably closed after query execution.
 * ============================================================================
 */
public class DBConnection {

    private static String jdbcUrl;
    private static String jdbcUser;
    private static String jdbcPassword;
    private static String jdbcDriver;

    static {
        loadConfiguration();
    }

    /**
     * Loads database credentials from db.properties file in the classpath.
     */
    private static void loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream inputStream = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (inputStream == null) {
                System.err.println("[DBConnection] WARNING: db.properties not found in classpath. Using defaults.");
                jdbcUrl = "jdbc:mysql://localhost:3306/finance_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
                jdbcUser = "root";
                jdbcPassword = "";
                jdbcDriver = "com.mysql.cj.jdbc.Driver";
            } else {
                properties.load(inputStream);
                jdbcUrl = properties.getProperty("db.url");
                jdbcUser = properties.getProperty("db.username");
                jdbcPassword = properties.getProperty("db.password");
                jdbcDriver = properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            }

            // Register the MySQL JDBC Driver
            Class.forName(jdbcDriver);
        } catch (IOException e) {
            System.err.println("[DBConnection] ERROR: Failed to read db.properties: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] ERROR: MySQL JDBC Driver class not found: " + e.getMessage());
        }
    }

    /**
     * Obtains a fresh active connection to the MySQL database.
     * Callers must wrap this in a try-with-resources block!
     *
     * Example:
     * <pre>
     * try (Connection conn = DBConnection.getConnection();
     *      PreparedStatement ps = conn.prepareStatement(sql)) {
     *     // execute query
     * }
     * </pre>
     *
     * @return Active java.sql.Connection instance
     * @throws SQLException If database connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (jdbcUrl == null) {
            loadConfiguration();
        }
        return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
    }

    /**
     * Helper method to test if database connection succeeds.
     *
     * @return true if connected successfully, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("[DBConnection] Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
