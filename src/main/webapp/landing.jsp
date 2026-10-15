<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="csrf-token" content="${sessionScope.csrfToken}">
    <title>FinanceFlow | Sovereign Wealth in Motion</title>
    <meta name="description" content="Ultra-smooth scroll-driven financial platform. Real-time cashflow intelligence, AES-256 encrypted ledger, and autonomous wealth management.">
    
    <!-- Google Fonts: Inter -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    
    <!-- Scrollytelling Stylesheet -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/scrollytelling.css">
</head>
<body>

    <!-- ====================================================================
         FIXED FULL-SCREEN BACKGROUND VIDEO LAYER
         Pitch-black background (#000000) with zero gray tones, borders, or seam lines.
         ==================================================================== -->
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

    <!-- Pitch-black vignette & edge-fade blending layer -->
    <div class="video-edge-blend" aria-hidden="true"></div>
    <div class="ambient-glow-layer" aria-hidden="true"></div>

    <!-- Top Scroll Progress Indicator Bar -->
    <div id="scrollProgressBar" class="scroll-progress-line" role="progressbar" aria-valuemin="0" aria-valuemax="100"></div>

    <!-- ====================================================================
         STICKY TOP NAVIGATION BAR
         ==================================================================== -->
    <header class="top-nav">
        <a href="#hero" class="brand-wrapper">
            <div class="brand-icon-box">
                <i class="bi bi-hourglass-split"></i>
            </div>
            <span class="brand-name">FinanceFlow</span>
        </a>

        <ul class="nav-links">
            <li><a href="#hero" class="nav-item-link">Overview</a></li>
            <li><a href="#inflow" class="nav-item-link">Inflow Capture</a></li>
            <li><a href="#guardrails" class="nav-item-link">The Protocol</a></li>
            <li><a href="#reservoir" class="nav-item-link">Accumulation</a></li>
        </ul>

        <div class="nav-actions">
            <c:choose>
                <c:when test="${sessionScope.currentUser != null}">
                    <c:choose>
                        <c:when test="${sessionScope.userRole eq 'ADMIN'}">
                            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn-nav-primary">
                                <i class="bi bi-speedometer2"></i>
                                <span>Admin Center</span>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/user/dashboard" class="btn-nav-primary">
                                <i class="bi bi-wallet2"></i>
                                <span>My Dashboard</span>
                            </a>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" class="btn-nav-ghost">Sign In</a>
                    <a href="${pageContext.request.contextPath}/register" class="btn-nav-primary">
                        <span>Get Started</span>
                        <i class="bi bi-arrow-right"></i>
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
    </header>

    <!-- ====================================================================
         SCROLLYTELLING FOREGROUND NARRATIVE
         ==================================================================== -->
    <main class="scrolly-container">

        <!-- ----------------------------------------------------------------
             HERO SECTION (0% - 15% Scroll: Upper Chamber Full / The Flow Begins)
             ---------------------------------------------------------------- -->
        <section id="hero" class="scrolly-section section-center">
            <div class="badge-pill">
                <i class="bi bi-shield-check"></i>
                <span>Institutional-Grade Financial Architecture</span>
            </div>

            <h1 class="hero-headline">
                Your Wealth in Motion.<br>
                <span class="gradient-text-emerald">Every Grain Accounted For.</span>
            </h1>

            <p class="section-desc">
                Time is finite. Capital is dynamic. As seconds slip through the hourglass, FinanceFlow synchronizes your daily transactions with long-term financial sovereignty through mathematical precision and AES-256 encryption.
            </p>

            <div class="hero-cta-group">
                <a href="#inflow" class="btn-hero-primary">
                    <span>Witness The Flow</span>
                    <i class="bi bi-arrow-down-circle"></i>
                </a>
                <c:choose>
                    <c:when test="${sessionScope.currentUser != null}">
                        <a href="${pageContext.request.contextPath}/user/dashboard" class="btn-hero-secondary">
                            <i class="bi bi-speedometer2"></i>
                            <span>Go to Dashboard</span>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" class="btn-hero-secondary">
                            <i class="bi bi-box-arrow-in-right"></i>
                            <span>Access Dashboard</span>
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Scroll Prompt Cue -->
            <div class="scroll-indicator-wrap">
                <div class="mouse-scroll-icon">
                    <div class="mouse-wheel-dot"></div>
                </div>
                <span>Scroll down to flow time & capital</span>
            </div>
        </section>

        <!-- ----------------------------------------------------------------
             PHASE 01 (20% - 40% Scroll: Upper Chamber Pouring / Inflow Capture)
             ---------------------------------------------------------------- -->
        <section id="inflow" class="scrolly-section">
            <div class="section-split">
                <!-- Text Description -->
                <div>
                    <div class="badge-pill">
                        <i class="bi bi-lightning-charge-fill"></i>
                        <span>Phase 01 &bull; Real-Time Capture</span>
                    </div>
                    <h2 class="section-title">
                        Harness Cashflow Before It Dissipates.
                    </h2>
                    <p class="section-desc">
                        Unmanaged money evaporates through micro-leakages and neglected subscriptions. As capital enters your ecosystem, our intelligent engine categorizes, audits, and channels every cent into high-impact allocations.
                    </p>
                    <div style="display: flex; gap: 16px; flex-wrap: wrap;">
                        <div class="badge-pill gold" style="margin-bottom: 0;">
                            <i class="bi bi-check2-circle"></i>
                            <span>Zero-Latency Classification</span>
                        </div>
                        <div class="badge-pill" style="margin-bottom: 0;">
                            <i class="bi bi-graph-up-arrow"></i>
                            <span>+24.8% Savings Velocity</span>
                        </div>
                    </div>
                </div>

                <!-- Interactive Glass Card Mockup -->
                <div class="glass-card">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                        <h4 style="font-size: 1.1rem; font-weight: 700; color: #ffffff; margin: 0;">Live Cashflow Intake</h4>
                        <span style="font-size: 0.75rem; color: #34d399; font-weight: 600; background: rgba(16, 185, 129, 0.15); padding: 4px 10px; border-radius: 6px;">
                            ● STREAM ACTIVE
                        </span>
                    </div>

                    <div class="mockup-item">
                        <div style="display: flex; align-items: center;">
                            <div class="mockup-icon emerald">
                                <i class="bi bi-briefcase-fill"></i>
                            </div>
                            <div>
                                <div style="font-weight: 600; font-size: 0.95rem; color: #ffffff;">Consulting Revenue</div>
                                <div style="font-size: 0.78rem; color: var(--text-muted);">Direct Wire &bull; Instant Clear</div>
                            </div>
                        </div>
                        <div style="text-align: right;">
                            <div style="font-weight: 700; color: #34d399; font-size: 1rem;">+$4,500.00</div>
                            <div style="font-size: 0.72rem; color: #34d399;">100% Routed</div>
                        </div>
                    </div>

                    <div class="mockup-item">
                        <div style="display: flex; align-items: center;">
                            <div class="mockup-icon gold">
                                <i class="bi bi-pie-chart-fill"></i>
                            </div>
                            <div>
                                <div style="font-weight: 600; font-size: 0.95rem; color: #ffffff;">Dividends & Yield</div>
                                <div style="font-size: 0.78rem; color: var(--text-muted);">Treasury & ETFs &bull; Compounded</div>
                            </div>
                        </div>
                        <div style="text-align: right;">
                            <div style="font-weight: 700; color: #fbbf24; font-size: 1rem;">+$628.40</div>
                            <div style="font-size: 0.72rem; color: #fbbf24;">Reinvested</div>
                        </div>
                    </div>

                    <div class="mockup-item">
                        <div style="display: flex; align-items: center;">
                            <div class="mockup-icon purple">
                                <i class="bi bi-shield-shaded"></i>
                            </div>
                            <div>
                                <div style="font-weight: 600; font-size: 0.95rem; color: #ffffff;">Redundant SaaS Canceled</div>
                                <div style="font-size: 0.78rem; color: var(--text-muted);">Leak Detection Triggered</div>
                            </div>
                        </div>
                        <div style="text-align: right;">
                            <div style="font-weight: 700; color: #c084fc; font-size: 1rem;">+$184/mo</div>
                            <div style="font-size: 0.72rem; color: #c084fc;">Recovered</div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- ----------------------------------------------------------------
             PHASE 02 (45% - 70% Scroll: Constriction Neck / Strict Guardrails)
             ---------------------------------------------------------------- -->
        <section id="guardrails" class="scrolly-section">
            <div class="section-split">
                <!-- Interactive Glass Card: Budget & Encryption -->
                <div class="glass-card gold-hover">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
                        <h4 style="font-size: 1.1rem; font-weight: 700; color: #ffffff; margin: 0;">Autonomous Budget Guardrails</h4>
                        <span class="badge-pill gold" style="margin: 0; padding: 4px 10px; font-size: 0.72rem;">
                            TRACKABLE PROTOCOL
                        </span>
                    </div>

                    <!-- Category 1 -->
                    <div style="margin-bottom: 20px;">
                        <div style="display: flex; justify-content: space-between; font-size: 0.88rem; margin-bottom: 4px;">
                            <span style="color: #ffffff; font-weight: 500;">Living & Infrastructure</span>
                            <span style="color: #34d399; font-weight: 600;">$1,650 / $2,800 (58%)</span>
                        </div>
                        <div class="budget-bar-track">
                            <div class="budget-bar-fill safe" style="width: 58%;"></div>
                        </div>
                    </div>

                    <!-- Category 2 -->
                    <div style="margin-bottom: 20px;">
                        <div style="display: flex; justify-content: space-between; font-size: 0.88rem; margin-bottom: 4px;">
                            <span style="color: #ffffff; font-weight: 500;">Discretionary Dining & Entertainment</span>
                            <span style="color: #fbbf24; font-weight: 600;">$780 / $950 (82% Warning)</span>
                        </div>
                        <div class="budget-bar-track">
                            <div class="budget-bar-fill warning" style="width: 82%;"></div>
                        </div>
                    </div>

                    <!-- Category 3 -->
                    <div style="margin-bottom: 24px;">
                        <div style="display: flex; justify-content: space-between; font-size: 0.88rem; margin-bottom: 4px;">
                            <span style="color: #ffffff; font-weight: 500;">Emergency Reserve Allocation</span>
                            <span style="color: #34d399; font-weight: 600;">$1,200 / $1,200 (100% Target)</span>
                        </div>
                        <div class="budget-bar-track">
                            <div class="budget-bar-fill safe" style="width: 100%;"></div>
                        </div>
                    </div>

                    <div style="padding: 16px; background: rgba(0, 0, 0, 0.4); border-radius: 12px; border: 1px solid rgba(255, 255, 255, 0.05); display: flex; align-items: center; gap: 14px;">
                        <i class="bi bi-key-fill text-warning fs-4"></i>
                        <div style="font-size: 0.8rem; color: var(--text-muted); line-height: 1.4;">
                            <strong style="color: #ffffff;">AES-256 Vault Encryption Active:</strong> Private transaction notes and memo payloads are encrypted at rest with Galois/Counter Mode.
                        </div>
                    </div>
                </div>

                <!-- Text Description -->
                <div>
                    <div class="badge-pill gold">
                        <i class="bi bi-hourglass-split"></i>
                        <span>Phase 02 &bull; The Constriction Point</span>
                    </div>
                    <h2 class="section-title">
                        Strict Discipline at the Narrow Neck.
                    </h2>
                    <p class="section-desc">
                        Just as the physical neck of the hourglass meters the descent of every single gold coin, our Trackable architecture enforces strict budget thresholds. Visual telemetry turns amber at 80% and red at 100%, giving you absolute immunity against lifestyle creep.
                    </p>
                    <div class="features-grid" style="grid-template-columns: 1fr; gap: 14px; margin-top: 20px;">
                        <div class="feature-box" style="padding: 18px;">
                            <div style="display: flex; align-items: center; gap: 14px;">
                                <div class="mockup-icon emerald" style="margin: 0;">
                                    <i class="bi bi-cpu"></i>
                                </div>
                                <div>
                                    <h5 style="color: #ffffff; font-size: 0.95rem; margin-bottom: 2px;">Multithreaded Background Workers</h5>
                                    <p style="color: var(--text-muted); font-size: 0.8rem; margin: 0;">ExecutorService thread pool processes asynchronous CSV audits without blocking UI threads.</p>
                                </div>
                            </div>
                        </div>
                        <div class="feature-box" style="padding: 18px;">
                            <div style="display: flex; align-items: center; gap: 14px;">
                                <div class="mockup-icon gold" style="margin: 0;">
                                    <i class="bi bi-lock-fill"></i>
                                </div>
                                <div>
                                    <h5 style="color: #ffffff; font-size: 0.95rem; margin-bottom: 2px;">Zero-Trust Session Hardening</h5>
                                    <p style="color: var(--text-muted); font-size: 0.8rem; margin: 0;">Automated 15-minute inactivity timeouts, HttpOnly cookie directives, and CSRF token verification.</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- ----------------------------------------------------------------
             PHASE 03 (75% - 90% Scroll: Lower Chamber Filling / Accumulation)
             ---------------------------------------------------------------- -->
        <section id="reservoir" class="scrolly-section section-center">
            <div class="badge-pill">
                <i class="bi bi-bank2"></i>
                <span>Phase 03 &bull; Compounding Reservoir</span>
            </div>

            <h2 class="section-title">
                The Lower Chamber Fills:<br>
                <span class="gradient-text-gold">Tangible Financial Sovereignty.</span>
            </h2>

            <p class="section-desc">
                Witness the power of disciplined accumulation. Every grain passed through the bottleneck now stacks firmly in your lower reservoir, turning finite working hours into permanent sovereign wealth.
            </p>

            <div class="features-grid" style="max-width: 1100px; margin: 30px auto 0 auto;">
                <!-- Milestone Card 1 -->
                <div class="glass-card">
                    <div class="feature-icon-wrapper" style="background: rgba(16, 185, 129, 0.15); color: #34d399;">
                        <i class="bi bi-shield-fill-check"></i>
                    </div>
                    <h3 style="font-size: 1.25rem; font-weight: 700; color: #ffffff; margin-bottom: 8px;">6-Month Runway Vault</h3>
                    <div style="font-size: 1.8rem; font-weight: 800; color: #34d399; margin-bottom: 12px;">$35,000</div>
                    <p style="color: var(--text-muted); font-size: 0.88rem; line-height: 1.5; margin-bottom: 16px;">
                        100% of liquid emergency requirements funded. Zero reliance on high-interest credit lines.
                    </p>
                    <div class="badge-pill" style="margin: 0; padding: 4px 12px; font-size: 0.72rem;">
                        Target Achieved
                    </div>
                </div>

                <!-- Milestone Card 2 -->
                <div class="glass-card gold-hover">
                    <div class="feature-icon-wrapper" style="background: rgba(245, 158, 11, 0.15); color: #fbbf24; border-color: rgba(245, 158, 11, 0.3);">
                        <i class="bi bi-graph-up-arrow"></i>
                    </div>
                    <h3 style="font-size: 1.25rem; font-weight: 700; color: #ffffff; margin-bottom: 8px;">Freedom Portfolio</h3>
                    <div style="font-size: 1.8rem; font-weight: 800; color: #fbbf24; margin-bottom: 12px;">+$64,280</div>
                    <p style="color: var(--text-muted); font-size: 0.88rem; line-height: 1.5; margin-bottom: 16px;">
                        Automated dollar-cost averaging into index funds and productive equities with +28.4% annualized yield.
                    </p>
                    <div class="badge-pill gold" style="margin: 0; padding: 4px 12px; font-size: 0.72rem;">
                        Compounding Exponentially
                    </div>
                </div>

                <!-- Milestone Card 3 -->
                <div class="glass-card">
                    <div class="feature-icon-wrapper" style="background: rgba(56, 189, 248, 0.15); color: #38bdf8; border-color: rgba(56, 189, 248, 0.3);">
                        <i class="bi bi-award-fill"></i>
                    </div>
                    <h3 style="font-size: 1.25rem; font-weight: 700; color: #ffffff; margin-bottom: 8px;">Debt Elimination Velocity</h3>
                    <div style="font-size: 1.8rem; font-weight: 800; color: #38bdf8; margin-bottom: 12px;">$0.00 Debt</div>
                    <p style="color: var(--text-muted); font-size: 0.88rem; line-height: 1.5; margin-bottom: 16px;">
                        Systematic debt payoff velocity achieved 11 months ahead of original amortized projections.
                    </p>
                    <div class="badge-pill" style="margin: 0; padding: 4px 12px; font-size: 0.72rem; color: #38bdf8; border-color: rgba(56, 189, 248, 0.3); background: rgba(56, 189, 248, 0.1);">
                        Zero Liability
                    </div>
                </div>
            </div>
        </section>

        <!-- ----------------------------------------------------------------
             GRAND FINALE (95% - 100% Scroll: Chamber Fully Transferred / Sovereignty)
             ---------------------------------------------------------------- -->
        <section id="cta" class="scrolly-section section-center" style="min-height: 90vh;">
            <div class="grand-cta-card">
                <div class="badge-pill gold" style="margin-bottom: 16px;">
                    <i class="bi bi-infinity"></i>
                    <span>The Hourglass Never Halts. Neither Does Your Growth.</span>
                </div>

                <h2 style="font-size: clamp(2rem, 5vw, 3.5rem); font-weight: 800; color: #ffffff; line-height: 1.15; margin-bottom: 20px;">
                    Take Complete Command of Your<br>
                    <span class="gradient-text-emerald">Financial Horizon.</span>
                </h2>

                <p style="color: var(--text-muted); font-size: 1.15rem; max-width: 600px; margin: 0 auto 36px auto; line-height: 1.6;">
                    Deploy bank-level encryption, real-time analytics, and automated budget safeguards for your financial ecosystem today.
                </p>

                <div class="hero-cta-group">
                    <c:choose>
                        <c:when test="${sessionScope.currentUser != null}">
                            <a href="${pageContext.request.contextPath}/user/dashboard" class="btn-hero-primary" style="padding: 16px 40px; font-size: 1.1rem;">
                                <i class="bi bi-speedometer2"></i>
                                <span>Go to Your Dashboard</span>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/register" class="btn-hero-primary" style="padding: 16px 40px; font-size: 1.1rem;">
                                <i class="bi bi-person-plus-fill"></i>
                                <span>Create Free Account</span>
                            </a>
                            <a href="${pageContext.request.contextPath}/login" class="btn-hero-secondary" style="padding: 16px 36px; font-size: 1.1rem;">
                                <i class="bi bi-box-arrow-in-right"></i>
                                <span>Sign In to Platform</span>
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div style="display: flex; justify-content: center; gap: 28px; margin-top: 36px; color: var(--text-subtle); font-size: 0.82rem; flex-wrap: wrap;">
                    <span><i class="bi bi-check2 text-success me-1"></i> AES-256 Encrypted</span>
                    <span><i class="bi bi-check2 text-success me-1"></i> No Credit Card Required</span>
                    <span><i class="bi bi-check2 text-success me-1"></i> Jakarta EE &amp; Tomcat 10.1</span>
                </div>
            </div>
        </section>

    </main>

    <!-- ====================================================================
         INTERACTIVE TIMELINE HUD (Heads-Up Display)
         ==================================================================== -->
    <aside class="timeline-hud" aria-label="Playback Controls and Timeline HUD">
        <span style="letter-spacing: 0.05em; font-weight: 600; color: var(--text-subtle);">TIMELINE</span>
        <span id="hudPercent" class="hud-progress-pill">00%</span>
        <span style="color: rgba(255, 255, 255, 0.2);">|</span>
        <span id="hudTime" class="hud-timestamp">0.00s / 4.09s</span>
        <span style="color: rgba(255, 255, 255, 0.2);">|</span>
        <button id="hudRewindBtn" class="hud-btn-rewind" title="Rewind to Top (Smooth Glide)" aria-label="Scroll to Top">
            <i class="bi bi-chevron-up"></i>
        </button>
    </aside>

    <!-- ====================================================================
         FOOTER
         ==================================================================== -->
    <footer class="site-footer">
        <div style="max-width: 1200px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 16px;">
            <div style="display: flex; align-items: center; gap: 10px;">
                <i class="bi bi-hourglass-split text-success"></i>
                <span style="color: #ffffff; font-weight: 600;">FinanceFlow Platform</span>
                <span style="color: var(--text-subtle);">&bull; Full-Stack Enterprise Edition</span>
            </div>
            <div>
                <span style="color: #10b981; margin-right: 14px;">● System Online</span>
                <span>&copy; 2026 FinanceFlow Inc. All rights reserved.</span>
            </div>
        </div>
    </footer>

    <!-- Scrollytelling Video Playback Engine -->
    <script src="${pageContext.request.contextPath}/js/scrollytelling.js"></script>
</body>
</html>
