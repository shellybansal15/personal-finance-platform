package com.finance.servlet;

import com.finance.exception.AppException;
import com.finance.model.User;
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
 * [Servlets & Admin Rubric: AdminUserServlet]
 * Controller for user management: searchable directory, pagination,
 * account status toggling (ACTIVE/BLOCKED), role reassignment (USER/ADMIN),
 * add user, and delete user.
 * Safeguards prevent administrators from locking or demoting their own account.
 * ============================================================================
 */
@WebServlet(urlPatterns = {"/admin/users"})
public class AdminUserServlet extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() {
        this.adminService = new AdminService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String query = req.getParameter("search");
        String role = req.getParameter("role");
        String status = req.getParameter("status");

        int page = 1;
        String pageStr = req.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Math.max(1, Integer.parseInt(pageStr));
            } catch (NumberFormatException ignored) {}
        }
        int pageSize = 10;

        int totalUsers = adminService.countUsers(query, role, status);
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
        if (totalPages == 0) totalPages = 1;

        List<User> users = adminService.searchUsers(query, role, status, page, pageSize);

        req.setAttribute("users", users);
        req.setAttribute("search", query);
        req.setAttribute("selectedRole", role != null ? role : "ALL");
        req.setAttribute("selectedStatus", status != null ? status : "ALL");
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalUsers", totalUsers);

        req.getRequestDispatcher("/WEB-INF/jsp/admin/users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);
        int adminUserId = (Integer) session.getAttribute("userId");
        String action = req.getParameter("action");
        String clientIp = getClientIp(req);

        try {
            if ("addUser".equalsIgnoreCase(action)) {
                // Create new user via admin
                String name = req.getParameter("name");
                String email = req.getParameter("email");
                String role = req.getParameter("role");
                String password = req.getParameter("password");

                adminService.addUser(name, email, role, password, adminUserId, clientIp);
                session.setAttribute("flashSuccess", "User account created successfully for " + email + "!");

            } else if ("deleteUser".equalsIgnoreCase(action)) {
                int targetUserId = Integer.parseInt(req.getParameter("userId"));
                adminService.deleteUser(targetUserId, adminUserId, clientIp);
                session.setAttribute("flashSuccess", "User account deleted successfully!");

            } else {
                int targetUserId = Integer.parseInt(req.getParameter("userId"));

                if ("toggleStatus".equalsIgnoreCase(action)) {
                    adminService.toggleUserStatus(targetUserId, adminUserId, clientIp);
                    session.setAttribute("flashSuccess", "User account status updated successfully!");

                } else if ("changeRole".equalsIgnoreCase(action)) {
                    String newRole = req.getParameter("newRole");
                    adminService.changeUserRole(targetUserId, adminUserId, newRole, clientIp);
                    session.setAttribute("flashSuccess", "User role updated to " + newRole + " successfully!");
                }
            }
        } catch (AppException e) {
            session.setAttribute("flashError", e.getMessage());
        } catch (Exception e) {
            session.setAttribute("flashError", "An unexpected error occurred: " + e.getMessage());
        }

        // Post-Redirect-Get pattern
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }

    private String getClientIp(HttpServletRequest req) {
        String remoteAddr = req.getHeader("X-Forwarded-For");
        if (remoteAddr == null || remoteAddr.isEmpty()) {
            remoteAddr = req.getRemoteAddr();
        }
        return remoteAddr != null ? remoteAddr : "127.0.0.1";
    }
}
