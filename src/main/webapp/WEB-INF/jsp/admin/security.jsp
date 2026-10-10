<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Security Audit Logs" />
<c:set var="pageHeader" value="Security & Audit Trail" />
<c:set var="activeMenu" value="admin-security" />
<%@ include file="/WEB-INF/jsp/fragments/header.jsp" %>

<div class="app-wrapper">
    <%@ include file="/WEB-INF/jsp/fragments/sidebar.jsp" %>

    <main class="app-main">
        <%@ include file="/WEB-INF/jsp/fragments/navbar.jsp" %>

        <div class="p-3 p-md-4">
            <%@ include file="/WEB-INF/jsp/fragments/alerts.jsp" %>

            <!-- Top Header -->
            <div class="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center gap-3 mb-4">
                <div>
                    <h4 class="fw-bold mb-1">Security Audit Trail [Synchronized]</h4>
                    <p class="text-muted small mb-0">Immutable, append-only logs capturing critical security events, failed logins, and administrative operations.</p>
                </div>
                <div>
                    <span class="badge bg-primary-subtle text-primary border border-primary-subtle px-3 py-2">
                        <i class="bi bi-lock-fill me-1"></i> Synchronized Logger Active
                    </span>
                </div>
            </div>

            <!-- Security Logs Table Card -->
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <span class="fw-semibold">Recent Audit Entries (Last 100 Events)</span>
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
