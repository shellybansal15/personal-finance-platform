package com.finance.servlet;

import com.finance.exception.AppException;
import com.finance.model.SystemSetting;
import com.finance.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * ============================================================================
 * [Servlets & Admin Rubric: AdminSettingsServlet]
 * Controller for viewing and altering global system settings and feature toggles.
 * ============================================================================
 */
@WebServlet(urlPatterns = {"/admin/settings"})
public class AdminSettingsServlet extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() {
        this.adminService = new AdminService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Ensure application-wide defaults exist before rendering
        adminService.ensureDefaultSettings(null);
        List<SystemSetting> settings = adminService.getSystemSettings();
        req.setAttribute("settings", settings);

        req.getRequestDispatcher("/WEB-INF/jsp/admin/settings.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);
        int adminUserId = (Integer) session.getAttribute("userId");
        String action = req.getParameter("action");
        String clientIp = getClientIp(req);

        try {
            if ("addSetting".equalsIgnoreCase(action)) {
                String key = req.getParameter("settingKey");
                String value = req.getParameter("settingValue");
                adminService.updateSystemSetting(key, value, adminUserId, clientIp);
                session.setAttribute("flashSuccess", "Success: New system parameter '" + key + "' saved!");
            } else {
                String key = req.getParameter("settingKey");
                String value = req.getParameter("settingValue");
                adminService.updateSystemSetting(key, value, adminUserId, clientIp);
                session.setAttribute("flashSuccess", "Success: System parameter '" + key + "' updated to '" + value + "'!");
            }
        } catch (AppException e) {
            session.setAttribute("flashError", e.getMessage());
        } catch (Exception e) {
            session.setAttribute("flashError", "Failed to update setting: " + e.getMessage());
        }

        // Post-Redirect-Get pattern
        resp.sendRedirect(req.getContextPath() + "/admin/settings");
    }

    private String getClientIp(HttpServletRequest req) {
        String remoteAddr = req.getHeader("X-Forwarded-For");
        if (remoteAddr == null || remoteAddr.isEmpty()) {
            remoteAddr = req.getRemoteAddr();
        }
        return remoteAddr != null ? remoteAddr : "127.0.0.1";
    }
}
