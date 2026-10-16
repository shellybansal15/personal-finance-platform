package com.finance.servlet;

import com.finance.model.ActiveSession;
import com.finance.model.SecurityLog;
import com.finance.service.AdminService;
import com.finance.util.LoginThrottler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================
 * [Servlets & Security Rubric: AdminSecurityServlet]
 * Security admin module: system audit logs, live authenticated sessions,
 * and recent / in-memory login attempt visibility.
 * ============================================================================
 */
@WebServlet(urlPatterns = {"/admin/security"})
public class AdminSecurityServlet extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() {
        this.adminService = new AdminService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<SecurityLog> logs = adminService.getRecentSecurityLogs(100);
        List<SecurityLog> loginAttempts = adminService.getRecentLoginAttempts(50);
        List<ActiveSession> activeSessions = adminService.getActiveSessions();
        Map<String, LoginThrottler.AttemptRecord> liveFailures = adminService.getLiveFailedLoginAttempts();

        req.setAttribute("logs", logs);
        req.setAttribute("loginAttempts", loginAttempts);
        req.setAttribute("activeSessions", activeSessions);
        req.setAttribute("liveFailures", liveFailures);
        req.setAttribute("maxLoginAttempts", LoginThrottler.MAX_FAILED_ATTEMPTS);
        req.setAttribute("lockoutMinutes", LoginThrottler.LOCKOUT_DURATION_MS / 60000L);

        req.getRequestDispatcher("/WEB-INF/jsp/admin/security.jsp").forward(req, resp);
    }
}
