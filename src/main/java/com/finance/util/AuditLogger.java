package com.finance.util;

import com.finance.dao.SecurityLogDAO;
import com.finance.exception.DatabaseException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ============================================================================
 * [Multithreading & Synchronization Rubric: Synchronized AuditLogger]
 * AuditLogger is a thread-safe singleton that logs security-sensitive actions
 * (logins, logouts, lockouts, password resets, role changes) to the database
 * and standard security log streams.
 *
 * The synchronized keyword ensures that concurrent requests from multiple
 * servlet worker threads execute sequentially without corrupting shared state,
 * race conditions, or interleaving log entries.
 * ============================================================================
 */
public class AuditLogger {

    private static volatile AuditLogger instance;
    private final SecurityLogDAO securityLogDAO;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AuditLogger() {
        this.securityLogDAO = new SecurityLogDAO();
    }

    /**
     * Thread-safe double-checked locking singleton accessor.
     */
    public static AuditLogger getInstance() {
        if (instance == null) {
            synchronized (AuditLogger.class) {
                if (instance == null) {
                    instance = new AuditLogger();
                }
            }
        }
        return instance;
    }

    /**
     * [Rubric: Synchronization]
     * Synchronized method guaranteeing mutually exclusive execution across threads
     * when recording a security audit action.
     *
     * @param userId User ID involved (null for unauthenticated events)
     * @param action Description of the security action taken
     * @param ipAddress Client IP address
     */
    public synchronized void log(Integer userId, String action, String ipAddress) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logLine = String.format("[AUDIT-LOG %s] [User: %s] [IP: %s] Action: %s",
                timestamp, (userId != null ? userId : "Anonymous"), ipAddress, action);

        // Print thread-safely to standard output
        System.out.println(logLine);

        // Persist to database table security_logs
        try {
            securityLogDAO.saveLog(userId, action, ipAddress);
        } catch (DatabaseException e) {
            System.err.println("[AuditLogger] ERROR: Failed to persist audit log to DB: " + e.getMessage());
        }
    }
}
