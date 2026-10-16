package com.finance.filter;

import com.finance.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * ============================================================================
 * [Servlets & Security Rubric: AdminFilter]
 * Strict gate for /admin/* — requires a live session with currentUser, userId,
 * and ADMIN role (cross-checked against the User entity when present).
 * Unauthenticated callers go to /login; non-admins receive HTTP 403.
 * ============================================================================
 */
@WebFilter(urlPatterns = {"/admin/*"})
public class AdminFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);

        // Strict session presence checks
        if (session == null
                || session.getAttribute("currentUser") == null
                || session.getAttribute("userId") == null
                || session.getAttribute("userRole") == null) {
            HttpSession flashSession = httpRequest.getSession(true);
            flashSession.setAttribute("flashWarning", "Please log in as an administrator to continue.");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("userRole");
        Object currentUserObj = session.getAttribute("currentUser");

        boolean roleIsAdmin = "ADMIN".equalsIgnoreCase(role);
        boolean entityIsAdmin = true;
        if (currentUserObj instanceof User) {
            entityIsAdmin = ((User) currentUserObj).isAdmin();
        }

        if (roleIsAdmin && entityIsAdmin) {
            chain.doFilter(request, response);
            return;
        }

        // Authenticated but not an administrator
        session.setAttribute("flashError",
                "Access Denied: Administrator privileges are required for this area.");
        httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        httpRequest.getRequestDispatcher("/WEB-INF/jsp/error/403.jsp").forward(httpRequest, httpResponse);
    }

    @Override
    public void destroy() {
    }
}
