<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<header class="app-navbar">
    <div class="d-flex align-items-center gap-3">
        <!-- Mobile Sidebar Toggle -->
        <button class="btn btn-outline-secondary d-md-none p-1 px-2" id="sidebarToggleBtn" aria-label="Toggle Sidebar Navigation">
            <i class="bi bi-list fs-5"></i>
        </button>

        <!-- Current Breadcrumb / Section Header -->
        <h5 class="mb-0 fw-semibold text-truncate">${pageHeader != null ? pageHeader : 'Overview'}</h5>
    </div>

    <!-- Right Controls: Dark Mode & Profile -->
    <div class="d-flex align-items-center gap-3">
        <!-- Dark Mode Toggle Button -->
        <button type="button" class="btn btn-sm btn-outline-secondary rounded-circle" id="themeToggleBtn" 
                style="width: 38px; height: 38px;" title="Switch Theme">
            <i class="bi bi-moon-stars-fill text-success"></i>
        </button>

        <!-- User Profile Dropdown / Info -->
        <div class="dropdown">
            <button class="btn btn-sm btn-light border dropdown-toggle d-flex align-items-center gap-2 rounded-pill px-3 py-1" 
                    type="button" data-bs-toggle="dropdown" aria-expanded="false">
                <span class="user-avatar" style="width: 26px; height: 26px; font-size: 0.75rem;">
                    ${sessionScope.userName != null ? sessionScope.userName.substring(0, 1).toUpperCase() : 'U'}
                </span>
                <span class="d-none d-sm-inline small fw-medium">${sessionScope.userName}</span>
            </button>
            <ul class="dropdown-menu dropdown-menu-end shadow-sm">
                <li class="dropdown-header small text-muted">
                    Signed in as <strong>${sessionScope.userEmail}</strong>
                </li>
                <li><hr class="dropdown-divider"></li>
                <c:if test="${sessionScope.userRole == 'ADMIN'}">
                    <li>
                        <a class="dropdown-item small" href="${pageContext.request.contextPath}/admin/dashboard">
                            <i class="bi bi-shield-check me-2 text-primary"></i>Admin Panel
                        </a>
                    </li>
                </c:if>
                <li>
                    <a class="dropdown-item small" href="${pageContext.request.contextPath}/user/dashboard">
                        <i class="bi bi-person me-2"></i>User Dashboard
                    </a>
                </li>
                <li><hr class="dropdown-divider"></li>
                <li>
                    <a class="dropdown-item small text-danger" href="${pageContext.request.contextPath}/logout">
                        <i class="bi bi-box-arrow-right me-2"></i>Log Out
                    </a>
                </li>
            </ul>
        </div>
    </div>
</header>
