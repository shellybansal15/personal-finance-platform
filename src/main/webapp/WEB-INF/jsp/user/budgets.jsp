<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.finance.util.CurrencyUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="en_IN"/>
<c:set var="pageTitle" value="Budgets" />
<c:set var="pageHeader" value="Category Budgets" />
<c:set var="activeMenu" value="budgets" />
<%@ include file="/WEB-INF/jsp/fragments/header.jsp" %>

<div class="app-wrapper">
    <%@ include file="/WEB-INF/jsp/fragments/sidebar.jsp" %>

    <main class="app-main">
        <%@ include file="/WEB-INF/jsp/fragments/navbar.jsp" %>

        <div class="p-3 p-md-4">
            <%@ include file="/WEB-INF/jsp/fragments/alerts.jsp" %>

            <!-- Top Header & Actions -->
            <div class="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center gap-3 mb-4">
                <div>
                    <h4 class="fw-bold mb-1">Budget Limits [Trackable]</h4>
                    <p class="text-muted small mb-0">Establish spending caps per category and receive visual alert thresholds.</p>
                </div>
                <button class="btn btn-primary d-flex align-items-center gap-2" data-bs-toggle="modal" data-bs-target="#createBudgetModal">
                    <i class="bi bi-plus-lg"></i> Create Budget
                </button>
            </div>

            <!-- Budgets Cards Grid -->
            <div class="row g-3">
                <c:choose>
                    <c:when test="${not empty budgets}">
                        <c:forEach var="b" items="${budgets}">
                            <c:set var="pct" value="${b.progressPercentage}" />
                            <div class="col-12 col-md-6 col-xl-4">
                                <div class="card card-hover h-100">
                                    <div class="card-body">
                                        <div class="d-flex justify-content-between align-items-start mb-3">
                                            <div>
                                                <h5 class="fw-bold mb-1">${b.categoryName}</h5>
                                                <span class="text-muted small">
                                                    <i class="bi bi-calendar3 me-1"></i>${b.startDate} to ${b.endDate}
                                                </span>
                                            </div>
                                            <div class="dropdown">
                                                <button class="btn btn-sm btn-light border-0" type="button" data-bs-toggle="dropdown">
                                                    <i class="bi bi-three-dots-vertical"></i>
                                                </button>
                                                <ul class="dropdown-menu dropdown-menu-end shadow-sm">
                                                    <li>
                                                        <button class="dropdown-item small" 
                                                                onclick="openEditBudgetModal(${b.id}, ${b.categoryId}, '${b.amount}', '${b.startDate}', '${b.endDate}')">
                                                            <i class="bi bi-pencil me-2"></i>Edit
                                                        </button>
                                                    </li>
                                                    <li><hr class="dropdown-divider"></li>
                                                    <li>
                                                        <button class="dropdown-item small text-danger" 
                                                                onclick="openDeleteBudgetModal(${b.id}, '${b.categoryName}')">
                                                            <i class="bi bi-trash me-2"></i>Delete
                                                        </button>
                                                    </li>
                                                </ul>
                                            </div>
                                        </div>

                                        <div class="d-flex justify-content-between align-items-end mb-2">
                                            <div>
                                                <span class="text-muted small d-block">Spent</span>
                                                <h4 class="fw-bold mb-0 ${pct >= 100 ? 'text-danger' : (pct >= 80 ? 'text-warning' : 'text-primary')}">
                                                    <%= CurrencyUtil.format(((com.finance.model.Budget) pageContext.getAttribute("b")).getSpentAmount()) %>
                                                </h4>
                                            </div>
                                            <div class="text-end">
                                                <span class="text-muted small d-block">Target Limit</span>
                                                <span class="fw-semibold text-secondary">
                                                    <%= CurrencyUtil.format(((com.finance.model.Budget) pageContext.getAttribute("b")).getAmount()) %>
                                                </span>
                                            </div>
                                        </div>

                                        <!-- Dynamic Progress Bar -->
                                        <div class="progress mb-2" style="height: 12px;">
                                            <div class="progress-bar ${pct >= 100 ? 'progress-bar-red' : (pct >= 80 ? 'progress-bar-amber' : 'progress-bar-green')}" 
                                                 role="progressbar" 
                                                 style="width: ${pct > 100 ? 100 : pct}%;" 
                                                 aria-valuenow="${pct}" aria-valuemin="0" aria-valuemax="100"></div>
                                        </div>

                                        <div class="d-flex justify-content-between align-items-center small">
                                            <span class="text-muted">
                                                <fmt:formatNumber value="${pct}" maxFractionDigits="1"/>% consumed
                                            </span>
                                            <c:choose>
                                                <c:when test="${pct >= 100}">
                                                    <span class="badge bg-danger-subtle text-danger fw-semibold">
                                                        <i class="bi bi-exclamation-triangle-fill"></i> Limit Exceeded
                                                    </span>
                                                </c:when>
                                                <c:when test="${pct >= 80}">
                                                    <span class="badge bg-warning-subtle text-warning fw-semibold">
                                                        <i class="bi bi-exclamation-circle-fill"></i> Nearing Limit
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-success-subtle text-success fw-semibold">
                                                        <i class="bi bi-check-circle"></i> On Track (<%= CurrencyUtil.format(((com.finance.model.Budget) pageContext.getAttribute("b")).getRemainingAmount()) %> left)
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="col-12">
                            <div class="card">
                                <div class="card-body empty-state">
                                    <i class="bi bi-pie-chart empty-state-icon"></i>
                                    <h5>No budgets created yet</h5>
                                    <p class="text-muted small">Set monthly spending limits for categories like Food, Utilities, or Rent to prevent overspending.</p>
                                    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createBudgetModal">
                                        <i class="bi bi-plus-lg me-1"></i> Create First Budget
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

