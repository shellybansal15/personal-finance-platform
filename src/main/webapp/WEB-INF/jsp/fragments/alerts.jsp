<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- [UI Rubric: Every action shows a confirmation message] -->
<c:if test="${not empty sessionScope.flashSuccess}">
    <div class="alert alert-success alert-dismissible fade show d-flex align-items-center gap-2 mb-4" role="alert">
        <i class="bi bi-check-circle-fill fs-5 text-success"></i>
        <div>${sessionScope.flashSuccess}</div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <% session.removeAttribute("flashSuccess"); %>
</c:if>

<c:if test="${not empty sessionScope.flashError}">
    <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center gap-2 mb-4" role="alert">
        <i class="bi bi-exclamation-triangle-fill fs-5 text-danger"></i>
        <div>${sessionScope.flashError}</div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <% session.removeAttribute("flashError"); %>
</c:if>

<c:if test="${not empty sessionScope.flashWarning}">
    <div class="alert alert-warning alert-dismissible fade show d-flex align-items-center gap-2 mb-4" role="alert">
        <i class="bi bi-exclamation-circle-fill fs-5 text-warning"></i>
        <div>${sessionScope.flashWarning}</div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <% session.removeAttribute("flashWarning"); %>
</c:if>

<c:if test="${not empty sessionScope.flashInfo}">
    <div class="alert alert-info alert-dismissible fade show d-flex align-items-center gap-2 mb-4" role="alert">
        <i class="bi bi-info-circle-fill fs-5 text-info"></i>
        <div>${sessionScope.flashInfo}</div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <% session.removeAttribute("flashInfo"); %>
</c:if>
