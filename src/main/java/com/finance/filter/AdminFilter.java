package com.finance.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * ============================================================================
 * [Servlets & Security Rubric: AdminFilter]
 * Intercepts requests to /admin/* routes, ensuring that only authenticated
 * users possessing the 'ADMIN' role can access administrative tools.
 * Non-admins are redirected back to the user dashboard with an access-denied alert.
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
        String role = (session != null) ? (String) session.getAttribute("userRole") : null;

        if ("ADMIN".equalsIgnoreCase(role)) {
            // Authorized admin user
            chain.doFilter(request, response);
        } else {
            // Unauthorized non-admin user
            if (session != null) {
                session.setAttribute("flashError", "Access Denied: You do not have permission to access the administration panel.");
            }
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/user/dashboard");
        }
    }

    @Override
    public void destroy() {
    }
}
