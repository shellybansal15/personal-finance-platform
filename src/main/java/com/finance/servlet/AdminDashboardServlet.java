package com.finance.servlet;

import com.finance.model.SecurityLog;
import com.finance.model.User;
import com.finance.service.AdminService;
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
 * [Servlets & Admin Rubric: AdminDashboardServlet]
 * Controller for the administrative management overview screen, showing
 * system health, platform metrics, and recent security events.
 * ============================================================================
 */
@WebServlet(urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() {
        this.adminService = new AdminService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Map<String, Object> platformStats = adminService.getPlatformStats();
        List<SecurityLog> recentLogs = adminService.getRecentSecurityLogs(6);
        List<User> recentUsers = adminService.searchUsers("", "ALL", "ALL", 1, 5);

        req.setAttribute("stats", platformStats);
        req.setAttribute("recentLogs", recentLogs);
        req.setAttribute("recentUsers", recentUsers);

        req.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp").forward(req, resp);
    }
}
