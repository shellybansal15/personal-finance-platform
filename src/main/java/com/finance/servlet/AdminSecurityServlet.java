package com.finance.servlet;

import com.finance.model.SecurityLog;
import com.finance.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * ============================================================================
 * [Servlets & Security Rubric: AdminSecurityServlet]
 * Controller for inspecting immutable system audit logs, security event trails,
 * failed logins, and administrative actions.
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
        req.setAttribute("logs", logs);

        req.getRequestDispatcher("/WEB-INF/jsp/admin/security.jsp").forward(req, resp);
    }
}
