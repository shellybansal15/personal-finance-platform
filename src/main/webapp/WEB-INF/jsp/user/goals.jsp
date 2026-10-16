<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.finance.util.CurrencyUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="en_IN"/>
<c:set var="pageTitle" value="Financial Goals" />
<c:set var="pageHeader" value="Financial Goals [Trackable]" />
<c:set var="activeMenu" value="goals" />
<%@ include file="/WEB-INF/jsp/fragments/header.jsp" %>

<div class="app-wrapper">
    <%@ include file="/WEB-INF/jsp/fragments/sidebar.jsp" %>

    <main class="app-main">
        <%@ include file="/WEB-INF/jsp/fragments/navbar.jsp" %>

        <div class="p-3 p-md-4">
            <%@ include file="/WEB-INF/jsp/fragments/alerts.jsp" %>

            <!-- Top Header & Summary -->
            <div class="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center gap-3 mb-4">
                <div>
                    <h4 class="fw-bold mb-1">Savings Goals [Trackable]</h4>
                    <p class="text-muted small mb-0">Plan for milestones, track contributions, and celebrate accomplishments.</p>
                </div>
                <button class="btn btn-primary d-flex align-items-center gap-2" data-bs-toggle="modal" data-bs-target="#createGoalModal">
                    <i class="bi bi-plus-lg"></i> Create Goal
                </button>
            </div>

            <!-- Total Savings Banner -->
            <div class="card mb-4 border-success-subtle bg-success-subtle bg-opacity-25">
                <div class="card-body py-3 d-flex flex-column flex-sm-row justify-content-between align-items-sm-center gap-2">
                    <div class="d-flex align-items-center gap-3">
                        <div class="stat-card-icon stat-icon-green" style="width: 42px; height: 42px;">
                            <i class="bi bi-piggy-bank fs-4"></i>
                        </div>
                        <div>
                            <span class="small text-muted fw-semibold">Total Accumulated Savings</span>
                            <h4 class="fw-bold text-success mb-0"><%= CurrencyUtil.format((Double) request.getAttribute("totalSaved")) %></h4>
                        </div>
                    </div>
                    <div class="text-muted small">
                        <i class="bi bi-shield-check text-success me-1"></i>Every contribution updates goal targets in real time.
                    </div>
                </div>
            </div>

            <!-- Goals Grid -->
            <div class="row g-3">
                <c:choose>
                    <c:when test="${not empty goals}">
                        <c:forEach var="g" items="${goals}">
                            <c:set var="pct" value="${g.progressPercentage}" />
                            <div class="col-12 col-md-6 col-xl-4">
                                <div class="card card-hover h-100 ${g.status == 'COMPLETED' ? 'border-success' : ''}">
                                    <div class="card-body">
                                        <div class="d-flex justify-content-between align-items-start mb-2">
                                            <h5 class="fw-bold mb-1 text-truncate" title="${g.description}">${g.description}</h5>
                                            <div class="dropdown">
                                                <button class="btn btn-sm btn-light border-0" type="button" data-bs-toggle="dropdown">
                                                    <i class="bi bi-three-dots-vertical"></i>
                                                </button>
                                                <ul class="dropdown-menu dropdown-menu-end shadow-sm">
                                                    <li>
                                                        <button class="dropdown-item small" 
                                                                onclick="openDepositModal(${g.id}, '${g.description}')">
                                                            <i class="bi bi-cash-coin me-2 text-success"></i>Add Funds
                                                        </button>
                                                    </li>
                                                    <li>
                                                        <button class="dropdown-item small" 
                                                                onclick="openEditGoalModal(${g.id}, '${g.description}', '${g.targetAmount}', '${g.deadline}', '${g.status}')">
                                                            <i class="bi bi-pencil me-2"></i>Edit
                                                        </button>
                                                    </li>
                                                    <li><hr class="dropdown-divider"></li>
                                                    <li>
                                                        <button class="dropdown-item small text-danger" 
                                                                onclick="openDeleteGoalModal(${g.id}, '${g.description}')">
                                                            <i class="bi bi-trash me-2"></i>Delete
                                                        </button>
                                                    </li>
                                                </ul>
                                            </div>
                                        </div>

                                        <div class="d-flex align-items-center gap-2 mb-3">
                                            <c:choose>
                                                <c:when test="${g.status == 'COMPLETED'}">
                                                    <span class="badge bg-success text-white small">
                                                        <i class="bi bi-check-circle-fill me-1"></i> Completed
                                                    </span>
                                                </c:when>
                                                <c:when test="${g.overdue}">
                                                    <span class="badge bg-danger-subtle text-danger small">
                                                        <i class="bi bi-clock-history me-1"></i> Overdue (${g.deadline})
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary-subtle text-secondary small">
                                                        <i class="bi bi-hourglass-split me-1"></i> Target: ${g.deadline}
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <!-- Saved vs Target Numbers -->
                                        <div class="d-flex justify-content-between align-items-end mb-2">
                                            <div>
                                                <span class="text-muted small d-block">Saved</span>
                                                <h4 class="fw-bold mb-0 text-success">
                                                    <%= CurrencyUtil.format(((com.finance.model.Goal) pageContext.getAttribute("g")).getSavedAmount()) %>
                                                </h4>
                                            </div>
                                            <div class="text-end">
                                                <span class="text-muted small d-block">Target</span>
                                                <span class="fw-semibold text-secondary">
                                                    <%= CurrencyUtil.format(((com.finance.model.Goal) pageContext.getAttribute("g")).getTargetAmount()) %>
                                                </span>
                                            </div>
                                        </div>

                                        <!-- Progress Bar -->
                                        <div class="progress mb-2" style="height: 10px;">
                                            <div class="progress-bar ${g.status == 'COMPLETED' ? 'progress-bar-green' : (pct >= 80 ? 'progress-bar-amber' : 'bg-primary')}" 
                                                 role="progressbar" 
                                                 style="width: ${pct > 100 ? 100 : pct}%;" 
                                                 aria-valuenow="${pct}" aria-valuemin="0" aria-valuemax="100"></div>
                                        </div>

                                        <div class="d-flex justify-content-between align-items-center mb-3 small">
                                            <span class="text-muted"><fmt:formatNumber value="${pct}" maxFractionDigits="1"/>% Achieved</span>
                                            <span class="text-muted">Remaining: <%= CurrencyUtil.format(((com.finance.model.Goal) pageContext.getAttribute("g")).getRemainingAmount()) %></span>
                                        </div>

                                        <!-- Action Deposit Button -->
                                        <button class="btn btn-sm btn-outline-success w-100 d-flex align-items-center justify-content-center gap-1"
                                                onclick="openDepositModal(${g.id}, '${g.description}')">
                                            <i class="bi bi-plus-circle"></i> Contribute Funds
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="col-12">
                            <div class="card">
                                <div class="card-body empty-state">
                                    <i class="bi bi-bullseye empty-state-icon"></i>
                                    <h5>No financial goals set yet</h5>
                                    <p class="text-muted small">Dreaming of a new laptop, vacation, or emergency fund? Create your first savings goal today.</p>
                                    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createGoalModal">
                                        <i class="bi bi-plus-lg me-1"></i> Create First Goal
                                    </button>
                                </div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>
