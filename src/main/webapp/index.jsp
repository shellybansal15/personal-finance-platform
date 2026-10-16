<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    /*
     * Welcome router:
     * - Authenticated ADMIN  → /admin/dashboard
     * - Authenticated USER   → /user/dashboard
     * - Guest                → immersive scrollytelling landing page
     */
    Object currentUser = session.getAttribute("currentUser");
    if (currentUser != null) {
        String role = (String) session.getAttribute("userRole");
        if ("ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/user/dashboard");
        }
    } else {
        request.getRequestDispatcher("/landing.jsp").forward(request, response);
    }
%>