<!-- Create Budget Modal -->
<div class="modal fade" id="createBudgetModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold"><i class="bi bi-plus-circle text-primary me-2"></i>Create Category Budget</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/user/budgets" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="create">

                <div class="modal-body">
                    <div class="mb-3">
                        <label for="createCategory" class="form-label small fw-semibold">Category *</label>
                        <select class="form-select" id="createCategory" name="categoryId" required>
                            <option value="">-- Select Category --</option>
                            <c:forEach var="c" items="${categories}">
                                <c:if test="${c.type == 'EXPENSE'}">
                                    <option value="${c.id}">${c.name}</option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label for="createAmount" class="form-label small fw-semibold">Monthly Limit Amount (₹) *</label>
                        <div class="input-group">
                            <span class="input-group-text">₹</span>
                            <input type="number" step="0.01" min="0.01" class="form-control" id="createAmount" name="amount" placeholder="500.00" required>
                        </div>
                    </div>

                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label for="createStartDate" class="form-label small fw-semibold">Start Date *</label>
                            <input type="date" class="form-control" id="createStartDate" name="startDate" required 
                                   value="<%= java.time.LocalDate.now().withDayOfMonth(1) %>">
                        </div>
                        <div class="col-6">
                            <label for="createEndDate" class="form-label small fw-semibold">End Date *</label>
                            <input type="date" class="form-control" id="createEndDate" name="endDate" required 
                                   value="<%= java.time.LocalDate.now().withDayOfMonth(java.time.LocalDate.now().lengthOfMonth()) %>">
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Create Budget</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Edit Budget Modal -->
<div class="modal fade" id="editBudgetModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square text-primary me-2"></i>Edit Budget Limit</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/user/budgets" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" id="editBudgetId" name="budgetId">

                <div class="modal-body">
                    <div class="mb-3">
                        <label for="editCategorySelect" class="form-label small fw-semibold">Category *</label>
                        <select class="form-select" id="editCategorySelect" name="categoryId" required>
                            <c:forEach var="c" items="${categories}">
                                <c:if test="${c.type == 'EXPENSE'}">
                                    <option value="${c.id}">${c.name}</option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label for="editBudgetAmount" class="form-label small fw-semibold">Limit Amount (₹) *</label>
                        <div class="input-group">
                            <span class="input-group-text">₹</span>
                            <input type="number" step="0.01" min="0.01" class="form-control" id="editBudgetAmount" name="amount" required>
                        </div>
                    </div>

                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label for="editStartDate" class="form-label small fw-semibold">Start Date *</label>
                            <input type="date" class="form-control" id="editStartDate" name="startDate" required>
                        </div>
                        <div class="col-6">
                            <label for="editEndDate" class="form-label small fw-semibold">End Date *</label>
                            <input type="date" class="form-control" id="editEndDate" name="endDate" required>
                        </div>
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

<!-- Delete Budget Modal -->
<div class="modal fade" id="deleteBudgetModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold text-danger"><i class="bi bi-trash me-2"></i>Delete Budget</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/user/budgets" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" id="deleteBudgetId" name="budgetId">

                <div class="modal-body">
                    <p class="small mb-1">Remove this budget limit?</p>
                    <p class="fw-bold mb-0 text-danger" id="deleteBudgetName"></p>
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
    function openEditBudgetModal(id, categoryId, amount, startDate, endDate) {
        document.getElementById('editBudgetId').value = id;
        document.getElementById('editCategorySelect').value = categoryId;
        document.getElementById('editBudgetAmount').value = amount;
        document.getElementById('editStartDate').value = startDate;
        document.getElementById('editEndDate').value = endDate;
        new bootstrap.Modal(document.getElementById('editBudgetModal')).show();
    }

    function openDeleteBudgetModal(id, categoryName) {
        document.getElementById('deleteBudgetId').value = id;
        document.getElementById('deleteBudgetName').textContent = categoryName;
        new bootstrap.Modal(document.getElementById('deleteBudgetModal')).show();
    }
</script>
