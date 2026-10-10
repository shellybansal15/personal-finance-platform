package com.finance.model;

import java.time.LocalDateTime;

/**
 * ============================================================================
 * [Security Rubric: Audit Trail Security Log Entity]
 * Stores immutable audit logs of sensitive operations (logins, lockouts,
 * profile modifications, permission changes).
 * ============================================================================
 */
public class SecurityLog {

    private int id;
    private Integer userId;     // Can be null for failed login attempts
    private String userEmail;   // Joined field for easy display in admin tables
    private String action;
    private String ipAddress;
    private LocalDateTime createdAt;

    public SecurityLog() {
    }

    public SecurityLog(int id, Integer userId, String action, String ipAddress, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.ipAddress = ipAddress;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
