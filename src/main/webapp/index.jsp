<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // If user is already logged in, redirect to user dashboard or admin dashboard
    Object currentUser = session.getAttribute("currentUser");
    if (currentUser != null) {
        String role = (String) session.getAttribute("userRole");
        if ("ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/user/dashboard");
        }
    } else {
        // Otherwise, send to login page
        response.sendRedirect(request.getContextPath() + "/login");
    }
%>
