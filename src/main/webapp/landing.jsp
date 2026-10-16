<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="csrf-token" content="${sessionScope.csrfToken}">
    <title>FinanceFlow | Smart Finance in Motion</title>
    <meta name="description" content="Scroll-driven personal finance platform with smart expense tracking, real-time INR (₹) localization, and automated insights. Built for Tomcat &amp; Maven.">

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/scrollytelling.css">
</head>
<body>

    <!-- Role-aware dashboard destination for logged-in visitors -->
    <c:set var="dashHref" value="${pageContext.request.contextPath}/user/dashboard" />
    <c:set var="dashLabel" value="My Dashboard" />
    <c:set var="dashIcon" value="bi-wallet2" />
    <c:if test="${sessionScope.currentUser != null && sessionScope.userRole eq 'ADMIN'}">
        <c:set var="dashHref" value="${pageContext.request.contextPath}/admin/dashboard" />
        <c:set var="dashLabel" value="Admin Center" />
        <c:set var="dashIcon" value="bi-speedometer2" />
    </c:if>

    <!-- Fixed full-screen scroll-synced background video -->
    <div class="video-canvas-container" aria-hidden="true">
        <video
            id="hourglassVideo"
            class="bg-scroll-video"
            preload="auto"
            muted
            playsinline
            webkit-playsinline
            tabindex="-1">
            <source src="${pageContext.request.contextPath}/assets/videos/46185.mp4" type="video/mp4">
            Your browser does not support HTML5 video playback.
        </video>
    </div>

    <div class="video-edge-blend" aria-hidden="true"></div>
    <div class="ambient-glow-layer" aria-hidden="true"></div>

    <div id="scrollProgressBar" class="scroll-progress-line" role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-label="Page scroll progress"></div>

    <!-- Flash after logout / session messages -->
    <c:if test="${not empty sessionScope.flashSuccess || not empty sessionScope.flashError || not empty sessionScope.flashWarning}">
        <div class="landing-flash" id="landingFlash" role="status">
            <c:if test="${not empty sessionScope.flashSuccess}">
                <span class="landing-flash-ok"><i class="bi bi-check-circle-fill"></i> ${sessionScope.flashSuccess}</span>
                <c:remove var="flashSuccess" scope="session" />
            </c:if>
            <c:if test="${not empty sessionScope.flashError}">
                <span class="landing-flash-err"><i class="bi bi-exclamation-triangle-fill"></i> ${sessionScope.flashError}</span>
                <c:remove var="flashError" scope="session" />
            </c:if>
            <c:if test="${not empty sessionScope.flashWarning}">
                <span class="landing-flash-warn"><i class="bi bi-info-circle-fill"></i> ${sessionScope.flashWarning}</span>
                <c:remove var="flashWarning" scope="session" />
            </c:if>
            <button type="button" class="landing-flash-close" id="landingFlashClose" aria-label="Dismiss">&times;</button>
        </div>
    </c:if>

    <!-- Sticky navigation -->
    <header class="top-nav">
        <a href="#hero" class="brand-wrapper">
            <div class="brand-icon-box">
                <i class="bi bi-hourglass-split"></i>
            </div>
            <span class="brand-name">FinanceFlow</span>
        </a>

        <ul class="nav-links" id="primaryNav">
            <li><a href="#hero" class="nav-item-link">Overview</a></li>
            <li><a href="#expenses" class="nav-item-link">Expense Tracking</a></li>
            <li><a href="#inr" class="nav-item-link">INR Locale</a></li>
            <li><a href="#insights" class="nav-item-link">Insights</a></li>
        </ul>

        <div class="nav-actions">
            <c:choose>
                <c:when test="${sessionScope.currentUser != null}">
                    <a href="${dashHref}" class="btn-nav-primary">
                        <i class="bi ${dashIcon}"></i>
                        <span>${dashLabel}</span>
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" class="btn-nav-ghost js-nav-loading" data-loading-text="Opening…">Sign In</a>
                    <a href="${pageContext.request.contextPath}/register" class="btn-nav-primary js-nav-loading" data-loading-text="Opening…">
                        <span>Get Started</span>
                        <i class="bi bi-arrow-right"></i>
                    </a>
                </c:otherwise>
            </c:choose>
            <button type="button" class="nav-mobile-toggle" id="navMobileToggle" aria-label="Toggle menu" aria-expanded="false">
                <i class="bi bi-list"></i>
            </button>
        </div>
    </header>

    <nav class="mobile-nav-drawer" id="mobileNavDrawer" aria-hidden="true">
        <a href="#hero" class="mobile-nav-link">Overview</a>
        <a href="#expenses" class="mobile-nav-link">Expense Tracking</a>
        <a href="#inr" class="mobile-nav-link">INR Locale</a>
        <a href="#insights" class="mobile-nav-link">Insights</a>
        <c:choose>
            <c:when test="${sessionScope.currentUser != null}">
                <a href="${dashHref}" class="mobile-nav-link accent">${dashLabel}</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login" class="mobile-nav-link js-nav-loading" data-loading-text="Opening…">Sign In</a>
                <a href="${pageContext.request.contextPath}/register" class="mobile-nav-link accent js-nav-loading" data-loading-text="Opening…">Get Started</a>
            </c:otherwise>
        </c:choose>
    </nav>

    <main class="scrolly-container">

        <!-- HERO -->
        <section id="hero" class="scrolly-section section-center" data-reveal>
            <div class="badge-pill">
                <i class="bi bi-shield-check"></i>
                <span>Personal Finance &middot; INR Native &middot; Role-Aware</span>
            </div>

            <h1 class="hero-headline">
                FinanceFlow
            </h1>
            <p class="hero-subhead">
                Your money, in motion — tracked, localized, and explained as you scroll.
            </p>

            <p class="section-desc">
                Smart expense tracking, real-time <strong class="inr-highlight">₹ Indian Rupee</strong> formatting,
                and automated insights — all inside a secure Jakarta EE / Tomcat stack.
            </p>

            <div class="hero-cta-group">
                <a href="#expenses" class="btn-hero-primary">
                    <span>Explore Features</span>
                    <i class="bi bi-arrow-down-circle"></i>
                </a>
                <c:choose>
                    <c:when test="${sessionScope.currentUser != null}">
                        <a href="${dashHref}" class="btn-hero-secondary">
                            <i class="bi ${dashIcon}"></i>
                            <span>Go to ${dashLabel}</span>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" class="btn-hero-secondary js-nav-loading" data-loading-text="Connecting…">
                            <i class="bi bi-box-arrow-in-right"></i>
                            <span>Sign In</span>
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="scroll-indicator-wrap">
                <div class="mouse-scroll-icon">
                    <div class="mouse-wheel-dot"></div>
                </div>
                <span>Scroll to unlock the story</span>
            </div>
        </section>

        <!-- FEATURE 01: Smart Expense Tracking -->
        <section id="expenses" class="scrolly-section" data-reveal>
            <div class="section-split">
                <div>
                    <div class="badge-pill">
                        <i class="bi bi-receipt-cutoff"></i>
                        <span>Feature 01 &bull; Smart Expense Tracking</span>
                    </div>
                    <h2 class="section-title">
                        Every rupee categorized before it disappears.
                    </h2>
                    <p class="section-desc">
                        Log spending by category and payment mode. Budgets flash amber at 80% and red at 100%
                        so lifestyle creep never catches you off guard.
                    </p>
                    <ul class="feature-checklist">
                        <li><i class="bi bi-check2-circle"></i> Categories, notes &amp; AES-256 encrypted memos</li>
                        <li><i class="bi bi-check2-circle"></i> Live budget guardrails on your dashboard</li>
                        <li><i class="bi bi-check2-circle"></i> Goals that track progress toward targets</li>
                    </ul>
                </div>

                <div class="glass-card" data-reveal-delay="1">
                    <div class="glass-card-header">
                        <h4>Today&rsquo;s Ledger</h4>
                        <span class="live-pill">● LIVE</span>
                    </div>

                    <div class="mockup-item">
                        <div class="mockup-item-left">
                            <div class="mockup-icon emerald"><i class="bi bi-cup-hot-fill"></i></div>
                            <div>
                                <div class="mockup-title">Café &amp; Snacks</div>
                                <div class="mockup-meta">UPI &bull; Food</div>
                            </div>
                        </div>
                        <div class="mockup-amount expense">−₹285.00</div>
                    </div>

                    <div class="mockup-item">
                        <div class="mockup-item-left">
                            <div class="mockup-icon gold"><i class="bi bi-train-front-fill"></i></div>
                            <div>
                                <div class="mockup-title">Metro Commute</div>
                                <div class="mockup-meta">Card &bull; Transport</div>
                            </div>
                        </div>
                        <div class="mockup-amount expense">−₹60.00</div>
                    </div>

                    <div class="mockup-item">
                        <div class="mockup-item-left">
                            <div class="mockup-icon purple"><i class="bi bi-briefcase-fill"></i></div>
                            <div>
                                <div class="mockup-title">Freelance Payout</div>
                                <div class="mockup-meta">NEFT &bull; Income</div>
                            </div>
                        </div>
                        <div class="mockup-amount income">+₹12,500.00</div>
                    </div>

                    <div class="budget-mini">
                        <div class="budget-mini-row">
                            <span>Food budget</span>
                            <span class="warn-text">₹2,840 / ₹3,500 (81%)</span>
                        </div>
                        <div class="budget-bar-track">
                            <div class="budget-bar-fill warning" style="width: 81%;"></div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- FEATURE 02: Real-time INR Localization -->
        <section id="inr" class="scrolly-section" data-reveal>
            <div class="section-split">
                <div class="glass-card gold-hover" data-reveal-delay="1">
                    <div class="glass-card-header">
                        <h4>CurrencyUtil &middot; en-IN</h4>
                        <span class="badge-pill gold" style="margin: 0; padding: 4px 10px; font-size: 0.72rem;">₹ NATIVE</span>
                    </div>

                    <div class="inr-demo-grid">
                        <div class="inr-demo-row">
                            <span class="inr-label">Salary credit</span>
                            <span class="inr-value income" data-inr-count="85000">₹0</span>
                        </div>
                        <div class="inr-demo-row">
                            <span class="inr-label">Monthly spend</span>
                            <span class="inr-value expense" data-inr-count="42850">₹0</span>
                        </div>
                        <div class="inr-demo-row highlight">
                            <span class="inr-label">Net surplus</span>
                            <span class="inr-value income" data-inr-count="42150">₹0</span>
                        </div>
                    </div>

                    <p class="inr-footnote">
                        Indian digit grouping (last 3, then pairs of 2) — e.g. <strong>₹1,25,000.50</strong> —
                        powered by server-side <code>CurrencyUtil</code>.
                    </p>
                </div>

                <div>
                    <div class="badge-pill gold">
                        <i class="bi bi-currency-rupee"></i>
                        <span>Feature 02 &bull; Real-Time INR Localization</span>
                    </div>
                    <h2 class="section-title">
                        Built for India. Formatted for clarity.
                    </h2>
                    <p class="section-desc">
                        Every dashboard total, budget bar, and report line uses the ₹ symbol with
                        authentic Indian number grouping — no USD leftovers, no locale surprises.
                    </p>
                    <ul class="feature-checklist">
                        <li><i class="bi bi-check2-circle"></i> Thread-safe <code>en-IN</code> currency formatting</li>
                        <li><i class="bi bi-check2-circle"></i> Consistent ₹ across JSP views &amp; Chart.js</li>
                        <li><i class="bi bi-check2-circle"></i> Scroll counters that animate to live totals</li>
                    </ul>
                </div>
            </div>
        </section>

        <!-- FEATURE 03: Automated Insights -->
        <section id="insights" class="scrolly-section section-center" data-reveal>
            <div class="badge-pill">
                <i class="bi bi-lightbulb-fill"></i>
                <span>Feature 03 &bull; Automated Insights</span>
            </div>

            <h2 class="section-title">
                Quiet intelligence.<br>
                <span class="gradient-text-emerald">Loud clarity.</span>
            </h2>

            <p class="section-desc">
                Reports and trend charts surface where money goes — so you act on patterns,
                not hunches. Admins get a separate security and user-ops panel.
            </p>

            <div class="features-grid insights-grid">
                <div class="glass-card" data-reveal-delay="1">
                    <div class="feature-icon-wrapper">
                        <i class="bi bi-pie-chart-fill"></i>
                    </div>
                    <h3 class="insight-card-title">Spending Mix</h3>
                    <p class="insight-card-desc">
                        Category breakdowns highlight top drains — food, rent, subscriptions — month over month.
                    </p>
                    <div class="insight-stat">42% food &amp; dining</div>
                </div>

                <div class="glass-card gold-hover" data-reveal-delay="2">
                    <div class="feature-icon-wrapper gold">
                        <i class="bi bi-graph-up-arrow"></i>
                    </div>
                    <h3 class="insight-card-title">Cashflow Pulse</h3>
                    <p class="insight-card-desc">
                        Income vs expense velocity on Chart.js — spot weeks where surplus shrinks early.
                    </p>
                    <div class="insight-stat gold">+₹42,150 surplus</div>
                </div>

                <div class="glass-card" data-reveal-delay="3">
                    <div class="feature-icon-wrapper sky">
                        <i class="bi bi-shield-lock-fill"></i>
                    </div>
                    <h3 class="insight-card-title">Role-Based Paths</h3>
                    <p class="insight-card-desc">
                        Users land on the financial dashboard. Admins open the advanced Admin panel — no wrong doors.
                    </p>
                    <div class="insight-stat sky">USER → /user &middot; ADMIN → /admin</div>
                </div>
            </div>
        </section>

        <!-- CTA -->
        <section id="cta" class="scrolly-section section-center" style="min-height: 90vh;" data-reveal>
            <div class="grand-cta-card">
                <div class="badge-pill gold" style="margin-bottom: 16px;">
                    <i class="bi bi-person-badge"></i>
                    <span>Persona login &middot; User or Administrator</span>
                </div>

                <h2 class="cta-headline">
                    Ready to take command of your<br>
                    <span class="gradient-text-emerald">financial horizon?</span>
                </h2>

                <p class="cta-desc">
                    Sign in as a User for your wallet dashboard, or as an Administrator for
                    users, security logs, and system settings.
                </p>

                <div class="hero-cta-group">
                    <c:choose>
                        <c:when test="${sessionScope.currentUser != null}">
                            <a href="${dashHref}" class="btn-hero-primary btn-lg">
                                <i class="bi ${dashIcon}"></i>
                                <span>Open ${dashLabel}</span>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/register" class="btn-hero-primary btn-lg js-nav-loading" data-loading-text="Connecting…">
                                <i class="bi bi-person-plus-fill"></i>
                                <span>Create Free Account</span>
                            </a>
                            <a href="${pageContext.request.contextPath}/login" class="btn-hero-secondary btn-lg js-nav-loading" data-loading-text="Connecting…">
                                <i class="bi bi-box-arrow-in-right"></i>
                                <span>Sign In</span>
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="cta-trust-row">
                    <span><i class="bi bi-check2 text-success"></i> AES-256 Encrypted</span>
                    <span><i class="bi bi-check2 text-success"></i> ₹ INR Localized</span>
                    <span><i class="bi bi-check2 text-success"></i> Jakarta EE &amp; Tomcat 10.1</span>
                </div>
            </div>
        </section>

    </main>

    <aside class="timeline-hud" aria-label="Scroll timeline">
        <span class="hud-label">TIMELINE</span>
        <span id="hudPercent" class="hud-progress-pill">00%</span>
        <span class="hud-sep">|</span>
        <span id="hudTime" class="hud-timestamp">0.00s / 4.09s</span>
        <span class="hud-sep">|</span>
        <button type="button" id="hudRewindBtn" class="hud-btn-rewind" title="Back to top" aria-label="Scroll to top">
            <i class="bi bi-chevron-up"></i>
        </button>
    </aside>

    <footer class="site-footer">
        <div class="footer-inner">
            <div class="footer-brand">
                <i class="bi bi-hourglass-split text-success"></i>
                <span class="footer-name">FinanceFlow</span>
                <span class="footer-tag">Personal Finance Platform</span>
            </div>
            <div class="footer-meta">
                <span class="system-online">● System Online</span>
                <span>&copy; 2026 FinanceFlow. All rights reserved.</span>
            </div>
        </div>
    </footer>

    <script src="${pageContext.request.contextPath}/js/scrollytelling.js"></script>
</body>
</html>
