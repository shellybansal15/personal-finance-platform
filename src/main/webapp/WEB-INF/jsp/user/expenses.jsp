<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Expenses" />
<c:set var="pageHeader" value="Expense Tracking" />
<c:set var="activeMenu" value="expenses" />
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
                    <h4 class="fw-bold mb-1">Expense Log</h4>
                    <p class="text-muted small mb-0">Track, filter, and export all personal financial transactions.</p>
                </div>
                <div class="d-flex gap-2">
                    <button class="btn btn-primary d-flex align-items-center gap-2" data-bs-toggle="modal" data-bs-target="#addExpenseModal">
                        <i class="bi bi-plus-lg"></i> Add Expense
                    </button>
                    <a href="${pageContext.request.contextPath}/user/expenses?action=export" 
                       class="btn btn-outline-secondary d-flex align-items-center gap-2" title="Asynchronous CSV Export">
                        <i class="bi bi-download"></i> Export CSV
                    </a>
                </div>
            </div>

            <!-- Filter & Search Toolbar Card -->
            <div class="card mb-4">
                <div class="card-body py-3">
                    <form action="${pageContext.request.contextPath}/user/expenses" method="GET" class="row g-2 align-items-end">
                        <div class="col-12 col-md-3">
                            <label for="filterCategory" class="form-label small fw-semibold">Category</label>
                            <select class="form-select form-select-sm" id="filterCategory" name="categoryId">
                                <option value="ALL">All Categories</option>
                                <c:forEach var="c" items="${categories}">
                                    <option value="${c.id}" ${selectedCategoryId == c.id ? 'selected' : ''}>${c.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-6 col-md-3">
                            <label for="startDate" class="form-label small fw-semibold">Start Date</label>
                            <input type="date" class="form-control form-control-sm" id="startDate" name="startDate" value="${startDate}">
                        </div>
                        <div class="col-6 col-md-3">
                            <label for="endDate" class="form-label small fw-semibold">End Date</label>
                            <input type="date" class="form-control form-control-sm" id="endDate" name="endDate" value="${endDate}">
                        </div>
                        <div class="col-12 col-md-3 d-flex gap-2">
                            <button type="submit" class="btn btn-sm btn-primary flex-grow-1">
                                <i class="bi bi-funnel me-1"></i> Filter
                            </button>
                            <a href="${pageContext.request.contextPath}/user/expenses" class="btn btn-sm btn-outline-secondary">
                                Reset
                            </a>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Expenses Data Table Card -->
            <div class="card">
                <div class="card-body p-0">
                    <c:choose>
                        <c:when test="${not empty expenses}">
                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th class="ps-3">Date</th>
                                            <th>Category</th>
                                            <th>Note</th>
                                            <th>Amount</th>
                                            <th class="text-end pe-3">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="exp" items="${expenses}">
                                            <tr>
                                                <td class="ps-3 small fw-medium">${exp.date}</td>
                                                <td>
                                                    <span class="badge bg-secondary-subtle text-secondary rounded-pill px-2 py-1">
                                                        ${exp.categoryName}
                                                    </span>
                                                </td>
                                                <td class="small" style="max-width: 250px;">
                                                    <c:choose>
                                                        <c:when test="${not empty exp.note}">
                                                            <span title="AES-256 decrypted note">
                                                                <i class="bi bi-shield-lock text-success me-1"></i>${exp.note}
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="text-muted fst-italic">None</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="fw-bold text-danger">
                                                    -$<fmt:formatNumber value="${exp.amount}" minFractionDigits="2" maxFractionDigits="2"/>
                                                </td>
                                                <td class="text-end pe-3">
                                                    <div class="btn-group btn-group-sm">
                                                        <button class="btn btn-outline-secondary" title="Edit Expense"
                                                                onclick="openEditExpenseModal(${exp.id}, ${exp.categoryId}, '${exp.amount}', '${exp.date}', '${exp.note != null ? exp.note.replace('\'', '\\\'') : ''}')">
                                                            <i class="bi bi-pencil"></i>
                                                        </button>
                                                        <button class="btn btn-outline-danger" title="Delete Expense"
                                                                onclick="openDeleteExpenseModal(${exp.id}, '${exp.amount}', '${exp.categoryName}')">
                                                            <i class="bi bi-trash"></i>
                                                        </button>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>

                            <!-- Pagination Controls -->
                            <div class="p-3 border-top d-flex flex-column flex-sm-row justify-content-between align-items-center gap-2">
                                <div class="text-muted small">
                                    Showing page <strong>${currentPage}</strong> of <strong>${totalPages}</strong> (${totalRecords} total entries)
                                </div>
                                <c:if test="${totalPages > 1}">
                                    <nav aria-label="Page navigation">
                                        <ul class="pagination pagination-sm mb-0">
                                            <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                                <a class="page-link" href="${pageContext.request.contextPath}/user/expenses?page=${currentPage - 1}&categoryId=${selectedCategoryId}&startDate=${startDate}&endDate=${endDate}">Previous</a>
                                            </li>
                                            <c:forEach var="i" begin="1" end="${totalPages}">
                                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                    <a class="page-link" href="${pageContext.request.contextPath}/user/expenses?page=${i}&categoryId=${selectedCategoryId}&startDate=${startDate}&endDate=${endDate}">${i}</a>
                                                </li>
                                            </c:forEach>
                                            <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                                <a class="page-link" href="${pageContext.request.contextPath}/user/expenses?page=${currentPage + 1}&categoryId=${selectedCategoryId}&startDate=${startDate}&endDate=${endDate}">Next</a>
                                            </li>
                                        </ul>
                                    </nav>
                                </c:if>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <i class="bi bi-receipt-cutoff empty-state-icon"></i>
                                <h5>No expenses found</h5>
                                <p class="text-muted small">No expenses match your current filters. Clear filters or add a new transaction.</p>
                                <button class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#addExpenseModal">
                                    <i class="bi bi-plus-lg me-1"></i> Add Expense
                                </button>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </main>
</div>

<!-- Add Expense Modal -->
<div class="modal fade" id="addExpenseModal" tabindex="-1" aria-labelledby="addExpenseModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold" id="addExpenseModalLabel">
                    <i class="bi bi-plus-circle text-success me-2"></i>Record New Expense
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/user/expenses" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="add">

                <div class="modal-body">
                    <div class="mb-3">
                        <label for="addModalAmount" class="form-label small fw-semibold">Amount ($) *</label>
                        <div class="input-group">
                            <span class="input-group-text">$</span>
                            <input type="number" step="0.01" min="0.01" class="form-control" id="addModalAmount" name="amount" placeholder="0.00" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="addModalCategory" class="form-label small fw-semibold">Category *</label>
                        <select class="form-select" id="addModalCategory" name="categoryId" required>
                            <option value="">-- Choose Category --</option>
                            <c:forEach var="c" items="${categories}">
                                <c:if test="${c.type == 'EXPENSE'}">
                                    <option value="${c.id}">${c.name}</option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label for="addModalDate" class="form-label small fw-semibold">Expense Date *</label>
                        <input type="date" class="form-control" id="addModalDate" name="date" required value="<%= java.time.LocalDate.now() %>">
                    </div>

                    <div class="mb-3">
                        <label for="addModalNote" class="form-label small fw-semibold">Private Note (AES-256 Encrypted)</label>
                        <textarea class="form-control" id="addModalNote" name="note" rows="2" placeholder="e.g. Dinner, Groceries"></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Expense</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Edit Expense Modal -->
<div class="modal fade" id="editExpenseModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square text-primary me-2"></i>Edit Expense</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/user/expenses" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" id="editExpenseId" name="expenseId">

                <div class="modal-body">
                    <div class="mb-3">
                        <label for="editAmount" class="form-label small fw-semibold">Amount ($) *</label>
                        <div class="input-group">
                            <span class="input-group-text">$</span>
                            <input type="number" step="0.01" min="0.01" class="form-control" id="editAmount" name="amount" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="editCategoryId" class="form-label small fw-semibold">Category *</label>
                        <select class="form-select" id="editCategoryId" name="categoryId" required>
                            <c:forEach var="c" items="${categories}">
                                <c:if test="${c.type == 'EXPENSE'}">
                                    <option value="${c.id}">${c.name}</option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label for="editDate" class="form-label small fw-semibold">Expense Date *</label>
                        <input type="date" class="form-control" id="editDate" name="date" required>
                    </div>

                    <div class="mb-3">
                        <label for="editNote" class="form-label small fw-semibold">Private Note</label>
                        <textarea class="form-control" id="editNote" name="note" rows="2"></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Expense</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteExpenseModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold text-danger"><i class="bi bi-trash me-2"></i>Delete Expense</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/user/expenses" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" id="deleteExpenseId" name="expenseId">

                <div class="modal-body">
                    <p class="small mb-1">Are you sure you want to delete this expense record?</p>
                    <p class="fw-bold mb-0 text-danger" id="deleteExpenseDetails"></p>
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
    function openEditExpenseModal(id, categoryId, amount, date, note) {
        document.getElementById('editExpenseId').value = id;
        document.getElementById('editCategoryId').value = categoryId;
        document.getElementById('editAmount').value = amount;
        document.getElementById('editDate').value = date;
        document.getElementById('editNote').value = note || '';
        new bootstrap.Modal(document.getElementById('editExpenseModal')).show();
    }

    function openDeleteExpenseModal(id, amount, categoryName) {
        document.getElementById('deleteExpenseId').value = id;
        document.getElementById('deleteExpenseDetails').textContent = '$' + amount + ' (' + categoryName + ')';
        new bootstrap.Modal(document.getElementById('deleteExpenseModal')).show();
    }
</script>
