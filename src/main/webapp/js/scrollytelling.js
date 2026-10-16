/**
 * ============================================================================
 * FINANCEFLOW - HIGH-PERFORMANCE SCROLL-DRIVEN VIDEO ENGINE (SCROLLYTELLING)
 * ============================================================================
 * Features:
 * - Linear Interpolation (LERP) for buttery smooth, liquid inertia playback
 * - requestAnimationFrame render loop with hardware acceleration
 * - Smart Seeking Lock & Pending Target Buffer to eliminate browser decode jitter
 * - Forward & Reverse scroll synchronization
 * - Mobile / iOS WebKit video decoder priming
 * - Real-time Progress HUD and Dynamic Navigation Indicator
 * ============================================================================
 */

(function () {
    'use strict';

    // Configuration Constants
    const LERP_FACTOR = 0.08;          // Smoothing interpolation factor (0.05 = heavier inertia, 0.12 = snappier)
    const SEEK_THRESHOLD = 0.008;       // Delta in seconds before firing seek (avoids micro-thrashing)
    const DEFAULT_VIDEO_DURATION = 4.09;// Fallback duration if metadata delayed (matches 46185.mp4)

    // DOM Element References
    const video = document.getElementById('hourglassVideo');
    const progressBar = document.getElementById('scrollProgressBar');
    const hudPercent = document.getElementById('hudPercent');
    const hudTime = document.getElementById('hudTime');
    const hudRewindBtn = document.getElementById('hudRewindBtn');

    // Engine State
    let targetProgress = 0;             // Target scroll progress (0.0 to 1.0)
    let currentProgress = 0;            // Interpolated current progress (0.0 to 1.0)
    let videoDuration = DEFAULT_VIDEO_DURATION;
    let isVideoReady = false;
    let pendingSeekTime = null;         // Holds next target time if browser is actively decoding previous seek
    let seekWatchdog = null;            // Safety timer in case browser hangs on seeking state
    let isPrimed = false;

    /**
     * Compute current normalized scroll progress [0.0 - 1.0]
     */
    function calculateScrollProgress() {
        const scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;
        const scrollHeight = document.documentElement.scrollHeight || document.body.scrollHeight || 1;
        const clientHeight = window.innerHeight || document.documentElement.clientHeight || 1;
        const maxScroll = Math.max(1, scrollHeight - clientHeight);
        
        const rawProgress = scrollTop / maxScroll;
        return Math.min(Math.max(rawProgress, 0), 1);
    }

    /**
     * Update target progress on window scroll / resize
     */
    function onScrollUpdate() {
        targetProgress = calculateScrollProgress();
    }

    /**
     * Prime video decoder on modern mobile and strict browser environments
     * Muted inline video requires a subtle initial play/pause handshake
     */
    function primeVideoEngine() {
        if (isPrimed || !video) return;
        isPrimed = true;

        video.muted = true;
        video.playsInline = true;

        const playPromise = video.play();
        if (playPromise !== undefined) {
            playPromise.then(() => {
                video.pause();
                syncVideoToProgress(currentProgress);
            }).catch(() => {
                // Policy prevented autoplay; seeking will activate on scroll
            });
        }
    }

    /**
     * Safely apply seek time to the video element without dropping frames
     */
    function applyVideoTime(desiredTime) {
        if (!video || !isVideoReady || isNaN(desiredTime)) return;

        const safeTime = Math.min(Math.max(desiredTime, 0), Math.max(0, videoDuration - 0.001));

        // Skip seeking if delta is below human visual threshold (< 8 milliseconds)
        if (Math.abs(video.currentTime - safeTime) < SEEK_THRESHOLD) {
            return;
        }

        // If the video decoder is already busy decoding a prior frame,
        // buffer this target time until 'seeked' triggers
        if (video.seeking) {
            pendingSeekTime = safeTime;

            // Watchdog fallback: unstick seek lock if browser drops 'seeked' event
            if (!seekWatchdog) {
                seekWatchdog = setTimeout(() => {
                    seekWatchdog = null;
                    if (video && pendingSeekTime !== null) {
                        try {
                            video.currentTime = pendingSeekTime;
                            pendingSeekTime = null;
                        } catch (e) {}
                    }
                }, 80);
            }
            return;
        }

        try {
            video.currentTime = safeTime;
        } catch (err) {
            // Video element might still be attaching to media pipeline
        }
    }

    /**
     * Synchronize video playback time with given normalized progress [0.0 - 1.0]
     */
    function syncVideoToProgress(progress) {
        if (videoDuration > 0) {
            const computedTime = progress * videoDuration;
            applyVideoTime(computedTime);
        }
    }

    /**
     * Update UI indicators (Top Progress Bar & Bottom-Right Timeline HUD)
     */
    function updateHUD(progress) {
        const percentInt = Math.round(progress * 100);
        
        if (progressBar) {
            progressBar.style.width = `${progress * 100}%`;
        }

        if (hudPercent) {
            hudPercent.textContent = `${String(percentInt).padStart(2, '0')}%`;
        }

        if (hudTime && videoDuration > 0) {
            const timeSeconds = (progress * videoDuration).toFixed(2);
            hudTime.textContent = `${timeSeconds}s / ${videoDuration.toFixed(2)}s`;
        }
    }

    /**
     * High-Performance requestAnimationFrame Render Loop
     * Runs continuously at monitor refresh rate (60Hz / 120Hz / 144Hz)
     */
    function renderEngineLoop() {
        // Linear Interpolation: current = current + (target - current) * LERP_FACTOR
        const delta = targetProgress - currentProgress;
        
        if (Math.abs(delta) > 0.0001) {
            currentProgress += delta * LERP_FACTOR;
        } else {
            currentProgress = targetProgress;
        }

        // Apply synchronized time to video
        syncVideoToProgress(currentProgress);

        // Update HUD
        updateHUD(currentProgress);

        // Schedule next animation frame
        requestAnimationFrame(renderEngineLoop);
    }

    /**
     * Initialization & Lifecycle Listeners
     */
    function init() {
        // UI layers always run (reveal, INR counters, mobile nav) even if video is unavailable
        initScrollReveal();
        initInrCounters();
        initMobileNav();
        initLandingFlash();
        initNavLoadingFeedback();

        if (hudRewindBtn) {
            hudRewindBtn.addEventListener('click', (e) => {
                e.preventDefault();
                window.scrollTo({ top: 0, behavior: 'smooth' });
            });
        }

        window.addEventListener('scroll', onScrollUpdate, { passive: true });
        window.addEventListener('resize', onScrollUpdate, { passive: true });

        if (!video) {
            // Still update progress bar / HUD from scroll without video sync
            targetProgress = calculateScrollProgress();
            currentProgress = targetProgress;
            requestAnimationFrame(renderEngineLoop);
            return;
        }

        // Force proper properties to prevent unwanted autoplay or audio conflicts
        video.autoplay = false;
        video.loop = false;
        video.muted = true;
        video.playsInline = true;
        video.setAttribute('webkit-playsinline', 'true');
        video.pause();

        function onMetadataReady() {
            if (video.duration && !isNaN(video.duration) && video.duration > 0) {
                videoDuration = video.duration;
            }
            isVideoReady = true;
            targetProgress = calculateScrollProgress();
            currentProgress = targetProgress;
            applyVideoTime(currentProgress * videoDuration);
            updateHUD(currentProgress);
        }

        if (video.readyState >= 1) {
            onMetadataReady();
        } else {
            video.addEventListener('loadedmetadata', onMetadataReady, { once: true });
            video.addEventListener('canplay', onMetadataReady, { once: true });
        }

        // Listen for completed seek operation to flush any queued pending seek
        video.addEventListener('seeked', () => {
            if (seekWatchdog) {
                clearTimeout(seekWatchdog);
                seekWatchdog = null;
            }

            if (pendingSeekTime !== null) {
                const nextTime = pendingSeekTime;
                pendingSeekTime = null;
                applyVideoTime(nextTime);
            }
        });

        // User gestures to prime decoder
        ['click', 'touchstart', 'scroll', 'keydown'].forEach(evt => {
            window.addEventListener(evt, primeVideoEngine, { once: true, passive: true });
        });

        // Kick off the continuous RAF loop
        targetProgress = calculateScrollProgress();
        currentProgress = targetProgress;
        requestAnimationFrame(renderEngineLoop);
    }

    /**
     * Fade-in sections as they enter the viewport (scrollytelling narrative pacing)
     */
    function initScrollReveal() {
        const nodes = document.querySelectorAll('[data-reveal]');
        if (!nodes.length || !('IntersectionObserver' in window)) {
            nodes.forEach((el) => el.classList.add('is-visible'));
            return;
        }

        const observer = new IntersectionObserver((entries) => {
            entries.forEach((entry) => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('is-visible');
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.18, rootMargin: '0px 0px -8% 0px' });

        nodes.forEach((el) => observer.observe(el));
    }

    /**
     * Format integer as Indian-grouped INR string (₹1,25,000)
     */
    function formatInr(amount) {
        const n = Math.round(Math.abs(amount));
        const str = String(n);
        if (str.length <= 3) {
            return '₹' + str;
        }
        const last3 = str.slice(-3);
        let rest = str.slice(0, -3);
        const parts = [];
        while (rest.length > 2) {
            parts.unshift(rest.slice(-2));
            rest = rest.slice(0, -2);
        }
        if (rest.length) {
            parts.unshift(rest);
        }
        return '₹' + parts.join(',') + ',' + last3;
    }

    /**
     * Animate [data-inr-count] values when the INR section scrolls into view
     */
    function initInrCounters() {
        const counters = document.querySelectorAll('[data-inr-count]');
        if (!counters.length) return;

        const animate = (el) => {
            if (el.dataset.counted === '1') return;
            el.dataset.counted = '1';
            const target = parseInt(el.getAttribute('data-inr-count'), 10) || 0;
            const duration = 1200;
            const start = performance.now();

            function frame(now) {
                const t = Math.min(1, (now - start) / duration);
                const eased = 1 - Math.pow(1 - t, 3);
                el.textContent = formatInr(Math.round(target * eased));
                if (t < 1) {
                    requestAnimationFrame(frame);
                } else {
                    el.textContent = formatInr(target);
                }
            }
            requestAnimationFrame(frame);
        };

        if (!('IntersectionObserver' in window)) {
            counters.forEach(animate);
            return;
        }

        const observer = new IntersectionObserver((entries) => {
            entries.forEach((entry) => {
                if (entry.isIntersecting) {
                    animate(entry.target);
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.4 });

        counters.forEach((el) => observer.observe(el));
    }

    /**
     * Mobile hamburger drawer for section navigation
     */
    function initMobileNav() {
        const toggle = document.getElementById('navMobileToggle');
        const drawer = document.getElementById('mobileNavDrawer');
        if (!toggle || !drawer) return;

        function setOpen(open) {
            drawer.classList.toggle('is-open', open);
            drawer.setAttribute('aria-hidden', open ? 'false' : 'true');
            toggle.setAttribute('aria-expanded', open ? 'true' : 'false');
            toggle.querySelector('i').className = open ? 'bi bi-x-lg' : 'bi bi-list';
        }

        toggle.addEventListener('click', () => {
            setOpen(!drawer.classList.contains('is-open'));
        });

        drawer.querySelectorAll('a').forEach((link) => {
            link.addEventListener('click', () => setOpen(false));
        });
    }

    function initLandingFlash() {
        const flash = document.getElementById('landingFlash');
        const closeBtn = document.getElementById('landingFlashClose');
        if (!flash) return;

        const dismiss = () => {
            flash.style.opacity = '0';
            flash.style.transform = 'translateX(-50%) translateY(-8px)';
            setTimeout(() => flash.remove(), 280);
        };

        if (closeBtn) {
            closeBtn.addEventListener('click', dismiss);
        }
        setTimeout(dismiss, 6000);
    }

    /**
     * Visual feedback when navigating to login/register (helps during Render cold starts).
     */
    function initNavLoadingFeedback() {
        const links = document.querySelectorAll('a.js-nav-loading');
        if (!links.length) return;

        links.forEach((link) => {
            link.addEventListener('click', function (e) {
                if (e.metaKey || e.ctrlKey || e.shiftKey || e.altKey || e.button !== 0) {
                    return;
                }
                if (link.classList.contains('is-loading')) {
                    e.preventDefault();
                    return;
                }

                const loadingText = link.getAttribute('data-loading-text') || 'Loading…';
                link.classList.add('is-loading');
                link.setAttribute('aria-busy', 'true');
                link.setAttribute('aria-disabled', 'true');
                link.dataset.originalHtml = link.innerHTML;
                link.innerHTML =
                    '<span class="nav-btn-spinner" aria-hidden="true"></span>' +
                    '<span>' + loadingText + '</span>';
                // Navigation continues; spinner stays visible until the next page paints
            });
        });
    }

    // Initialize when DOM content is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
