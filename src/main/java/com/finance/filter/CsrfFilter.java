package com.finance.filter;

import com.finance.util.CsrfUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * ============================================================================
 * [Security Rubric: CSRF Verification Filter]
 * Ensures all state-altering HTTP POST requests contain a valid CSRF token.
 * Rejects forged or missing CSRF tokens to safeguard users against cross-site attacks.
 * ============================================================================
 */
@WebFilter(urlPatterns = {"/*"})
public class CsrfFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Ensure session and CSRF token exist for GET requests so forms have tokens ready
        HttpSession session = httpRequest.getSession(true);
        CsrfUtil.getToken(session);

        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();

        // Only validate CSRF on POST requests that are NOT static assets
        if ("POST".equalsIgnoreCase(method)) {
            // Static assets or non-form calls do not need verification
            if (!uri.endsWith(".css") && !uri.endsWith(".js") && !uri.endsWith(".png")) {
                if (!CsrfUtil.isValid(httpRequest)) {
                    session.setAttribute("flashError", "Security Alert: Invalid or expired security token. Please retry your request.");
                    String referer = httpRequest.getHeader("Referer");
                    if (referer != null && !referer.isEmpty()) {
                        httpResponse.sendRedirect(referer);
                    } else {
                        httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
                    }
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
