<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="User Management" />
<c:set var="pageHeader" value="User Directory & Access Control" />
<c:set var="activeMenu" value="admin-users" />
<%@ include file="/WEB-INF/jsp/fragments/header.jsp" %>

<div class="app-wrapper">
    <%@ include file="/WEB-INF/jsp/fragments/sidebar.jsp" %>

    <main class="app-main">
        <%@ include file="/WEB-INF/jsp/fragments/navbar.jsp" %>

        <div class="p-3 p-md-4">
            <%@ include file="/WEB-INF/jsp/fragments/alerts.jsp" %>

            <!-- Header -->
            <div class="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center gap-3 mb-4">
                <div>
                    <h4 class="fw-bold mb-1">User Directory</h4>
                    <p class="text-muted small mb-0">View registered accounts, update roles (User/Admin), and delete or deactivate accounts.</p>
                </div>
                <button type="button" class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addUserModal">
                    <i class="bi bi-person-plus me-1"></i> Add User
                </button>
            </div>

            <!-- Search & Filter Card -->
            <div class="card mb-4">
                <div class="card-body py-3">
                    <form action="${pageContext.request.contextPath}/admin/users" method="GET" class="row g-2 align-items-end">
                        <div class="col-12 col-md-5">
                            <label for="userSearch" class="form-label small fw-semibold">Search by Name or Email</label>
                            <div class="input-group input-group-sm">
                                <span class="input-group-text"><i class="bi bi-search"></i></span>
                                <input type="text" class="form-control" id="userSearch" name="search"
                                       placeholder="e.g. Alex Morgan or user@finance.com" value="${search}">
                            </div>
                        </div>
                        <div class="col-6 col-md-3">
                            <label for="roleFilter" class="form-label small fw-semibold">Role</label>
                            <select class="form-select form-select-sm" id="roleFilter" name="role">
                                <option value="ALL" ${selectedRole == 'ALL' ? 'selected' : ''}>All Roles</option>
                                <option value="USER" ${selectedRole == 'USER' ? 'selected' : ''}>USER</option>
                                <option value="ADMIN" ${selectedRole == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                            </select>
                        </div>
                        <div class="col-6 col-md-2">
                            <label for="statusFilter" class="form-label small fw-semibold">Status</label>
                            <select class="form-select form-select-sm" id="statusFilter" name="status">
                                <option value="ALL" ${selectedStatus == 'ALL' ? 'selected' : ''}>All Statuses</option>
                                <option value="ACTIVE" ${selectedStatus == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                                <option value="BLOCKED" ${selectedStatus == 'BLOCKED' ? 'selected' : ''}>BLOCKED</option>
                            </select>
                        </div>
                        <div class="col-12 col-md-2 d-flex gap-2">
                            <button type="submit" class="btn btn-sm btn-primary flex-grow-1">Filter</button>
                            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-sm btn-outline-secondary">Reset</a>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Users Table Card -->
            <div class="card">
                <div class="card-body p-0">
                    <c:choose>
                        <c:when test="${not empty users}">
                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th class="ps-3">User</th>
                                            <th>Role</th>
                                            <th>Status</th>
                                            <th>Registered</th>
                                            <th class="text-end pe-3">Access Controls</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="u" items="${users}">
                                            <c:set var="isSelf" value="${u.id == sessionScope.userId}" />
                                            <tr>
                                                <td class="ps-3">
                                                    <div class="d-flex align-items-center gap-2">
                                                        <div class="user-avatar" style="width: 32px; height: 32px; font-size: 0.8rem;">
                                                            ${u.name.substring(0, 1).toUpperCase()}
                                                        </div>
                                                        <div>
                                                            <div class="fw-semibold small">
                                                                ${u.name}
                                                                <c:if test="${isSelf}">
                                                                    <span class="badge bg-secondary-subtle text-secondary ms-1">You</span>
                                                                </c:if>
                                                            </div>
                                                            <div class="text-muted small">${u.email}</div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>
                                                    <span class="badge ${u.admin ? 'bg-primary' : 'bg-secondary-subtle text-secondary'} rounded-pill px-2 py-1">
                                                        ${u.role}
                                                    </span>
                                                </td>
                                                <td>
                                                    <span class="badge ${u.blocked ? 'bg-danger' : 'bg-success-subtle text-success'} rounded-pill px-2 py-1">
                                                        ${u.status}
                                                    </span>
                                                </td>
                                                <td class="small text-muted">${u.createdAt}</td>
                                                <td class="text-end pe-3">
                                                    <c:choose>
                                                        <c:when test="${isSelf}">
                                                            <span class="text-muted small fst-italic" title="Admins cannot block, demote, or delete their own account">
                                                                <i class="bi bi-shield-lock"></i> Protected
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="btn-group btn-group-sm">
                                                                <button class="btn ${u.blocked ? 'btn-outline-success' : 'btn-outline-warning'}"
                                                                        title="${u.blocked ? 'Reactivate Account' : 'Deactivate Account'}"
                                                                        onclick="openStatusModal(${u.id}, '${u.email}', '${u.status}')">
                                                                    <i class="bi ${u.blocked ? 'bi-check-circle' : 'bi-slash-circle'}"></i>
                                                                    ${u.blocked ? 'Activate' : 'Deactivate'}
                                                                </button>
                                                                <button class="btn btn-outline-secondary"
                                                                        title="Change Role"
                                                                        onclick="openRoleModal(${u.id}, '${u.email}', '${u.role}')">
                                                                    <i class="bi bi-arrow-repeat"></i> Role
                                                                </button>
                                                                <button class="btn btn-outline-danger"
                                                                        title="Delete Account"
                                                                        onclick="openDeleteModal(${u.id}, '${u.email}')">
                                                                    <i class="bi bi-trash"></i>
                                                                </button>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>

                            <!-- Pagination -->
                            <div class="p-3 border-top d-flex flex-column flex-sm-row justify-content-between align-items-center gap-2">
                                <div class="text-muted small">
                                    Page <strong>${currentPage}</strong> of <strong>${totalPages}</strong> (${totalUsers} users)
                                </div>
                                <c:if test="${totalPages > 1}">
                                    <nav>
                                        <ul class="pagination pagination-sm mb-0">
                                            <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                                <a class="page-link" href="${pageContext.request.contextPath}/admin/users?page=${currentPage - 1}&search=${search}&role=${selectedRole}&status=${selectedStatus}">Previous</a>
                                            </li>
                                            <c:forEach var="i" begin="1" end="${totalPages}">
                                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                    <a class="page-link" href="${pageContext.request.contextPath}/admin/users?page=${i}&search=${search}&role=${selectedRole}&status=${selectedStatus}">${i}</a>
                                                </li>
                                            </c:forEach>
                                            <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                                <a class="page-link" href="${pageContext.request.contextPath}/admin/users?page=${currentPage + 1}&search=${search}&role=${selectedRole}&status=${selectedStatus}">Next</a>
                                            </li>
                                        </ul>
                                    </nav>
                                </c:if>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <i class="bi bi-people empty-state-icon"></i>
                                <h5>No users found</h5>
                                <p class="text-muted small">No accounts match the specified criteria.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </main>
</div>

<!-- Add User Modal -->
<div class="modal fade" id="addUserModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold">Create User Account</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/users" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="addUser">
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="addName" class="form-label small fw-semibold">Full Name</label>
                        <input type="text" class="form-control form-control-sm" id="addName" name="name" required maxlength="100">
                    </div>
                    <div class="mb-3">
                        <label for="addEmail" class="form-label small fw-semibold">Email</label>
                        <input type="email" class="form-control form-control-sm" id="addEmail" name="email" required maxlength="150">
                    </div>
                    <div class="mb-3">
                        <label for="addRole" class="form-label small fw-semibold">Role</label>
                        <select class="form-select form-select-sm" id="addRole" name="role">
                            <option value="USER">USER</option>
                            <option value="ADMIN">ADMIN</option>
                        </select>
                    </div>
                    <div class="mb-0">
                        <label for="addPassword" class="form-label small fw-semibold">Temporary Password</label>
                        <input type="password" class="form-control form-control-sm" id="addPassword" name="password"
                               placeholder="Leave blank for FinanceFlow@123" minlength="8">
                        <div class="form-text">Strong password required if provided (8+ chars, mixed case, number, symbol).</div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Create Account</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Status Toggle Confirmation Modal -->
<div class="modal fade" id="statusModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold" id="statusModalTitle">Confirm Account Status</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/users" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="toggleStatus">
                <input type="hidden" id="statusUserId" name="userId">

                <div class="modal-body">
                    <p class="small mb-1">Change account status for:</p>
                    <p class="fw-bold mb-0 text-primary" id="statusUserEmail"></p>
                    <p class="text-muted small mt-2 mb-0">Deactivating blocks login and ends any active sessions.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary" id="statusSubmitBtn">Confirm</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Role Change Modal -->
<div class="modal fade" id="roleModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold">Change User Role</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/users" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="changeRole">
                <input type="hidden" id="roleUserId" name="userId">

                <div class="modal-body">
                    <p class="small mb-2">User: <strong id="roleUserEmail"></strong></p>
                    <label for="roleSelect" class="form-label small fw-semibold">Select New Role</label>
                    <select class="form-select form-select-sm" id="roleSelect" name="newRole">
                        <option value="USER">USER</option>
                        <option value="ADMIN">ADMIN</option>
                    </select>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Role</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Delete User Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold text-danger">Delete Account</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/users" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="deleteUser">
                <input type="hidden" id="deleteUserId" name="userId">

                <div class="modal-body">
                    <p class="small mb-1">Permanently delete account for:</p>
                    <p class="fw-bold mb-2 text-danger" id="deleteUserEmail"></p>
                    <p class="text-muted small mb-0">This removes the user and related expenses, budgets, and goals. This cannot be undone.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-danger">Yes, Delete</button>
                </div>
            </form>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/fragments/footer.jsp" %>

<script>
    function openStatusModal(userId, email, currentStatus) {
        document.getElementById('statusUserId').value = userId;
        document.getElementById('statusUserEmail').textContent = email;
        const newAction = currentStatus === 'ACTIVE' ? 'Deactivate' : 'Activate';
        document.getElementById('statusModalTitle').textContent = newAction + ' Account';
        document.getElementById('statusSubmitBtn').textContent = 'Yes, ' + newAction;
        new bootstrap.Modal(document.getElementById('statusModal')).show();
    }

    function openRoleModal(userId, email, currentRole) {
        document.getElementById('roleUserId').value = userId;
        document.getElementById('roleUserEmail').textContent = email;
        document.getElementById('roleSelect').value = currentRole;
        new bootstrap.Modal(document.getElementById('roleModal')).show();
    }

    function openDeleteModal(userId, email) {
        document.getElementById('deleteUserId').value = userId;
        document.getElementById('deleteUserEmail').textContent = email;
        new bootstrap.Modal(document.getElementById('deleteModal')).show();
    }
</script>
