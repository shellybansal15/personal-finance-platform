package com.finance.servlet;

import com.finance.exception.AppException;
import com.finance.model.User;
import com.finance.service.UserService;
import com.finance.util.CsrfUtil;
import com.finance.util.DatabaseSeeder;
import com.finance.util.SessionTracker;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * ============================================================================
 * [Servlets Rubric: AuthServlet - @WebServlet, doGet/doPost, Post-Redirect-Get]
 * Handles user login, registration, and logout flows.
 * Uses Post-Redirect-Get (PRG) with session flash messages to prevent duplicate form
 * submissions and provide clean confirmation notifications for every user action.
 * Updated: Login validates selected role (User/Admin) against the database record.
 * ============================================================================
 */
@WebServlet(urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();
        HttpSession session = req.getSession(false);

        // If user is already authenticated and visits /login or /register, redirect to dashboard
        if (session != null && session.getAttribute("currentUser") != null) {
            String role = (String) session.getAttribute("userRole");
            if (!"/logout".equals(path)) {
                if ("ADMIN".equalsIgnoreCase(role)) {
                    resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/user/dashboard");
                }
                return;
            }
        }

        switch (path) {
            case "/login":
                req.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(req, resp);
                break;
            case "/register":
                req.getRequestDispatcher("/WEB-INF/jsp/auth/register.jsp").forward(req, resp);
                break;
            case "/logout":
                handleLogout(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/login");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String path = req.getServletPath();
        String clientIp = getClientIp(req);

        switch (path) {
            case "/login":
                handleLogin(req, resp, clientIp);
                break;
            case "/register":
                handleRegister(req, resp, clientIp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/login");
                break;
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp, String clientIp) throws IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        // New: "Login as" role selector — "USER" or "ADMIN"
        String loginAsRole = req.getParameter("loginAs");

        try {
            if (loginAsRole == null || loginAsRole.trim().isEmpty()) {
                throw new AppException("Please select whether you are logging in as User or Administrator.");
            }
            // Safety net: if the DB was wiped or never seeded, recreate demo personas + MVP data
            // before authentication so Fill → Sign In never hits "Failed to find user by email".
            if (DatabaseSeeder.isDemoEmail(email)) {
                DatabaseSeeder.ensureDemoWorkspace();
            }
            User user = userService.loginWithRole(email, password, loginAsRole, clientIp);

            // Invalidate old session and generate new session ID (Session Fixation Prevention)
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = req.getSession(true);
            newSession.setMaxInactiveInterval(15 * 60);
            newSession.setAttribute("currentUser", user);
            newSession.setAttribute("userId", user.getId());
            newSession.setAttribute("userName", user.getName());
            newSession.setAttribute("userEmail", user.getEmail());
            newSession.setAttribute("userRole", user.getRole());
            CsrfUtil.getToken(newSession);
            SessionTracker.register(newSession, user, clientIp);

            newSession.setAttribute("flashSuccess", "Welcome back, " + user.getName() + "! You have logged in successfully.");

            if (user.isAdmin()) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/user/dashboard");
            }

        } catch (AppException e) {
            // Store error message and redirect back to login page
            HttpSession session = req.getSession(true);
            session.setAttribute("flashError", e.getMessage());
            session.setAttribute("loginEmail", email);
            session.setAttribute("loginAs", loginAsRole); // Preserve selector state
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp, String clientIp) throws IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String occupation = req.getParameter("occupation");  // New: occupation field

        HttpSession session = req.getSession(true);

        if (password == null || !password.equals(confirmPassword)) {
            session.setAttribute("flashError", "Passwords do not match. Please verify and try again.");
            session.setAttribute("regName", name);
            session.setAttribute("regEmail", email);
            session.setAttribute("regOccupation", occupation);
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }

        try {
            // Public registration always creates USER; ignore any posted role parameter
            userService.register(name, email, password, occupation, clientIp);

            // [Rubric: Every action shows confirmation message]
            session.setAttribute("flashSuccess", "Registration successful! You may now sign in with your email and password.");
            resp.sendRedirect(req.getContextPath() + "/login");

        } catch (AppException e) {
            session.setAttribute("flashError", e.getMessage());
            session.setAttribute("regName", name);
            session.setAttribute("regEmail", email);
            session.setAttribute("regOccupation", occupation);
            resp.sendRedirect(req.getContextPath() + "/register");
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        HttpSession newSession = req.getSession(true);
        newSession.setAttribute("flashSuccess", "You have been logged out safely.");
        // Return to landing; flash is shown on next login visit if they navigate there
        resp.sendRedirect(req.getContextPath() + "/");
    }

    private String getClientIp(HttpServletRequest req) {
        String remoteAddr = req.getHeader("X-Forwarded-For");
        if (remoteAddr == null || remoteAddr.isEmpty()) {
            remoteAddr = req.getRemoteAddr();
        }
        return remoteAddr != null ? remoteAddr : "127.0.0.1";
    }
}
