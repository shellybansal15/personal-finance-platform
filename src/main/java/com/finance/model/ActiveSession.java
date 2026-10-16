package com.finance.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Snapshot of an authenticated HttpSession for the admin Security module.
 */
public class ActiveSession {

    private String sessionId;
    private int userId;
    private String userName;
    private String userEmail;
    private String userRole;
    private String ipAddress;
    private LocalDateTime loginTime;
    private long lastAccessedTimeMs;
    private int maxInactiveIntervalSec;

    public ActiveSession() {
    }

    public ActiveSession(String sessionId, int userId, String userName, String userEmail,
                         String userRole, String ipAddress, LocalDateTime loginTime,
                         long lastAccessedTimeMs, int maxInactiveIntervalSec) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.ipAddress = ipAddress;
        this.loginTime = loginTime;
        this.lastAccessedTimeMs = lastAccessedTimeMs;
        this.maxInactiveIntervalSec = maxInactiveIntervalSec;
    }

    /** Short opaque token for display (never expose full session id). */
    public String getMaskedSessionId() {
        if (sessionId == null || sessionId.length() < 8) {
            return "****";
        }
        return sessionId.substring(0, 4) + "…" + sessionId.substring(sessionId.length() - 4);
    }

    public LocalDateTime getLastAccessedAt() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(lastAccessedTimeMs), ZoneId.systemDefault());
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(userRole);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public long getLastAccessedTimeMs() {
        return lastAccessedTimeMs;
    }

    public void setLastAccessedTimeMs(long lastAccessedTimeMs) {
        this.lastAccessedTimeMs = lastAccessedTimeMs;
    }

    public int getMaxInactiveIntervalSec() {
        return maxInactiveIntervalSec;
    }

    public void setMaxInactiveIntervalSec(int maxInactiveIntervalSec) {
        this.maxInactiveIntervalSec = maxInactiveIntervalSec;
    }
}