</div>

<!-- Create Goal Modal -->
<div class="modal fade" id="createGoalModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold"><i class="bi bi-bullseye text-primary me-2"></i>Create Financial Goal</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/user/goals" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="create">

                <div class="modal-body">
                    <div class="mb-3">
                        <label for="createGoalDesc" class="form-label small fw-semibold">Goal Description *</label>
                        <input type="text" class="form-control" id="createGoalDesc" name="description" 
                               placeholder="e.g. Emergency Fund (6 Months), New MacBook" required>
                    </div>

                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label for="createGoalTarget" class="form-label small fw-semibold">Target Amount (₹) *</label>
                            <div class="input-group">
                                <span class="input-group-text">₹</span>
                                <input type="number" step="0.01" min="0.01" class="form-control" id="createGoalTarget" name="targetAmount" placeholder="3000.00" required>
                            </div>
                        </div>
                        <div class="col-6">
                            <label for="createGoalInitial" class="form-label small fw-semibold">Starting Saved (₹)</label>
                            <div class="input-group">
                                <span class="input-group-text">₹</span>
                                <input type="number" step="0.01" min="0.00" class="form-control" id="createGoalInitial" name="initialSaved" value="0.00">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="createGoalDeadline" class="form-label small fw-semibold">Target Deadline Date *</label>
                        <input type="date" class="form-control" id="createGoalDeadline" name="deadline" required 
                               value="<%= java.time.LocalDate.now().plusMonths(6) %>">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Create Goal</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Contribute Funds Modal -->
