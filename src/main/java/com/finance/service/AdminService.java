package com.finance.service;

import com.finance.dao.ExpenseDAO;
import com.finance.dao.SecurityLogDAO;
import com.finance.dao.SystemSettingDAO;
import com.finance.dao.UserDAO;
import com.finance.exception.ResourceNotFoundException;
import com.finance.exception.ValidationException;
import com.finance.model.SecurityLog;
import com.finance.model.SystemSetting;
import com.finance.model.User;
import com.finance.util.AuditLogger;
import com.finance.util.PasswordUtil;
import com.finance.util.ValidationUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ============================================================================
 * [Service & Security Rubric: AdminService]
 * Central administrative operations: user account oversight, role controls,
 * audit trail inspection, and system feature configurations.
 * Contains critical self-preservation safeguards (admins cannot demote or block themselves).
 * Updated: addUser(), deleteUser(), getPlatformStats() with active/blocked counts.
 * ============================================================================
 */
public class AdminService {

    private final UserDAO userDAO;
    private final SecurityLogDAO securityLogDAO;
    private final SystemSettingDAO systemSettingDAO;
    private final ExpenseDAO expenseDAO;
    private final AuditLogger auditLogger;

    public AdminService() {
        this.userDAO = new UserDAO();
        this.securityLogDAO = new SecurityLogDAO();
        this.systemSettingDAO = new SystemSettingDAO();
        this.expenseDAO = new ExpenseDAO();
        this.auditLogger = AuditLogger.getInstance();
    }

    public List<User> searchUsers(String query, String role, String status, int page, int pageSize) {
        int offset = Math.max(0, (page - 1) * pageSize);
        return userDAO.searchUsers(query, role, status, offset, pageSize);
    }

    public int countUsers(String query, String role, String status) {
        return userDAO.countFilteredUsers(query, role, status);
    }

    /**
     * Creates a new user with the specified role (admin-only operation).
     */
    public User addUser(String name, String email, String role, String password, int adminUserId, String ipAddress) {
        String cleanName = ValidationUtil.requireNonEmpty(name, "Full Name");
        String cleanEmail = ValidationUtil.validateEmail(email);
        String cleanRole = (role != null && "ADMIN".equalsIgnoreCase(role)) ? "ADMIN" : "USER";

        if (userDAO.findByEmail(cleanEmail).isPresent()) {
            throw new ValidationException("An account with email " + cleanEmail + " already exists.");
        }

        String pw = (password != null && !password.trim().isEmpty()) ? password.trim() : "FinanceFlow@123";
        ValidationUtil.validatePassword(pw);

        User user = new User();
        user.setName(cleanName);
        user.setEmail(cleanEmail);
        user.setPasswordHash(PasswordUtil.hashPassword(pw));
        user.setRole(cleanRole);
        user.setStatus("ACTIVE");

        userDAO.save(user);
        auditLogger.log(adminUserId, String.format("Admin created new %s account '%s' (ID #%d)", cleanRole, cleanEmail, user.getId()), ipAddress);
        return user;
    }

    /**
     * Deletes a user (admin cannot delete self).
     */
    public void deleteUser(int targetUserId, int adminUserId, String ipAddress) {
        if (targetUserId == adminUserId) {
            throw new ValidationException("Action Forbidden: Administrators cannot delete their own account.");
        }

        Optional<User> userOpt = userDAO.findById(targetUserId);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User not found.");
        }

        User user = userOpt.get();
        userDAO.delete(targetUserId);
        auditLogger.log(adminUserId, String.format("Admin deleted user '%s' (ID #%d)", user.getEmail(), targetUserId), ipAddress);
    }

    /**
     * [Security Rubric: Admin cannot block or deactivate themselves]
     */
    public void toggleUserStatus(int targetUserId, int adminUserId, String ipAddress) {
        if (targetUserId == adminUserId) {
            throw new ValidationException("Action Forbidden: Administrators cannot deactivate or block their own account.");
        }

        Optional<User> userOpt = userDAO.findById(targetUserId);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User not found.");
        }

        User user = userOpt.get();
        String newStatus = "ACTIVE".equalsIgnoreCase(user.getStatus()) ? "BLOCKED" : "ACTIVE";
        userDAO.updateStatus(targetUserId, newStatus);

        auditLogger.log(adminUserId, String.format("Admin changed user '%s' (ID #%d) status to %s",
                user.getEmail(), targetUserId, newStatus), ipAddress);
    }

    /**
     * [Security Rubric: Admin cannot demote themselves]
     */
    public void changeUserRole(int targetUserId, int adminUserId, String newRole, String ipAddress) {
        String role = ValidationUtil.requireNonEmpty(newRole, "User role").toUpperCase();
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            throw new ValidationException("Invalid role specified. Must be USER or ADMIN.");
        }

        if (targetUserId == adminUserId && "USER".equals(role)) {
            throw new ValidationException("Action Forbidden: Administrators cannot revoke their own admin privileges.");
        }

        Optional<User> userOpt = userDAO.findById(targetUserId);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User not found.");
        }

        User user = userOpt.get();
        userDAO.updateRole(targetUserId, role);

        auditLogger.log(adminUserId, String.format("Admin changed user '%s' (ID #%d) role to %s",
                user.getEmail(), targetUserId, role), ipAddress);
    }

    public List<SecurityLog> getRecentSecurityLogs(int limit) {
        return securityLogDAO.getRecentLogs(limit);
    }

    public List<SecurityLog> getFilteredSecurityLogs(String userFilter, String actionFilter, int offset, int limit) {
        return securityLogDAO.getFilteredLogs(userFilter, actionFilter, offset, limit);
    }

    public int countFilteredSecurityLogs(String userFilter, String actionFilter) {
        return securityLogDAO.countFilteredLogs(userFilter, actionFilter);
    }

    public List<SystemSetting> getSystemSettings() {
        return systemSettingDAO.findAll();
    }

    public String getSetting(String key, String defaultValue) {
        return systemSettingDAO.getSettingValue(key, defaultValue);
    }

    public void updateSystemSetting(String key, String value, int adminUserId, String ipAddress) {
        String cleanKey = ValidationUtil.requireNonEmpty(key, "Setting key");
        // For boolean toggles, value may be empty (unchecked = false)
        String cleanVal = (value != null) ? value.trim() : "false";

        systemSettingDAO.setSettingValue(cleanKey, cleanVal, adminUserId);
        auditLogger.log(adminUserId, String.format("Admin updated system setting '%s' to '%s'", cleanKey, cleanVal), ipAddress);
    }

    public Map<String, Object> getPlatformStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userDAO.countUsers());
        stats.put("activeUsers", userDAO.countUsersByStatus("ACTIVE"));
        stats.put("blockedUsers", userDAO.countUsersByStatus("BLOCKED"));
        stats.put("totalExpensesCount", expenseDAO.countAllExpenses());
        stats.put("securityLogsCount", securityLogDAO.countLogs());
        return stats;
    }
}
