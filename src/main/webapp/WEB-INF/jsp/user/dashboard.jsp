<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Dashboard" />
<c:set var="pageHeader" value="Financial Overview" />
<c:set var="activeMenu" value="dashboard" />
<%@ include file="/WEB-INF/jsp/fragments/header.jsp" %>

<div class="app-wrapper">
    <!-- Sidebar -->
    <%@ include file="/WEB-INF/jsp/fragments/sidebar.jsp" %>

    <!-- Main Content Area -->
    <main class="app-main">
        <!-- Top Navbar -->
        <%@ include file="/WEB-INF/jsp/fragments/navbar.jsp" %>

        <div class="p-3 p-md-4">
            <!-- Flash Notification Alerts -->
            <%@ include file="/WEB-INF/jsp/fragments/alerts.jsp" %>

            <!-- Welcome Greeting Banner -->
            <div class="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center gap-2 mb-4">
                <div>
                    <h4 class="fw-bold mb-1">Hello, ${sessionScope.userName}! 👋</h4>
                    <p class="text-muted small mb-0">Here is your financial snapshot and spending activity.</p>
                </div>
                <div class="d-flex gap-2">
                    <button class="btn btn-outline-primary btn-sm d-flex align-items-center gap-2" 
                            data-bs-toggle="modal" data-bs-target="#addExpenseModal">
                        <i class="bi bi-plus-circle"></i> Add Expense
                    </button>
                    <a href="${pageContext.request.contextPath}/user/expenses?action=export" 
                       class="btn btn-outline-secondary btn-sm d-flex align-items-center gap-2" title="Export CSV Report">
                        <i class="bi bi-download"></i> Export CSV
                    </a>
                </div>
            </div>

            <!-- 1. Four Fintech Stat Cards -->
            <div class="row g-3 mb-4">
                <div class="col-12 col-sm-6 col-xl-3">
                    <div class="card card-hover h-100">
                        <div class="card-body d-flex align-items-center justify-content-between">
                            <div>
                                <span class="text-muted small fw-semibold text-uppercase d-block mb-1">Spent This Month</span>
                                <h3 class="fw-bold mb-0 text-danger">$<fmt:formatNumber value="${monthSpent}" minFractionDigits="2" maxFractionDigits="2"/></h3>
                            </div>
                            <div class="stat-card-icon stat-icon-red">
                                <i class="bi bi-arrow-up-right"></i>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-12 col-sm-6 col-xl-3">
                    <div class="card card-hover h-100">
                        <div class="card-body d-flex align-items-center justify-content-between">
                            <div>
                                <span class="text-muted small fw-semibold text-uppercase d-block mb-1">Active Budgets</span>
                                <h3 class="fw-bold mb-0">$<fmt:formatNumber value="${activeBudgetTotal}" minFractionDigits="2" maxFractionDigits="2"/></h3>
                            </div>
                            <div class="stat-card-icon stat-icon-blue">
                                <i class="bi bi-pie-chart-fill"></i>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-12 col-sm-6 col-xl-3">
                    <div class="card card-hover h-100">
                        <div class="card-body d-flex align-items-center justify-content-between">
                            <div>
                                <span class="text-muted small fw-semibold text-uppercase d-block mb-1">Goals Saved</span>
                                <h3 class="fw-bold mb-0 text-success">$<fmt:formatNumber value="${totalSaved}" minFractionDigits="2" maxFractionDigits="2"/></h3>
                            </div>
                            <div class="stat-card-icon stat-icon-green">
                                <i class="bi bi-bullseye"></i>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-12 col-sm-6 col-xl-3">
                    <div class="card card-hover h-100">
                        <div class="card-body d-flex align-items-center justify-content-between">
                            <div>
                                <span class="text-muted small fw-semibold text-uppercase d-block mb-1">All-Time Expenses</span>
                                <h3 class="fw-bold mb-0">$<fmt:formatNumber value="${allTimeSpent}" minFractionDigits="2" maxFractionDigits="2"/></h3>
                            </div>
                            <div class="stat-card-icon stat-icon-amber">
                                <i class="bi bi-cash-stack"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 2. Visual Charts Row (Chart.js) -->
            <div class="row g-3 mb-4">
                <!-- Doughnut Chart: Spending By Category -->
                <div class="col-12 col-lg-5">
                    <div class="card h-100">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <span class="fw-semibold">Spending by Category</span>
                            <i class="bi bi-pie-chart text-muted"></i>
                        </div>
                        <div class="card-body position-relative" style="min-height: 280px;">
                            <c:choose>
                                <c:when test="${not empty categorySpending}">
                                    <div style="height: 250px;">
                                        <canvas id="categoryDoughnutChart"></canvas>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="empty-state">
                                        <i class="bi bi-pie-chart empty-state-icon"></i>
                                        <p class="text-muted small mb-0">No categorized expenses recorded yet.</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <!-- Line Chart: 6-Month Expenditure Trend -->
                <div class="col-12 col-lg-7">
                    <div class="card h-100">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <span class="fw-semibold">Monthly Spending Trend (Last 6 Months)</span>
                            <i class="bi bi-graph-up text-muted"></i>
                        </div>
                        <div class="card-body position-relative" style="min-height: 280px;">
                            <c:choose>
                                <c:when test="${not empty monthlyTrend}">
                                    <div style="height: 250px;">
                                        <canvas id="monthlyTrendLineChart"></canvas>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="empty-state">
                                        <i class="bi bi-graph-up empty-state-icon"></i>
                                        <p class="text-muted small mb-0">Not enough history to generate trends.</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 3. Bottom Row: Recent Transactions & Trackables (Budgets & Goals) -->
            <div class="row g-3">
                <!-- Left: Recent Transactions Table -->
                <div class="col-12 col-xl-7">
                    <div class="card h-100">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <span class="fw-semibold">Recent Transactions</span>
                            <a href="${pageContext.request.contextPath}/user/expenses" class="text-success small fw-semibold text-decoration-none">
                                View All <i class="bi bi-arrow-right"></i>
                            </a>
                        </div>
                        <div class="card-body p-0">
                            <c:choose>
                                <c:when test="${not empty recentExpenses}">
                                    <div class="table-responsive">
                                        <table class="table table-hover align-middle mb-0">
                                            <thead class="table-light">
                                                <tr>
                                                    <th class="ps-3">Date</th>
                                                    <th>Category</th>
                                                    <th>Note</th>
                                                    <th class="text-end pe-3">Amount</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="exp" items="${recentExpenses}">
                                                    <tr>
                                                        <td class="ps-3 small text-muted">${exp.date}</td>
                                                        <td>
                                                            <span class="badge bg-secondary-subtle text-secondary rounded-pill px-2 py-1">
                                                                ${exp.categoryName}
                                                            </span>
                                                        </td>
                                                        <td class="small text-truncate" style="max-width: 220px;" title="${exp.note}">
                                                            ${not empty exp.note ? exp.note : '<em class="text-muted">No note</em>'}
                                                        </td>
                                                        <td class="text-end pe-3 fw-bold text-danger">
                                                            -$<fmt:formatNumber value="${exp.amount}" minFractionDigits="2" maxFractionDigits="2"/>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="empty-state">
                                        <i class="bi bi-receipt empty-state-icon"></i>
                                        <h6>No expenses logged yet!</h6>
                                        <p class="text-muted small">Click the + button below or Add Expense to start tracking.</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <!-- Right: Active Budgets (Trackable progress bars) & Goals -->
                <div class="col-12 col-xl-5">
                    <div class="card h-100">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <span class="fw-semibold">Budget Limits [Trackable]</span>
                            <a href="${pageContext.request.contextPath}/user/budgets" class="text-success small fw-semibold text-decoration-none">
                                Manage <i class="bi bi-arrow-right"></i>
                            </a>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty budgets}">
                                    <div class="d-flex flex-column gap-3">
                                        <c:forEach var="b" items="${budgets}">
                                            <c:set var="pct" value="${b.progressPercentage}" />
                                            <div>
                                                <div class="d-flex justify-content-between align-items-center mb-1">
                                                    <span class="fw-semibold small">${b.categoryName}</span>
                                                    <span class="small ${pct >= 100 ? 'text-danger fw-bold' : (pct >= 80 ? 'text-warning fw-bold' : 'text-muted')}">
                                                        $<fmt:formatNumber value="${b.spentAmount}" minFractionDigits="2"/> / $<fmt:formatNumber value="${b.amount}" minFractionDigits="2"/>
                                                        (<fmt:formatNumber value="${pct}" maxFractionDigits="0"/>%)
                                                    </span>
                                                </div>
                                                <!-- Dynamic progress bar: amber at 80%, red at 100% -->
                                                <div class="progress">
                                                    <div class="progress-bar ${pct >= 100 ? 'progress-bar-red' : (pct >= 80 ? 'progress-bar-amber' : 'progress-bar-green')}" 
                                                         role="progressbar" 
                                                         style="width: ${pct > 100 ? 100 : pct}%;" 
                                                         aria-valuenow="${pct}" aria-valuemin="0" aria-valuemax="100"></div>
                                                </div>
                                                <c:if test="${pct >= 100}">
                                                    <div class="text-danger small mt-1"><i class="bi bi-exclamation-triangle-fill"></i> Budget limit exceeded!</div>
                                                </c:if>
                                                <c:if test="${pct >= 80 && pct < 100}">
                                                    <div class="text-warning small mt-1"><i class="bi bi-exclamation-circle-fill"></i> Warning: Approaching 80% limit</div>
                                                </c:if>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="empty-state py-4">
                                        <i class="bi bi-pie-chart empty-state-icon" style="font-size: 2.5rem;"></i>
                                        <p class="text-muted small mb-0">No active category budgets set.</p>
                                        <a href="${pageContext.request.contextPath}/user/budgets" class="btn btn-sm btn-outline-primary mt-2">
                                            Create Budget
                                        </a>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<!-- Floating Action Button (FAB) for Add Expense -->
