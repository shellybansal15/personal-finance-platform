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
                Track expenses in ₹, manage budgets, and hit goals — or open the Admin panel for users, security, and settings.
            </p>

            <div class="row g-3 mt-2">
                <div class="col-6">
                    <div class="p-3 rounded-3" style="background: rgba(255, 255, 255, 0.08); backdrop-filter: blur(8px);">
                        <i class="bi bi-person-fill text-success fs-4 mb-2 d-block"></i>
                        <h6 class="text-white mb-1">User Path</h6>
                        <small class="text-white-50">→ Financial dashboard</small>
                    </div>
                </div>
                <div class="col-6">
                    <div class="p-3 rounded-3" style="background: rgba(255, 255, 255, 0.08); backdrop-filter: blur(8px);">
                        <i class="bi bi-shield-lock-fill text-success fs-4 mb-2 d-block"></i>
                        <h6 class="text-white mb-1">Admin Path</h6>
                        <small class="text-white-50">→ Admin control panel</small>
                    </div>
                </div>
            </div>

            <p class="text-white-50 small mt-4 mb-0">
                <a href="${pageContext.request.contextPath}/" class="text-white text-decoration-none">
                    <i class="bi bi-arrow-left me-1"></i> Back to landing
                </a>
            </p>
        </div>
    </div>

    <!-- Right Form Panel -->
    <div class="auth-form-side position-relative">
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
                <p class="text-muted small">Choose your persona, then sign in. We route you to the right workspace.</p>
            </div>

            <%@ include file="/WEB-INF/jsp/fragments/alerts.jsp" %>

            <form action="${pageContext.request.contextPath}/login" method="POST" autocomplete="on" id="loginForm" data-loading-form>
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <!-- Persona / role selector -->
                <div class="mb-4">
                    <label class="form-label small fw-semibold d-block">Login as</label>
                    <div class="persona-selector" role="radiogroup" aria-label="Login persona">
                        <label class="persona-card ${sessionScope.loginAs != 'ADMIN' ? 'is-selected' : ''}">
                            <input type="radio" name="loginAs" value="USER" required
                                   ${sessionScope.loginAs != 'ADMIN' ? 'checked' : ''}>
                            <span class="persona-icon"><i class="bi bi-person-fill"></i></span>
                            <span class="persona-title">User</span>
                            <span class="persona-desc">Financial dashboard, expenses &amp; budgets</span>
                        </label>
                        <label class="persona-card ${sessionScope.loginAs == 'ADMIN' ? 'is-selected' : ''}">
                            <input type="radio" name="loginAs" value="ADMIN" required
                                   ${sessionScope.loginAs == 'ADMIN' ? 'checked' : ''}>
                            <span class="persona-icon admin"><i class="bi bi-shield-lock-fill"></i></span>
                            <span class="persona-title">Administrator</span>
                            <span class="persona-desc">Users, security logs &amp; system settings</span>
                        </label>
                    </div>
                    <% session.removeAttribute("loginAs"); %>
                </div>

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

                <div class="mb-4">
                    <label for="password" class="form-label small fw-semibold">Password</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-key"></i></span>
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder="••••••••" required>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold mb-3 btn-auth-submit" id="loginSubmitBtn">
                    <span class="btn-label">
                        <i class="bi bi-box-arrow-in-right me-1"></i> Sign In
                    </span>
                    <span class="btn-loading-label" aria-hidden="true">
                        <span class="spinner-border spinner-border-sm me-2" role="status"></span>
                        Signing in…
                    </span>
                </button>
                <p class="text-muted small text-center mb-0 auth-loading-hint d-none" id="loginLoadingHint">
                    Connecting to the server — first load after idle may take a moment.
                </p>
            </form>

            <div class="text-center mt-3">
                <p class="text-muted small mb-0">
                    Don't have an account yet?
                    <a href="${pageContext.request.contextPath}/register" class="text-success text-decoration-none fw-semibold">Create an Account</a>
                </p>
            </div>

            <div class="mt-4 p-3 rounded-3 bg-light border small">
                <div class="d-flex align-items-center justify-content-between mb-2">
                    <span class="fw-bold text-secondary"><i class="bi bi-info-circle me-1"></i> Demo Credentials:</span>
                    <span class="badge bg-secondary-subtle text-secondary">College Evaluation</span>
                </div>
                <div class="d-flex justify-content-between align-items-center py-1 border-bottom">
                    <span><strong>Admin:</strong> admin@finance.com</span>
                    <button type="button" class="btn btn-sm btn-outline-success py-0 px-2" style="font-size: 0.75rem;"
                            onclick="fillDemo('admin@finance.com','Admin@123','ADMIN')">Fill</button>
                </div>
                <div class="d-flex justify-content-between align-items-center pt-1">
                    <span><strong>User:</strong> user@finance.com</span>
                    <button type="button" class="btn btn-sm btn-outline-success py-0 px-2" style="font-size: 0.75rem;"
                            onclick="fillDemo('user@finance.com','User@123','USER')">Fill</button>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
(function () {
    function syncPersonaCards() {
        document.querySelectorAll('.persona-card').forEach(function (card) {
            var input = card.querySelector('input[type="radio"]');
            card.classList.toggle('is-selected', input && input.checked);
        });
    }

    document.querySelectorAll('.persona-card input[type="radio"]').forEach(function (radio) {
        radio.addEventListener('change', syncPersonaCards);
    });

    window.fillDemo = function (email, password, role) {
        document.getElementById('email').value = email;
        document.getElementById('password').value = password;
        var radio = document.querySelector('input[name="loginAs"][value="' + role + '"]');
        if (radio) {
            radio.checked = true;
            syncPersonaCards();
        }
    };

    var loginForm = document.getElementById('loginForm');
    var loginBtn = document.getElementById('loginSubmitBtn');
    var loginHint = document.getElementById('loginLoadingHint');
    if (loginForm && loginBtn) {
        loginForm.addEventListener('submit', function () {
            if (loginBtn.disabled) {
                return;
            }
            loginBtn.disabled = true;
            loginBtn.classList.add('is-loading');
            loginBtn.setAttribute('aria-busy', 'true');
            if (loginHint) {
                loginHint.classList.remove('d-none');
            }
        });
    }
})();
</script>

<%@ include file="/WEB-INF/jsp/fragments/footer.jsp" %>
