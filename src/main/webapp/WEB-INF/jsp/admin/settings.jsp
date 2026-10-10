<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="System Settings" />
<c:set var="pageHeader" value="Platform Preferences & Toggles" />
<c:set var="activeMenu" value="admin-settings" />
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
                    <h4 class="fw-bold mb-1">System Preferences & Feature Toggles</h4>
                    <p class="text-muted small mb-0">Configure operational parameters, security thresholds, and global application behavior.</p>
                </div>
            </div>

            <!-- Settings Grid / Table -->
            <div class="row g-3">
                <div class="col-12 col-lg-8">
                    <div class="card">
                        <div class="card-header">
                            <span class="fw-semibold">Active System Parameters</span>
                        </div>
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th class="ps-3">Setting Key</th>
                                            <th>Current Value</th>
                                            <th>Last Modified</th>
                                            <th class="text-end pe-3">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="s" items="${settings}">
                                            <tr>
                                                <td class="ps-3">
                                                    <code class="fw-bold text-primary">${s.settingKey}</code>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${s.settingValue == 'true' || s.settingValue == 'false'}">
                                                            <span class="badge ${s.settingValue == 'true' ? 'bg-success-subtle text-success' : 'bg-secondary-subtle text-secondary'} rounded-pill px-2 py-1">
                                                                ${s.settingValue}
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="fw-medium">${s.settingValue}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="small text-muted">${s.updatedAt}</td>
                                                <td class="text-end pe-3">
                                                    <button class="btn btn-sm btn-outline-primary"
                                                            onclick="openEditSettingModal('${s.settingKey}', '${s.settingValue}')">
                                                        <i class="bi bi-pencil me-1"></i> Edit
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Right Explanatory Guide Box -->
                <div class="col-12 col-lg-4">
                    <div class="card mb-3">
                        <div class="card-header fw-semibold">
                            <i class="bi bi-info-circle text-primary me-2"></i>Setting Descriptions
                        </div>
                        <div class="card-body small">
                            <ul class="list-unstyled d-flex flex-column gap-2 mb-0">
                                <li>
                                    <code>allow_registration</code>: Toggles open user self-registration.
                                </li>
                                <li>
                                    <code>max_login_attempts</code>: Number of failed password attempts before 15-min lockout (enforced by ConcurrentHashMap).
                                </li>
                                <li>
                                    <code>session_timeout_minutes</code>: Inactivity timeout before HttpOnly session expires (15 min standard).
                                </li>
                                <li>
                                    <code>platform_currency</code>: Default symbol for monetary values (USD, EUR, INR).
                                </li>
                                <li>
                                    <code>maintenance_mode</code>: Temporarily limits access for database updates.
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<!-- Edit Setting Modal -->
<div class="modal fade" id="editSettingModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold">Update Setting</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/settings" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" id="editSettingKey" name="settingKey">

                <div class="modal-body">
                    <p class="small text-muted mb-2">Parameter: <strong id="settingKeyLabel" class="text-primary"></strong></p>
                    <label for="editSettingValue" class="form-label small fw-semibold">New Value</label>
                    <input type="text" class="form-control" id="editSettingValue" name="settingValue" required autofocus>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Value</button>
                </div>
            </form>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/fragments/footer.jsp" %>

<script>
    function openEditSettingModal(key, value) {
        document.getElementById('editSettingKey').value = key;
        document.getElementById('settingKeyLabel').textContent = key;
        document.getElementById('editSettingValue').value = value;
        new bootstrap.Modal(document.getElementById('editSettingModal')).show();
    }
</script>
