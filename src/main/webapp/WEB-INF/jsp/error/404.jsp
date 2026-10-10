<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="404 Not Found" />
<%@ include file="/WEB-INF/jsp/fragments/header.jsp" %>

<div class="d-flex align-items-center justify-content-center min-vh-100 bg-page p-3">
    <div class="text-center" style="max-width: 500px;">
        <div class="stat-card-icon stat-icon-blue mx-auto mb-3" style="width: 72px; height: 72px; font-size: 2rem;">
            <i class="bi bi-question-circle"></i>
        </div>
        <h1 class="display-4 fw-bold">404</h1>
        <h4 class="fw-semibold mb-2">Page Not Found</h4>
        <p class="text-muted small mb-4">The URL you requested does not exist or may have been moved.</p>
        <div class="d-flex justify-content-center gap-2">
            <a href="${pageContext.request.contextPath}/user/dashboard" class="btn btn-primary btn-sm px-4">
                <i class="bi bi-house me-1"></i> Dashboard
            </a>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-secondary btn-sm px-4">
                Sign In
            </a>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/fragments/footer.jsp" %>
