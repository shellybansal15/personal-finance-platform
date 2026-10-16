/**
 * Online Personal Finance Management Platform - Frontend Application Script
 * Features: Dark Mode Toggle (localStorage), Mobile Sidebar Drawer, Auto-dismiss Alerts
 */

function formatInr(value) {
    const n = Number(value);
    if (Number.isNaN(n)) {
        return '₹0.00';
    }
    return '₹' + n.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

document.addEventListener('DOMContentLoaded', function () {
    // 1. Dark Mode / Light Mode Theme Engine
    const themeToggleBtn = document.getElementById('themeToggleBtn');
    const htmlElement = document.documentElement;

    function applyTheme(theme) {
        if (theme === 'dark') {
            htmlElement.setAttribute('data-bs-theme', 'dark');
            if (themeToggleBtn) {
                themeToggleBtn.innerHTML = '<i class="bi bi-sun-fill text-warning"></i>';
                themeToggleBtn.setAttribute('title', 'Switch to Light Mode');
            }
        } else {
            htmlElement.setAttribute('data-bs-theme', 'light');
            if (themeToggleBtn) {
                themeToggleBtn.innerHTML = '<i class="bi bi-moon-stars-fill text-success"></i>';
                themeToggleBtn.setAttribute('title', 'Switch to Dark Mode');
            }
        }
        localStorage.setItem('finance_app_theme', theme);
    }

    // Initialize saved theme or default to light
    const savedTheme = localStorage.getItem('finance_app_theme') || 'light';
    applyTheme(savedTheme);

    if (themeToggleBtn) {
        themeToggleBtn.addEventListener('click', function () {
            const currentTheme = htmlElement.getAttribute('data-bs-theme') || 'light';
            const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
            applyTheme(newTheme);
            // Trigger chart redraw if charts exist
            if (window.renderAllCharts) {
                window.renderAllCharts();
            }
        });
    }

    // 2. Mobile Responsive Sidebar Drawer
    const sidebarToggleBtn = document.getElementById('sidebarToggleBtn');
    const sidebar = document.getElementById('appSidebar');
    const sidebarOverlay = document.getElementById('sidebarOverlay');

    if (sidebarToggleBtn && sidebar) {
        sidebarToggleBtn.addEventListener('click', function () {
            sidebar.classList.toggle('show');
            if (sidebarOverlay) sidebarOverlay.classList.toggle('show');
        });
    }

    if (sidebarOverlay && sidebar) {
        sidebarOverlay.addEventListener('click', function () {
            sidebar.classList.remove('show');
            sidebarOverlay.classList.remove('show');
        });
    }

    // 3. Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) bsAlert.close();
        }, 5000);
    });

    // 4. Generic loading state for forms marked with data-loading-form
    document.querySelectorAll('form[data-loading-form]').forEach(function (form) {
        form.addEventListener('submit', function () {
            const btn = form.querySelector('.btn-auth-submit, button[type="submit"]');
            if (!btn || btn.disabled) {
                return;
            }
            btn.disabled = true;
            btn.classList.add('is-loading');
            btn.setAttribute('aria-busy', 'true');
            const hint = form.querySelector('.auth-loading-hint');
            if (hint) {
                hint.classList.remove('d-none');
            }
        });
    });
});
