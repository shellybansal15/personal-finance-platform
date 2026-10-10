package com.finance.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * ============================================================================
 * [Security Rubric: Cross-Site Request Forgery (CSRF) Protection]
 * Generates unique, cryptographically random CSRF tokens tied to the user's
 * session and verifies them on state-modifying POST requests.
 * ============================================================================
 */
public class CsrfUtil {

    public static final String CSRF_SESSION_ATTR = "csrfToken";
    public static final String CSRF_PARAM_NAME = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Retrieves existing token from session or generates a new one.
     *
     * @param session Current HTTP session
     * @return 32-character base64 CSRF token
     */
    public static String getToken(HttpSession session) {
        if (session == null) return "";
        String token = (String) session.getAttribute(CSRF_SESSION_ATTR);
        if (token == null || token.isEmpty()) {
            byte[] bytes = new byte[24];
            RANDOM.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(CSRF_SESSION_ATTR, token);
        }
        return token;
    }

    /**
     * Validates whether the CSRF token in the incoming request matches the session token.
     *
     * @param request Incoming HTTP servlet request
     * @return true if valid match, false otherwise
     */
    public static boolean isValid(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;

        String sessionToken = (String) session.getAttribute(CSRF_SESSION_ATTR);
        String requestToken = request.getParameter(CSRF_PARAM_NAME);

        if (sessionToken == null || requestToken == null) {
            return false;
        }

        // Constant-time comparison to prevent timing attacks
        return MessageDigestConstantTimeEquals(sessionToken, requestToken);
    }

    private static boolean MessageDigestConstantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