<div class="modal fade" id="depositModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold text-success"><i class="bi bi-cash-coin me-2"></i>Contribute Funds</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/user/goals" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="deposit">
                <input type="hidden" id="depositGoalId" name="goalId">

                <div class="modal-body">
                    <p class="small text-muted mb-2">Goal: <strong id="depositGoalDesc"></strong></p>
                    <label for="depositAmount" class="form-label small fw-semibold">Deposit Amount (₹) *</label>
                    <div class="input-group">
                        <span class="input-group-text">₹</span>
                        <input type="number" step="0.01" min="0.01" class="form-control" id="depositAmount" name="depositAmount" placeholder="100.00" required autofocus>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-success">Deposit Funds</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Edit Goal Modal -->
<div class="modal fade" id="editGoalModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square text-primary me-2"></i>Edit Goal</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/user/goals" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" id="editGoalId" name="goalId">

                <div class="modal-body">
                    <div class="mb-3">
                        <label for="editGoalDesc" class="form-label small fw-semibold">Goal Description *</label>
                        <input type="text" class="form-control" id="editGoalDesc" name="description" required>
                    </div>

                    <div class="mb-3">
                        <label for="editGoalTarget" class="form-label small fw-semibold">Target Amount (₹) *</label>
                        <div class="input-group">
                            <span class="input-group-text">₹</span>
                            <input type="number" step="0.01" min="0.01" class="form-control" id="editGoalTarget" name="targetAmount" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="editGoalDeadline" class="form-label small fw-semibold">Target Deadline Date *</label>
                        <input type="date" class="form-control" id="editGoalDeadline" name="deadline" required>
                    </div>

                    <div class="mb-3">
                        <label for="editGoalStatus" class="form-label small fw-semibold">Status *</label>
                        <select class="form-select" id="editGoalStatus" name="status">
                            <option value="IN_PROGRESS">IN_PROGRESS</option>
                            <option value="COMPLETED">COMPLETED</option>
                            <option value="CANCELLED">CANCELLED</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Changes</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Delete Goal Modal -->
<div class="modal fade" id="deleteGoalModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold text-danger"><i class="bi bi-trash me-2"></i>Delete Goal</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/user/goals" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" id="deleteGoalId" name="goalId">

                <div class="modal-body">
                    <p class="small mb-1">Remove this savings goal?</p>
                    <p class="fw-bold mb-0 text-danger" id="deleteGoalTitle"></p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-danger">Delete</button>
                </div>
            </form>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/fragments/footer.jsp" %>

<script>
    function openDepositModal(id, desc) {
        document.getElementById('depositGoalId').value = id;
        document.getElementById('depositGoalDesc').textContent = desc;
        document.getElementById('depositAmount').value = '';
        new bootstrap.Modal(document.getElementById('depositModal')).show();
    }

    function openEditGoalModal(id, desc, target, deadline, status) {
        document.getElementById('editGoalId').value = id;
        document.getElementById('editGoalDesc').value = desc;
        document.getElementById('editGoalTarget').value = target;
        document.getElementById('editGoalDeadline').value = deadline;
        document.getElementById('editGoalStatus').value = status;
        new bootstrap.Modal(document.getElementById('editGoalModal')).show();
    }

    function openDeleteGoalModal(id, desc) {
        document.getElementById('deleteGoalId').value = id;
        document.getElementById('deleteGoalTitle').textContent = desc;
        new bootstrap.Modal(document.getElementById('deleteGoalModal')).show();
    }
</script>
