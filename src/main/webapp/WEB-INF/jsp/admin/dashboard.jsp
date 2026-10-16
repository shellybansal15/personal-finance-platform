<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin Overview" />
<c:set var="pageHeader" value="System Administration" />
<c:set var="activeMenu" value="admin-dashboard" />
<%@ include file="/WEB-INF/jsp/fragments/header.jsp" %>

<div class="app-wrapper">
    <%@ include file="/WEB-INF/jsp/fragments/sidebar.jsp" %>

    <main class="app-main">
        <%@ include file="/WEB-INF/jsp/fragments/navbar.jsp" %>

        <div class="p-3 p-md-4">
            <%@ include file="/WEB-INF/jsp/fragments/alerts.jsp" %>

            <!-- Admin Banner -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h4 class="fw-bold mb-1"><i class="bi bi-shield-check text-success me-2"></i>Admin Command Center</h4>
                    <p class="text-muted small mb-0">Platform security oversight, user management, and system parameters.</p>
                </div>
                <div>
                    <span class="badge bg-success-subtle text-success border border-success-subtle px-3 py-2">
                        <i class="bi bi-circle-fill me-1 small"></i> System Healthy
                    </span>
                </div>
            </div>

            <!-- Admin Stat Cards -->
            <div class="row g-3 mb-4">
                <div class="col-6 col-md-3">
                    <div class="card card-hover h-100">
                        <div class="card-body d-flex align-items-center justify-content-between">
                            <div>
                                <span class="text-muted small fw-semibold text-uppercase d-block mb-1">Total Users</span>
                                <h3 class="fw-bold mb-0">${stats.totalUsers}</h3>
                                <span class="text-muted" style="font-size:0.7rem;">${stats.activeUsers} active · ${stats.blockedUsers} blocked</span>
                            </div>
                            <div class="stat-card-icon stat-icon-blue">
                                <i class="bi bi-people-fill"></i>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-6 col-md-3">
                    <div class="card card-hover h-100">
                        <div class="card-body d-flex align-items-center justify-content-between">
                            <div>
                                <span class="text-muted small fw-semibold text-uppercase d-block mb-1">Active Sessions</span>
                                <h3 class="fw-bold mb-0">${stats.activeSessions}</h3>
                            </div>
                            <div class="stat-card-icon stat-icon-green">
                                <i class="bi bi-broadcast-pin"></i>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-6 col-md-3">
                    <div class="card card-hover h-100">
                        <div class="card-body d-flex align-items-center justify-content-between">
                            <div>
                                <span class="text-muted small fw-semibold text-uppercase d-block mb-1">Platform Expenses</span>
                                <h3 class="fw-bold mb-0">${stats.totalExpensesCount}</h3>
                            </div>
                            <div class="stat-card-icon stat-icon-amber">
                                <i class="bi bi-receipt"></i>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-6 col-md-3">
                    <div class="card card-hover h-100">
                        <div class="card-body d-flex align-items-center justify-content-between">
                            <div>
                                <span class="text-muted small fw-semibold text-uppercase d-block mb-1">Audit Entries</span>
                                <h3 class="fw-bold mb-0">${stats.securityLogsCount}</h3>
                            </div>
                            <div class="stat-card-icon stat-icon-blue">
                                <i class="bi bi-shield-lock-fill"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Quick admin modules -->
            <div class="row g-3 mb-4">
                <div class="col-12 col-md-4">
                    <a href="${pageContext.request.contextPath}/admin/users" class="card card-hover text-decoration-none h-100">
                        <div class="card-body">
                            <div class="fw-semibold text-dark"><i class="bi bi-people-fill text-primary me-2"></i>User Management</div>
                            <p class="text-muted small mb-0 mt-1">View accounts, change roles, deactivate or delete users.</p>
                        </div>
                    </a>
                </div>
                <div class="col-12 col-md-4">
                    <a href="${pageContext.request.contextPath}/admin/security" class="card card-hover text-decoration-none h-100">
                        <div class="card-body">
                            <div class="fw-semibold text-dark"><i class="bi bi-shield-lock-fill text-danger me-2"></i>Security Center</div>
                            <p class="text-muted small mb-0 mt-1">Audit logs, live sessions, and recent login attempts.</p>
                        </div>
                    </a>
                </div>
                <div class="col-12 col-md-4">
                    <a href="${pageContext.request.contextPath}/admin/settings" class="card card-hover text-decoration-none h-100">
                        <div class="card-body">
                            <div class="fw-semibold text-dark"><i class="bi bi-sliders text-success me-2"></i>System Settings</div>
                            <p class="text-muted small mb-0 mt-1">Configure application-wide parameters and toggles.</p>
                        </div>
                    </a>
                </div>
            </div>

            <!-- Recent Security Events & Recent Users -->
            <div class="row g-3">
                <!-- Left: Recent Security Logs -->
                <div class="col-12 col-lg-7">
                    <div class="card h-100">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <span class="fw-semibold">Recent Security Audit Events [Synchronized]</span>
                            <a href="${pageContext.request.contextPath}/admin/security" class="text-success small fw-semibold text-decoration-none">
                                View Full Log <i class="bi bi-arrow-right"></i>
                            </a>
                        </div>
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th class="ps-3">Timestamp</th>
                                            <th>Account</th>
                                            <th>Action</th>
                                            <th class="text-end pe-3">IP Address</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="log" items="${recentLogs}">
                                            <tr>
                                                <td class="ps-3 small text-muted">${log.createdAt}</td>
                                                <td class="small fw-medium">${log.userEmail != null ? log.userEmail : '<span class="text-muted">Anonymous</span>'}</td>
                                                <td class="small">${log.action}</td>
                                                <td class="text-end pe-3 small text-muted"><code>${log.ipAddress}</code></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Right: Recent Users -->
                <div class="col-12 col-lg-5">
                    <div class="card h-100">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <span class="fw-semibold">Recently Registered Accounts</span>
                            <a href="${pageContext.request.contextPath}/admin/users" class="text-success small fw-semibold text-decoration-none">
                                Manage <i class="bi bi-arrow-right"></i>
                            </a>
                        </div>
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th class="ps-3">User</th>
                                            <th>Role</th>
                                            <th class="text-end pe-3">Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="u" items="${recentUsers}">
                                            <tr>
                                                <td class="ps-3">
                                                    <div class="fw-semibold small">${u.name}</div>
                                                    <div class="text-muted small">${u.email}</div>
                                                </td>
                                                <td>
                                                    <span class="badge ${u.admin ? 'bg-primary' : 'bg-secondary-subtle text-secondary'} small">
                                                        ${u.role}
                                                    </span>
                                                </td>
                                                <td class="text-end pe-3">
                                                    <span class="badge ${u.blocked ? 'bg-danger' : 'bg-success-subtle text-success'} small">
                                                        ${u.status}
                                                    </span>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/fragments/footer.jsp" %>
