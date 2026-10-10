<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Mobile Sidebar Backdrop Overlay -->
<div class="sidebar-overlay" id="sidebarOverlay"></div>

<!-- App Sidebar Navigation -->
<aside class="app-sidebar" id="appSidebar">
    <!-- Brand Logo -->
    <div class="sidebar-brand">
        <div class="brand-icon">
            <i class="bi bi-wallet2"></i>
        </div>
        <span>FinanceFlow</span>
    </div>

    <!-- Navigation Menu Items -->
    <div class="sidebar-nav">
        <div class="nav-section-title">Personal Finances</div>

        <a href="${pageContext.request.contextPath}/user/dashboard" 
           class="sidebar-link ${activeMenu == 'dashboard' ? 'active' : ''}">
            <i class="bi bi-grid-1x2-fill"></i>
            <span>Dashboard</span>
        </a>

        <a href="${pageContext.request.contextPath}/user/expenses" 
           class="sidebar-link ${activeMenu == 'expenses' ? 'active' : ''}">
            <i class="bi bi-receipt"></i>
            <span>Expenses</span>
        </a>

        <a href="${pageContext.request.contextPath}/user/budgets" 
           class="sidebar-link ${activeMenu == 'budgets' ? 'active' : ''}">
            <i class="bi bi-pie-chart-fill"></i>
            <span>Budgets</span>
        </a>

        <a href="${pageContext.request.contextPath}/user/goals" 
           class="sidebar-link ${activeMenu == 'goals' ? 'active' : ''}">
            <i class="bi bi-bullseye"></i>
            <span>Financial Goals</span>
        </a>

        <!-- Admin Only Menu Section -->
        <c:if test="${sessionScope.userRole == 'ADMIN'}">
            <div class="nav-section-title mt-3">Administration</div>

            <a href="${pageContext.request.contextPath}/admin/dashboard" 
               class="sidebar-link ${activeMenu == 'admin-dashboard' ? 'active' : ''}">
                <i class="bi bi-shield-check"></i>
                <span>Admin Overview</span>
            </a>

            <a href="${pageContext.request.contextPath}/admin/users" 
               class="sidebar-link ${activeMenu == 'admin-users' ? 'active' : ''}">
                <i class="bi bi-people-fill"></i>
                <span>User Management</span>
            </a>

            <a href="${pageContext.request.contextPath}/admin/security" 
               class="sidebar-link ${activeMenu == 'admin-security' ? 'active' : ''}">
                <i class="bi bi-shield-lock-fill"></i>
                <span>Security Logs</span>
            </a>

            <a href="${pageContext.request.contextPath}/admin/settings" 
               class="sidebar-link ${activeMenu == 'admin-settings' ? 'active' : ''}">
                <i class="bi bi-sliders"></i>
                <span>System Settings</span>
            </a>
        </c:if>
    </div>

    <!-- Sidebar Bottom User Info -->
    <div class="p-3 border-top border-secondary border-opacity-25">
        <div class="d-flex align-items-center justify-content-between">
            <div class="d-flex align-items-center gap-2 overflow-hidden">
                <div class="user-avatar" style="width: 32px; height: 32px; font-size: 0.8rem;">
                    ${sessionScope.userName != null ? sessionScope.userName.substring(0, 1).toUpperCase() : 'U'}
                </div>
                <div class="overflow-hidden">
                    <div class="text-white text-truncate small fw-semibold">${sessionScope.userName}</div>
                    <div class="text-muted text-truncate" style="font-size: 0.7rem;">${sessionScope.userRole}</div>
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/logout" class="text-secondary text-decoration-none" title="Log Out">
                <i class="bi bi-box-arrow-right fs-5"></i>
            </a>
        </div>
    </div>
</aside>
