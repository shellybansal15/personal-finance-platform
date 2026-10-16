<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Security Center" />
<c:set var="pageHeader" value="Security & Audit Trail" />
<c:set var="activeMenu" value="admin-security" />
<%@ include file="/WEB-INF/jsp/fragments/header.jsp" %>

<div class="app-wrapper">
    <%@ include file="/WEB-INF/jsp/fragments/sidebar.jsp" %>

    <main class="app-main">
        <%@ include file="/WEB-INF/jsp/fragments/navbar.jsp" %>

        <div class="p-3 p-md-4">
            <%@ include file="/WEB-INF/jsp/fragments/alerts.jsp" %>

            <div class="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center gap-3 mb-4">
                <div>
                    <h4 class="fw-bold mb-1">Security Center</h4>
                    <p class="text-muted small mb-0">System security logs, active sessions, and recent login attempts.</p>
                </div>
                <span class="badge bg-primary-subtle text-primary border border-primary-subtle px-3 py-2">
                    <i class="bi bi-lock-fill me-1"></i> Admin-only · Append-only audit trail
                </span>
            </div>

            <!-- Summary pills -->
            <div class="row g-3 mb-4">
                <div class="col-4">
                    <div class="card h-100">
                        <div class="card-body py-3 text-center">
                            <div class="text-muted small text-uppercase fw-semibold">Audit Logs</div>
                            <div class="fs-4 fw-bold">${logs.size()}</div>
                        </div>
                    </div>
                </div>
                <div class="col-4">
                    <div class="card h-100">
                        <div class="card-body py-3 text-center">
                            <div class="text-muted small text-uppercase fw-semibold">Active Sessions</div>
                            <div class="fs-4 fw-bold">${activeSessions.size()}</div>
                        </div>
                    </div>
                </div>
                <div class="col-4">
                    <div class="card h-100">
                        <div class="card-body py-3 text-center">
                            <div class="text-muted small text-uppercase fw-semibold">Live Failures</div>
                            <div class="fs-4 fw-bold">${liveFailures.size()}</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Active Sessions -->
            <div class="card mb-4">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <span class="fw-semibold"><i class="bi bi-broadcast-pin me-2 text-success"></i>Active Sessions</span>
                    <span class="badge bg-success-subtle text-success">${activeSessions.size()} live</span>
                </div>
                <div class="card-body p-0">
                    <c:choose>
                        <c:when test="${not empty activeSessions}">
                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th class="ps-3">Session</th>
                                            <th>User</th>
                                            <th>Role</th>
                                            <th>Login Time</th>
                                            <th>Last Access</th>
                                            <th class="text-end pe-3">Client IP</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="s" items="${activeSessions}">
                                            <tr>
                                                <td class="ps-3"><code class="small">${s.maskedSessionId}</code></td>
                                                <td>
                                                    <div class="fw-semibold small">${s.userName}</div>
                                                    <div class="text-muted small">${s.userEmail}</div>
                                                </td>
                                                <td>
                                                    <span class="badge ${s.admin ? 'bg-primary' : 'bg-secondary-subtle text-secondary'} rounded-pill px-2 py-1">
                                                        ${s.userRole}
                                                    </span>
                                                </td>
                                                <td class="small text-muted">${s.loginTime}</td>
                                                <td class="small text-muted">${s.lastAccessedAt}</td>
                                                <td class="text-end pe-3 small"><code class="px-2 py-1 rounded bg-secondary-subtle">${s.ipAddress}</code></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state py-4">
                                <i class="bi bi-broadcast empty-state-icon"></i>
                                <h5>No active sessions tracked</h5>
                                <p class="text-muted small mb-0">Sessions appear here after users sign in.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Live failed-attempt counters + Recent login attempts -->
            <div class="row g-3 mb-4">
                <div class="col-12 col-lg-5">
                    <div class="card h-100">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <span class="fw-semibold"><i class="bi bi-hourglass-split me-2 text-warning"></i>In-Memory Lockouts</span>
                            <span class="badge bg-warning-subtle text-warning">max ${maxLoginAttempts} / ${lockoutMinutes}m</span>
                        </div>
                        <div class="card-body p-0">
                            <c:choose>
                                <c:when test="${not empty liveFailures}">
                                    <div class="table-responsive">
                                        <table class="table table-sm table-hover align-middle mb-0">
                                            <thead class="table-light">
                                                <tr>
                                                    <th class="ps-3">Identifier</th>
                                                    <th>Attempts</th>
                                                    <th class="text-end pe-3">State</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="entry" items="${liveFailures}">
                                                    <tr>
                                                        <td class="ps-3 small"><code>${entry.key}</code></td>
                                                        <td class="small">${entry.value.count} / ${maxLoginAttempts}</td>
                                                        <td class="text-end pe-3">
                                                            <c:choose>
                                                                <c:when test="${entry.value.count >= maxLoginAttempts}">
                                                                    <span class="badge bg-danger">LOCKED</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="badge bg-warning-subtle text-warning">WATCH</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="p-4 text-center text-muted small">No active failed-attempt counters.</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <div class="col-12 col-lg-7">
                    <div class="card h-100">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <span class="fw-semibold"><i class="bi bi-box-arrow-in-right me-2 text-danger"></i>Recent Login Attempts</span>
                            <span class="badge bg-secondary-subtle text-secondary">${loginAttempts.size()} records</span>
                        </div>
                        <div class="card-body p-0">
                            <c:choose>
                                <c:when test="${not empty loginAttempts}">
                                    <div class="table-responsive" style="max-height: 320px; overflow-y: auto;">
                                        <table class="table table-sm table-hover align-middle mb-0">
                                            <thead class="table-light sticky-top">
                                                <tr>
                                                    <th class="ps-3">When</th>
                                                    <th>Account</th>
                                                    <th>Event</th>
                                                    <th class="text-end pe-3">IP</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="log" items="${loginAttempts}">
                                                    <tr>
                                                        <td class="ps-3 small text-muted">${log.createdAt}</td>
                                                        <td class="small">
                                                            <c:choose>
                                                                <c:when test="${not empty log.userEmail}">${log.userEmail}</c:when>
                                                                <c:otherwise><span class="text-muted fst-italic">Unauthenticated</span></c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td class="small">
                                                            <c:choose>
                                                                <c:when test="${log.action.toLowerCase().contains('failed') || log.action.toLowerCase().contains('lockout') || log.action.toLowerCase().contains('mismatch')}">
                                                                    <span class="text-danger">${log.action}</span>
                                                                </c:when>
                                                                <c:otherwise>${log.action}</c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td class="text-end pe-3 small"><code>${log.ipAddress}</code></td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="p-4 text-center text-muted small">No login attempts recorded yet.</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Full Security Logs -->
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <span class="fw-semibold"><i class="bi bi-journal-text me-2"></i>System Security Logs (Last 100)</span>
                    <span class="badge bg-secondary-subtle text-secondary">${logs.size()} records</span>
                </div>
                <div class="card-body p-0">
                    <c:choose>
                        <c:when test="${not empty logs}">
                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th class="ps-3" style="width: 80px;">ID</th>
                                            <th style="width: 180px;">Timestamp</th>
                                            <th>Account Email</th>
                                            <th>Action Description</th>
                                            <th class="text-end pe-3" style="width: 140px;">Client IP</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="log" items="${logs}">
                                            <tr>
                                                <td class="ps-3 text-muted small">#${log.id}</td>
                                                <td class="small text-muted">${log.createdAt}</td>
                                                <td class="small fw-medium">
                                                    <c:choose>
                                                        <c:when test="${not empty log.userEmail}">
                                                            <i class="bi bi-person-check text-success me-1"></i>${log.userEmail}
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="text-muted fst-italic"><i class="bi bi-question-circle me-1"></i>Unauthenticated</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="small">
                                                    <c:choose>
                                                        <c:when test="${log.action.toLowerCase().contains('failed') || log.action.toLowerCase().contains('lockout')}">
                                                            <span class="text-danger fw-semibold"><i class="bi bi-exclamation-triangle-fill me-1"></i>${log.action}</span>
                                                        </c:when>
                                                        <c:when test="${log.action.toLowerCase().contains('admin')}">
                                                            <span class="text-primary fw-semibold"><i class="bi bi-shield-check me-1"></i>${log.action}</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span>${log.action}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="text-end pe-3 small text-muted">
                                                    <code class="px-2 py-1 rounded bg-secondary-subtle">${log.ipAddress}</code>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <i class="bi bi-shield-lock empty-state-icon"></i>
                                <h5>No audit logs recorded</h5>
                                <p class="text-muted small">All system interactions will appear here automatically.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/fragments/footer.jsp" %>
