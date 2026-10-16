<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Create Account" />
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
            <h1 class="display-6 fw-bold text-white mb-3">Begin Your Financial Freedom Journey.</h1>
            <p class="text-white-50 lead mb-4">
                Track every penny, set category budgets, save towards dream goals, and gain crystal clarity over your financial health.
            </p>

            <ul class="list-unstyled text-white-50 d-flex flex-column gap-3 mt-4">
                <li class="d-flex align-items-center gap-2">
                    <i class="bi bi-check-circle-fill text-success fs-5"></i>
                    <span>Automatic budget limit warnings at 80% and 100% capacity</span>
                </li>
                <li class="d-flex align-items-center gap-2">
                    <i class="bi bi-check-circle-fill text-success fs-5"></i>
                    <span>Asynchronous CSV transaction exports via multithreaded workers</span>
                </li>
                <li class="d-flex align-items-center gap-2">
                    <i class="bi bi-check-circle-fill text-success fs-5"></i>
                    <span>Zero third-party trackers, 100% private and protected data</span>
                </li>
            </ul>
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
                    <i class="bi bi-person-plus-fill text-success fs-3"></i>
                </div>
                <h3 class="fw-bold">Create Account</h3>
                <p class="text-muted small">Sign up to take complete control of your finances.</p>
            </div>

            <!-- Flash Notifications -->
            <%@ include file="/WEB-INF/jsp/fragments/alerts.jsp" %>

            <form action="${pageContext.request.contextPath}/register" method="POST" autocomplete="on" id="registerForm" data-loading-form>
                <!-- CSRF Token -->
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <div class="mb-3">
                    <label for="name" class="form-label small fw-semibold">Full Name</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-person"></i></span>
                        <input type="text" class="form-control" id="name" name="name" 
                               value="${sessionScope.regName != null ? sessionScope.regName : ''}" 
                               placeholder="Alex Morgan" required autofocus>
                    </div>
                    <% session.removeAttribute("regName"); %>
                </div>

                <div class="mb-3">
                    <label for="email" class="form-label small fw-semibold">Email Address</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                        <input type="email" class="form-control" id="email" name="email" 
                               value="${sessionScope.regEmail != null ? sessionScope.regEmail : ''}" 
                               placeholder="name@example.com" required>
                    </div>
                    <% session.removeAttribute("regEmail"); %>
                </div>

                <div class="mb-3">
                    <div class="d-flex justify-content-between align-items-center">
                        <label for="regPassword" class="form-label small fw-semibold mb-0">Password</label>
                        <span id="strengthText" class="small text-muted"></span>
                    </div>
                    <div class="input-group mt-1">
                        <span class="input-group-text"><i class="bi bi-key"></i></span>
                        <input type="password" class="form-control" id="regPassword" name="password" 
                               placeholder="Min 6 characters" required minlength="6">
                    </div>
                    <!-- Password Strength Meter Bar -->
                    <div class="strength-meter-bar">
                        <div class="strength-fill" id="strengthFill"></div>
                    </div>
                </div>

                <div class="mb-4">
                    <div class="d-flex justify-content-between align-items-center">
                        <label for="regConfirmPassword" class="form-label small fw-semibold mb-0">Confirm Password</label>
                        <span id="matchStatus" class="small"></span>
                    </div>
                    <div class="input-group mt-1">
                        <span class="input-group-text"><i class="bi bi-shield-check"></i></span>
                        <input type="password" class="form-control" id="regConfirmPassword" name="confirmPassword" 
                               placeholder="Re-type your password" required minlength="6">
                    </div>
                </div>

                <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold mb-3 btn-auth-submit" id="registerSubmitBtn">
                    <span class="btn-label">
                        <i class="bi bi-person-check-fill me-1"></i> Register Account
                    </span>
                    <span class="btn-loading-label" aria-hidden="true">
                        <span class="spinner-border spinner-border-sm me-2" role="status"></span>
                        Creating account…
                    </span>
                </button>
                <p class="text-muted small text-center mb-0 auth-loading-hint d-none" id="registerLoadingHint">
                    Connecting to the server — first load after idle may take a moment.
                </p>
            </form>

            <div class="text-center mt-3">
                <p class="text-muted small mb-0">
                    Already have an account? 
                    <a href="${pageContext.request.contextPath}/login" class="text-success text-decoration-none fw-semibold">Sign In</a>
                </p>
            </div>
        </div>
    </div>
</div>

<script>
(function () {
    var form = document.getElementById('registerForm');
    var btn = document.getElementById('registerSubmitBtn');
    var hint = document.getElementById('registerLoadingHint');
    if (form && btn) {
        form.addEventListener('submit', function () {
            if (btn.disabled) {
                return;
            }
            btn.disabled = true;
            btn.classList.add('is-loading');
            btn.setAttribute('aria-busy', 'true');
            if (hint) {
                hint.classList.remove('d-none');
            }
        });
    }
})();
</script>

<%@ include file="/WEB-INF/jsp/fragments/footer.jsp" %>