<button class="fab-btn" data-bs-toggle="modal" data-bs-target="#addExpenseModal" title="Quick Add Expense" aria-label="Add Expense">
    <i class="bi bi-plus-lg"></i>
</button>

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
                        <label for="modalAmount" class="form-label small fw-semibold">Amount ($) *</label>
                        <div class="input-group">
                            <span class="input-group-text">$</span>
                            <input type="number" step="0.01" min="0.01" class="form-control" id="modalAmount" 
                                   name="amount" placeholder="0.00" required autofocus>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="modalCategory" class="form-label small fw-semibold">Category *</label>
                        <select class="form-select" id="modalCategory" name="categoryId" required>
                            <option value="">-- Choose Category --</option>
                            <c:forEach var="c" items="${categories}">
                                <c:if test="${c.type == 'EXPENSE'}">
                                    <option value="${c.id}">${c.name}</option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label for="modalDate" class="form-label small fw-semibold">Expense Date *</label>
                        <input type="date" class="form-control" id="modalDate" name="date" required 
                               value="<%= java.time.LocalDate.now() %>">
                    </div>

                    <div class="mb-3">
                        <label for="modalNote" class="form-label small fw-semibold">Private Note (AES-256 Encrypted)</label>
                        <textarea class="form-control" id="modalNote" name="note" rows="2" 
                                  placeholder="e.g. Dinner with teammates, Groceries"></textarea>
                        <div class="form-text small">Encrypted before saving to MySQL using AES-256.</div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-check-lg me-1"></i> Save Expense
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/fragments/footer.jsp" %>

<!-- Chart.js Data Injection -->
<script>
    document.addEventListener('DOMContentLoaded', function () {
        // Doughnut Data
        const catLabels = [];
        const catValues = [];
        <c:forEach var="entry" items="${categorySpending}">
            catLabels.push("${entry.key}");
            catValues.push(${entry.value});
        </c:forEach>

        if (catLabels.length > 0) {
            renderCategoryChart(catLabels, catValues);
        }

        // Line Trend Data
        const trendLabels = [];
        const trendValues = [];
        <c:forEach var="entry" items="${monthlyTrend}">
            trendLabels.push("${entry.key}");
            trendValues.push(${entry.value});
        </c:forEach>

        if (trendLabels.length > 0) {
            renderTrendChart(trendLabels, trendValues);
        }

        window.renderAllCharts = function() {
            if (catLabels.length > 0) renderCategoryChart(catLabels, catValues);
            if (trendLabels.length > 0) renderTrendChart(trendLabels, trendValues);
        };
    });
</script>
