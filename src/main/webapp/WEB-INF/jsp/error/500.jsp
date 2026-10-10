<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="500 Internal Server Error" />
<%@ include file="/WEB-INF/jsp/fragments/header.jsp" %>

<div class="d-flex align-items-center justify-content-center min-vh-100 bg-page p-3">
    <div class="text-center" style="max-width: 500px;">
        <div class="stat-card-icon stat-icon-red mx-auto mb-3" style="width: 72px; height: 72px; font-size: 2rem;">
            <i class="bi bi-exclamation-triangle"></i>
        </div>
        <h1 class="display-4 fw-bold">500</h1>
        <h4 class="fw-semibold mb-2">Unexpected Error</h4>
        <p class="text-muted small mb-4">Something went wrong while processing your request. The incident has been recorded in our security logs.</p>
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
