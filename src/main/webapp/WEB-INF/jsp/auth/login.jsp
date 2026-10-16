<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Sign In" />
<%@ include file="/WEB-INF/jsp/fragments/header.jsp" %>

<div class="auth-split-wrapper">
    <!-- Left Hero Banner -->
    <div class="auth-hero-side">
        <div style="max-width: 480px;">
            <div class="d-flex align-items-center gap-3 mb-4">
                <div class="brand-icon" style="width: 44px; height: 44px; background: #10b981; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 1.4rem;">
                    <i class="bi bi-wallet2 text-white"></i>
                </div>
                <h3 class="fw-bold mb-0 text-white">FinanceFlow</h3>
            </div>
            <h1 class="display-6 fw-bold text-white mb-3">Master Your Money, Secure Your Future.</h1>
            <p class="text-white-50 lead mb-4">
                An intelligent personal finance platform for tracking expenses, managing monthly budgets, and hitting your long-term financial targets with military-grade AES security.
            </p>

            <div class="row g-3 mt-2">
                <div class="col-6">
                    <div class="p-3 rounded-3" style="background: rgba(255, 255, 255, 0.08); backdrop-filter: blur(8px);">
                        <i class="bi bi-shield-lock-fill text-success fs-4 mb-2 d-block"></i>
                        <h6 class="text-white mb-1">AES-256 Encrypted</h6>
                        <small class="text-white-50">Private expense notes encrypted at rest</small>
                    </div>
                </div>
                <div class="col-6">
                    <div class="p-3 rounded-3" style="background: rgba(255, 255, 255, 0.08); backdrop-filter: blur(8px);">
                        <i class="bi bi-graph-up-arrow text-success fs-4 mb-2 d-block"></i>
                        <h6 class="text-white mb-1">Live Analytics</h6>
                        <small class="text-white-50">Interactive Chart.js spending trends</small>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Right Form Panel -->
    <div class="auth-form-side position-relative">
        <!-- Floating Theme Switcher -->
        <button type="button" class="btn btn-sm btn-outline-secondary position-absolute top-0 end-0 m-4 rounded-circle" 
                id="themeToggleBtn" style="width: 38px; height: 38px;" title="Switch Theme">
            <i class="bi bi-moon-stars-fill text-success"></i>
        </button>

        <div class="auth-box">
            <div class="text-center mb-4">
                <div class="d-inline-flex align-items-center justify-content-center p-2 rounded-3 bg-success-subtle mb-3">
                    <i class="bi bi-lock-fill text-success fs-3"></i>
                </div>
                <h3 class="fw-bold">Welcome Back</h3>
                <p class="text-muted small">Enter your credentials to access your financial dashboard.</p>
            </div>

            <!-- Flash Notifications -->
            <%@ include file="/WEB-INF/jsp/fragments/alerts.jsp" %>

            <form action="${pageContext.request.contextPath}/login" method="POST" autocomplete="on">
                <!-- CSRF Token -->
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <div class="mb-3">
                    <label for="email" class="form-label small fw-semibold">Email Address</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                        <input type="email" class="form-control" id="email" name="email" 
                               value="${sessionScope.loginEmail != null ? sessionScope.loginEmail : ''}" 
                               placeholder="name@example.com" required autofocus>
                    </div>
                    <% session.removeAttribute("loginEmail"); %>
                </div>

                <div class="mb-3">
                    <label for="password" class="form-label small fw-semibold">Password</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-key"></i></span>
                        <input type="password" class="form-control" id="password" name="password" 
                               placeholder="••••••••" required>
                    </div>
                </div>

                <div class="mb-4">
                    <label for="loginAs" class="form-label small fw-semibold">Login as</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-person-badge"></i></span>
                        <select class="form-select" id="loginAs" name="loginAs" required>
                            <option value="USER" ${sessionScope.loginAs == 'ADMIN' ? '' : 'selected'}>User</option>
                            <option value="ADMIN" ${sessionScope.loginAs == 'ADMIN' ? 'selected' : ''}>Administrator</option>
                        </select>
                    </div>
                    <% session.removeAttribute("loginAs"); %>
                </div>

                <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold mb-3">
                    <i class="bi bi-box-arrow-in-right me-1"></i> Sign In
                </button>
            </form>

            <div class="text-center mt-3">
                <p class="text-muted small mb-0">
                    Don't have an account yet? 
                    <a href="${pageContext.request.contextPath}/register" class="text-success text-decoration-none fw-semibold">Create an Account</a>
                </p>
            </div>

            <!-- College Project Evaluator Quick-Fill Helper Box -->
            <div class="mt-4 p-3 rounded-3 bg-light border small">
                <div class="d-flex align-items-center justify-content-between mb-2">
                    <span class="fw-bold text-secondary"><i class="bi bi-info-circle me-1"></i> Demo Credentials:</span>
                    <span class="badge bg-secondary-subtle text-secondary">College Evaluation</span>
                </div>
                <div class="d-flex justify-content-between align-items-center py-1 border-bottom">
                    <span><strong>Admin:</strong> admin@finance.com</span>
                    <button class="btn btn-sm btn-outline-success py-0 px-2" style="font-size: 0.75rem;"
                            onclick="document.getElementById('email').value='admin@finance.com';document.getElementById('password').value='Admin@123';document.getElementById('loginAs').value='ADMIN';">Fill</button>
                </div>
                <div class="d-flex justify-content-between align-items-center pt-1">
                    <span><strong>User:</strong> user@finance.com</span>
                    <button class="btn btn-sm btn-outline-success py-0 px-2" style="font-size: 0.75rem;"
                            onclick="document.getElementById('email').value='user@finance.com';document.getElementById('password').value='User@123';document.getElementById('loginAs').value='USER';">Fill</button>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/fragments/footer.jsp" %>
