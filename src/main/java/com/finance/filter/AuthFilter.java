package com.finance.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * ============================================================================
 * [Servlets & Web Integration Rubric: AuthFilter]
 * Intercepts requests destined for protected areas (/user/* and /admin/*).
 * Verifies active session presence. Unauthenticated visitors are redirected
 * to /login with an informational notification.
 * ============================================================================
 */
@WebFilter(urlPatterns = {"/user/*", "/admin/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("currentUser") != null);

        if (isLoggedIn) {
            // User is authenticated, proceed along filter chain
            chain.doFilter(request, response);
        } else {
            // Unauthenticated: store flash warning and redirect to login
            HttpSession newSession = httpRequest.getSession(true);
            newSession.setAttribute("flashWarning", "Please log in to access this page.");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
